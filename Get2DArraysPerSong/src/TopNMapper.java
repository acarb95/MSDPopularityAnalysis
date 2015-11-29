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
	int artistHottnessIndex = 3;
	int latitudeIndex = 5;
	int longitudeIndex = 7;
	int artistNameIndex = 11;
	int barStartIndex = 18;
	int beatsStartIndex = 20;
	int danceabilityIndex = 21;
	int durationIndex = 22;
	int endOfFadeInIndex = 23;
	int energyIndex = 24;
	int keyIndex = 25;
	int loudnessIndex = 27;
	int modeIndex = 28;
	int sectionsStartIndex = 33;
	int segMaxLoudnessIndex = 35;
	int segMaxLoudTimeIndex = 36;
	int segMaxLoudStartIndex = 37;
	int segPitchesIndex = 38;
	int segStartIndex = 39;
	int segTimbreIndex = 40;
	int songHottnessIndex = 42;
	int startOfFadeOutIndex = 44;
	int tatumsStartIndex = 46;
	int tempoIndex = 47;
	int timeSignatureIndex = 48;
	int songTitleIndex = 50;
	int yearIndex = 53;

	private HashMap<String, String> geoCodeLookup = new HashMap<String, String>();
	private HashSet<String> top100Songs = new HashSet<String>();
	private MultipleOutputs<Text, Text> mos;

	protected void setup(Context context) throws IOException, InterruptedException {
		if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
			URI[] uris = context.getCacheFiles();
			
			URI mappingFileuri = uris[0];
			if (mappingFileuri != null) {
				File file = new File("theFile0");
				
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
					String fileName = "theFile" + i;
					File file = new File(fileName);
					
					Scanner reader = new Scanner(file);
					// Save into class variable
		            while (reader.hasNext()) {
		            	String line = reader.nextLine();
						String[] split = line.split("\t");
						top100Songs.add(split[0]);
					}

					reader.close();
				} else {
					System.out.println("uri is null");
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
			String location = geoCodeLookup.get(split[latitudeIndex] +"," + split[longitudeIndex]);
			
			// Composition 2D Array
			String timbre = split[segTimbreIndex];
			String pitches = split[segPitchesIndex];
			
			String USData = "";
			if (location != null && location.equals("US")) {
				// Composition 1D Arrays
				String barsStart = split[barStartIndex];
				String beatsStart = split[beatsStartIndex];
				String sectionsStart = split[sectionsStartIndex];
				String segmentsMaxLoudness = split[segMaxLoudnessIndex];
				String segmentsMaxLoudnessTime = split[segMaxLoudTimeIndex];
				String segmentsMaxLoudnessStart = split[segMaxLoudStartIndex];
				String segmentsStart = split[segStartIndex];
				String tatumsStart = split[tatumsStartIndex];
				
				// Composition Integers
				String timeSignature = split[timeSignatureIndex];
				String songKey = split[keyIndex];
				String mode = split[modeIndex];

				// Composition Doubles/Floats
				String startOfFadeOut = split[startOfFadeOutIndex];
				String duration = split[durationIndex];
				String endOfFadeIn = split[endOfFadeInIndex];
				String danceability = split[danceabilityIndex];
				String energy = split[energyIndex];
				String loudness = split[loudnessIndex];
				String tempo = split[tempoIndex];
				
				String feature1DString = barsStart + "\t" + beatsStart + "\t" + sectionsStart + "\t" +segmentsMaxLoudness + "\t" + segmentsMaxLoudnessTime+ "\t" + segmentsMaxLoudnessStart + "\t" + segmentsStart + "\t"+ tatumsStart;
				String featureIntegers = timeSignature + "\t" +songKey + "\t" + mode;
				String featureDoubles = startOfFadeOut + "\t" +duration + "\t" + endOfFadeIn + "\t" +danceability + "\t" + energy + "\t" +loudness + "\t" + tempo;
				
				USData = feature1DString + "\t" + timbre + "\t" + pitches + "\t" + featureIntegers + "\t" + featureDoubles;
			}
			
			// Create identification string
			String identString = song_title + "|" + artist + "|" + latitude + "|" + longitude + "|" + location;

			if (top100Songs.contains(identString) || top100Songs.contains(song_title)) {
				Text data = new Text();
				if (location.equals("US")) {
					data = new Text(USData);
					identString = location + "\t" + song_title + "\t" + artist + "\t" + latitude + "\t" + longitude + "\t" + location;
				} else {
					data = new Text(timbre + "|" + pitches);
				}
				mos.write(new Text(identString), data, location+"Top100Data");
			} else {
				System.out.println(identString + " not in list.");
			}
		}
	}
	
	public void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}
}
