import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TopNMapper extends Mapper<LongWritable, Text, Text, SongWritable> {

	//Indexes:
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

	HashMap<String, String> geoCodeLookup = new HashMap<String, String>();

	protected void setup(Mapper<LongWritable, Text, Text, SongWritable>.Context context) throws IOException, InterruptedException {
		if (context.getCachedFiles() != null && context.getCachedFiles().length > 0) {
			File lookupTable = new File("./lookupTable.text");

			// Save into class variable
			Scanner reader = new Scanner(lookupTable);
			while (reader.hasNext()) {
				split = reader.nextLine().split("=");
				geoCodeLookup.add(split[0], split[1]);
			}

			reader.close();
		}

		super.setup(context);
	}
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] split = value.toString().split("\t");

		// TODO: Fix parsing for full song data
		// TODO: might be easier to not have a song writable object and just concat into a string and add to song writable.
		if (!split[latitudeIndex].toLowerCase().contains("nan") && !split[longitudeIndex].toLowerCase().contains("nan") && split.length == 54) {
			String song_title = split[songTitleIndex];
			String artist = split[artistNameIndex];
			double latitude = Double.parseDouble(split[latitudeIndex]);
			double longitude = Double.parseDouble(split[longitudeIndex]);
			
			// Implement new lookup table
			String location = "";//reverseGeoCode.nearestPlace(latitude, longitude).country;

			// Composition 1D Arrays
			String barsStart = split[barStartIndex];
			String beatsStart = split[beatsStartIndex];
			String sectionsStart = split[sectionsStartIndex];
			String segmentsMaxLoudness = split[segMaxLoudnessIndex];
			String segmentsMaxLoudnessTime = split[segMaxLoudTimeIndex];
			String segmentsMaxLoudnessStart = split[segMaxLoudStartIndex];
			String segmentsStart = split[segStartIndex];
			String tatumsStart = split[tatumsStartIndex];
			
			// Composition 2D Array
			String timbre = split[segTimbreIndex];
			String pitches = split[segPitchesIndex];
			
			// Composition Integers
			String timeSignature = split[timeSignatureIndex];
			String key = split[keyIndex];
			String mode = split[modeIndex];

			// Composition Doubles/Floats
			String startOfFadeOut = split[startOfFadeOutIndex];
			String duration = split[durationIndex];
			String endOfFadeIn = split[endOfFadeInIndex];
			String danceability = split[danceabilityIndex];
			String energy = split[energyIndex];
			String loudness = split[loudnessIndex];
			String tempo = split[tempoIndex];

			// This must be accurate for comparisons.
			Double hotness = Double.parseDouble(split[songHottnessIndex]);
			
			// Create identification string
			identString = song_title + "\t" + artist + "\t" + latitude + "\t" + longitude + "\t" + location;
			featureString = barsStart + "\t" + beatsStart + "\t" + sectionsStart + "\t" +segmentsMaxLoudness + "\t" + segmentsMaxLoudnessTime+ "\t" + segmentsMaxLoudnessStart + "\t" + segmentsStart + "\t" + tatumsStart + "\t" +timbre + "\t" + pitches + "\t" + timeSignature + "\t" +key + "\t" + mode + "\t" +startOfFadeOut + "\t" +duration + "\t" + endOfFadeIn + "\t" +danceability + "\t" + energy + "\t" +loudness + "\t" + tempo + "\t" + hotness;

			if (hotness > 0) {
				context.write(new Text(location), new SongWritable(identString, featureString, hottness));
			}
		}
	}
}
