import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class SongWritable implements WritableComparable<SongWritable> {

	// String rep of variables
	public String identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
	
	// Comparison Variable
	public double hotness = 0;

	public SongWritable() {
		// String rep of variables
		identifyingData = ""; //Title\tArtist\tLatitude\tLongitude\tCountry
		
		// Comparison Variable
		hotness = 0;
	}

	public SongWritable(String identifyingData, double comparisonVariable) {
		this.identifyingData = identifyingData;

		this.hotness = comparisonVariable;
	}

	public SongWritable(SongWritable other) {
		this.identifyingData = other.identifyingData;
		
		// Comparison Variable
		this.hotness = other.hotness;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.identifyingData = arg0.readUTF();
		
		// Comparison Variable
		this.hotness = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(identifyingData);
		
		// Comparison Variable
		arg0.writeDouble(hotness);
	}

	@Override
	public int compareTo(SongWritable o) {
		return new Double(this.hotness).compareTo(o.hotness)*-1;
	}
	
	private String getFeatureData() {
		return "" + hotness;
	}

	@Override
	public String toString() {
		return identifyingData + "\t" + getFeatureData();
	}

}
