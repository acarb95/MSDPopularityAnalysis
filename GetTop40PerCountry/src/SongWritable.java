import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class SongWritable implements WritableComparable<SongWritable> {

	// String rep of variables
	public String identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
	public String feature1DArray = "";
	public String timbre = "";
	public String pitches = "";
	public String featureIntegers = "";
	public String featureDoubles = "";

	// // Identifiers
	public String title = "";
	
	// Comparison Variable
	public double hotness = 0;

	public SongWritable() {
		// String rep of variables
		identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
		feature1DArray = "";
		pitches = "";
		timbre = "";
		featureIntegers = "";
		featureDoubles = "";
		title = "";
		
		// Comparison Variable
		hotness = 0;
	}

	public SongWritable(String identifyingData, String feature1DArray, String timbre, String pitches, String featureIntegers, String featureDoubles, double comparisonVariable) {
		this.identifyingData = identifyingData;
		this.feature1DArray = feature1DArray;
		this.timbre = timbre;
		this.pitches = pitches;
		this.featureIntegers = featureIntegers;
		this.featureDoubles = featureDoubles;
		
		title = identifyingData.split("\t")[0];

		this.hotness = comparisonVariable;
	}

	public SongWritable(SongWritable other) {
		this.identifyingData = other.identifyingData;
		this.feature1DArray = other.feature1DArray;
		this.timbre = other.timbre;
		this.pitches = other.pitches;
		this.featureIntegers = other.featureIntegers;
		this.featureDoubles = other.featureDoubles;
		
		this.title = other.title;
		
		// Comparison Variable
		this.hotness = other.hotness;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.identifyingData = arg0.readUTF();
		
		int arrayLength = arg0.readInt();
		byte[] featureArrayBytes = new byte[arrayLength];
		arg0.readFully(featureArrayBytes);
		this.feature1DArray = new String(featureArrayBytes);
		//this.feature1DArray = arg0.readUTF();
		
		int timbreLength = arg0.readInt();
		byte[] timbreBytes = new byte[timbreLength];
		arg0.readFully(timbreBytes);
		this.timbre = new String(timbreBytes);
		//this.timbre = arg0.readUTF();
		
		int pitchLength = arg0.readInt();
		byte[] pitchBytes = new byte[pitchLength];
		arg0.readFully(pitchBytes);
		this.pitches = new String(pitchBytes);
		//this.pitches = arg0.readUTF();
		
		this.featureIntegers = arg0.readUTF();
		this.featureDoubles = arg0.readUTF();
		
		this.title = arg0.readUTF();
		
		// Comparison Variable
		this.hotness = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(identifyingData);
		
		byte[] feature1DBytes = feature1DArray.getBytes();
		int feature1DLength = feature1DBytes.length;
		arg0.writeInt(feature1DLength);
		arg0.write(feature1DBytes);
		
		byte[] timbreArray = timbre.getBytes();
		int length = timbreArray.length;
		arg0.writeInt(length);
		arg0.write(timbreArray);
		
		byte[] pitchArray = pitches.getBytes();
		int pitchlength = pitchArray.length;
		arg0.writeInt(pitchlength);
		arg0.write(pitchArray);
		
		arg0.writeUTF(featureIntegers);
		arg0.writeUTF(featureDoubles);
		
		arg0.writeUTF(title);
		
		// Comparison Variable
		arg0.writeDouble(hotness);
	}

	@Override
	public int compareTo(SongWritable o) {
		return new Double(this.hotness).compareTo(o.hotness)*-1;
	}
	
	private String getFeatureData() {
		return feature1DArray + "\t" + featureIntegers + "\t" + featureDoubles;
	}
	
	public String outputFeatures() {
		return identifyingData + "\t" + getFeatureData();
	}

	@Override
	public String toString() {
		return identifyingData + "\t" + getFeatureData();
	}

}
