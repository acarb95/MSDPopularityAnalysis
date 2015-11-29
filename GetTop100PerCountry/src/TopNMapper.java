import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TopNMapper extends Mapper<LongWritable, Text, Text, SongWritable> {

	//Indexes
	int latitudeIndex = 5;
	int longitudeIndex = 7;
	int artistNameIndex = 11;
	int songHottnessIndex = 42;
	int songTitleIndex = 50;

	HashMap<String, String> geoCodeLookup = new HashMap<String, String>();
	
	HashSet<String> countriesToGet = new HashSet<String>();

	protected void setup(Mapper<LongWritable, Text, Text, SongWritable>.Context context) throws IOException, InterruptedException {
		if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
			URI mappingFileUri = context.getCacheFiles()[0];
			
			if (mappingFileUri != null) {
				File file = new File("theFile1");
				
				Scanner reader = new Scanner(file);
				// Save into class variable
				while (reader.hasNext()) {
					String line = reader.nextLine();
					String[] split = line.split("=");
					geoCodeLookup.put(split[0], split[1]);
				}

				reader.close();
			}
		}
		
		super.setup(context);
	}
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] split = value.toString().split("\t");

		if (!split[latitudeIndex].toLowerCase().contains("nan") && !split[longitudeIndex].toLowerCase().contains("nan") && split.length == 54) {
			String song_title = split[songTitleIndex];
			String artist = split[artistNameIndex];
			double latitude = Double.parseDouble(split[latitudeIndex]);
			double longitude = Double.parseDouble(split[longitudeIndex]);
			
			// Implement new lookup table
			String location = geoCodeLookup.get(split[latitudeIndex] +"," + split[longitudeIndex]);

			// This must be accurate for comparisons.
			Double hotness = -1.0;
			try {
				hotness = Double.parseDouble(split[songHottnessIndex]);
			} catch (Exception e) {
				hotness = -1.0;
			}
			// Create identification string
			String identString = song_title + "\t" + artist + "\t" + latitude + "\t" + longitude + "\t" + location;

			if (hotness > 0 && location != null) {
				if (countriesToGet.contains(location)) {
					context.write(new Text(location), new SongWritable(identString, hotness));
				}
			} 
		}
	}
}
