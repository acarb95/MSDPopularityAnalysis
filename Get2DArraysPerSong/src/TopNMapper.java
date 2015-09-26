import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class TopNMapper extends Mapper<LongWritable, Text, Text, Text> {

	//Indexes
	int latitudeIndex = 5;
	int longitudeIndex = 7;
	int artistNameIndex = 11;
	int segPitchesIndex = 38;
	int segTimbreIndex = 40;
	int songTitleIndex = 50;

	private HashMap<String, String> geoCodeLookup = new HashMap<String, String>();
	private HashSet<String> top40Songs = new HashSet<String>();
	private MultipleOutputs<Text, Text> mos;

	protected void setup(Context context) throws IOException, InterruptedException {
		if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
			URI[] uris = context.getCacheFiles();
			
			URI mappingFileuri = uris[0];
			if (mappingFileuri != null) {
				File file = new File("theFile");
				
				Scanner reader = new Scanner(file);
				// Save into class variable
	            while (reader.hasNext()) {
	            	String line = reader.nextLine();
					String[] split = line.split("=");
					geoCodeLookup.put(split[0], split[1]);
				}

				reader.close();
			}
			
			for (int i = 1; i < uris.length; i ++) {
				URI uri = uris[i];
				if (uri != null) {
					File file = new File("theFile");
					
					Scanner reader = new Scanner(file);
					// Save into class variable
		            while (reader.hasNext()) {
		            	String line = reader.nextLine();
						String[] split = line.split("\t");
						top40Songs.add(split[0]);
					}

					reader.close();
				}
			}
		}
		mos = new MultipleOutputs<Text, Text>(context);
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
			String location = geoCodeLookup.get(split[latitudeIndex] +"," + split[longitudeIndex]);//reverseGeoCode.nearestPlace(latitude, longitude).country;
			
			// Composition 2D Array
			String timbre = split[segTimbreIndex];
			String pitches = split[segPitchesIndex];
			
			// Create identification string
			String identString = song_title + "|" + artist + "|" + latitude + "|" + longitude + "|" + location;

			if (location != null && top40Songs.contains(identString)) {
					mos.write(new Text(identString), new Text(timbre + "|" + pitches), key+"Top40TimbrePitch");
			} 
		}
	}
	
	public void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}
}
