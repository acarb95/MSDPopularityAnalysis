import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class SongWritable implements WritableComparable<SongWritable> {

	// String rep of variables
	public String identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
	public String featureData = ""; //barsStart\tbeatsStart\t...\tloudness\ttempo

	// // Identifiers
	public String title = "";
	// public String artist = "";

	// // Location Variables
	// public double latitude = 0;
	// public double longitude = 0;
	// public String country = "";

	// // Composition 1D Arrays
	// public float[] barsStart = null;
	// public float[] beatsStart = null;
	// public float[] sectionsStart = null;
	// public float[] segmentsMaxLoudness = null;
	// public float[] segmentsMaxLoudnessTime = null;
	// public float[] segmentsMaxLoudnessStart = null;
	// public float[] segmentsStart = null;
	// public float[] tatumsStart = null;
	
	// // Composition 2D Array
	// public float[][] timbre = null;
	// public float[][] pitches = null;
	
	// // Composition Integers
	// public int timeSignature = 0;
	// public int key = 0;
	// public int mode = 0;

	// // Composition Doubles/Floats
	// public float startOfFadeIn = 0;
	// public float duration = 0;
	// public float endOfFadeIn = 0;
	// public double danceability = 0;
	// public double energy = 0;
	// public double loudness = 0;
	// public double tempo = 0;
	
	// Comparison Variable
	public double hotness = 0;

	public SongWritable() {
		// String rep of variables
		identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
		featureData = ""; //barsStart\tbeatsStart\t...\tloudness\ttempo

		title = "";
		
		// Comparison Variable
		hotness = 0;
	}

	public SongWritable(String identifyingData, String featureData, double comparisonVariable) {
		this.identifyingData = identifyingData;
		this.featureData = featureData;
		
		title = identifyingData.split("\t")[0];

		this.hotness = comparisonVariable;
	}

	public SongWritable(SongWritable other) {
		this.identifyingData = other.identifyingData;
		this.featureData = other.featureData;
		
		this.title = other.title;
		
		// Comparison Variable
		this.hotness = other.hotness;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.identifyingData = arg0.readUTF();
		this.featureData = arg0.readUTF();
		
		this.title = arg0.readUTF();
		
		// Comparison Variable
		this.hotness = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(identifyingData);
		arg0.writeUTF(featureData);
		
		arg0.writeUTF(title);
		
		// Comparison Variable
		arg0.writeDouble(hotness);
	}

	@Override
	public int compareTo(SongWritable o) {
		return new Double(this.hotness).compareTo(o.hotness)*-1;
	}

	public String outputFeatures() {
		return identifyingData + "\t" + featureData;
	}

	@Override
	public String toString() {
		return identifyingData + "\t" + featureData;
	}

}
