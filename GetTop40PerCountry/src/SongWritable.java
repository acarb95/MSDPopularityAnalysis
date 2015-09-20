import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class SongWritable implements WritableComparable<SongWritable> {

	// Identifiers
	public String title = "";
	public String artist = "";

	// Location Variables
	public double latitude = 0;
	public double longitude = 0;
	public String country = "";

	// Composition Variables
	public double danceability = 0;
	public double energy = 0;
	public int key = 0;
	public double loudness = 0;
	public double tempo = 0;
	public int mode = 0;

	// Comparison Variable
	public double hotness = 0;

	public SongWritable() {
		title = "NaN";
		artist = "NaN";
		latitude = 0;
		longitude = 0;
		country = "NaN";
		danceability = 0;
		energy = 0;
		key = 0;
		loudness = 0;
		tempo = 0;
		mode = 0;
		hotness = 0;
	}

	public SongWritable(String title, String artist, double lat, double longi,
			String country_code, double dance, double energy, int key,
			double loudness, double temp, int mode, double hot) {
		this.title = title;
		this.artist = artist;
		latitude = lat;
		longitude = longi;
		country = country_code;
		danceability = dance;
		this.energy = energy;
		this.key = key;
		this.loudness = loudness;
		tempo = temp;
		this.mode = mode;
		hotness = hot;
	}

	public SongWritable(SongWritable other) {
		this.title = other.title;
		this.artist = other.artist;
		this.latitude = other.latitude;
		this.longitude = other.longitude;
		this.country = other.country;
		this.danceability = other.danceability;
		this.energy = other.energy;
		this.key = other.key;
		this.loudness = other.loudness;
		this.tempo = other.tempo;
		this.mode = other.mode;
		this.hotness = other.hotness;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		title = arg0.readUTF();
		artist = arg0.readUTF();
		latitude = arg0.readDouble();
		longitude = arg0.readDouble();
		country = arg0.readUTF();
		danceability = arg0.readDouble();
		energy = arg0.readDouble();
		key = arg0.readInt();
		loudness = arg0.readDouble();
		tempo = arg0.readDouble();
		mode = arg0.readInt();
		hotness = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(title);
		arg0.writeUTF(artist);
		arg0.writeDouble(latitude);
		arg0.writeDouble(longitude);
		arg0.writeUTF(country);
		arg0.writeDouble(danceability);
		arg0.writeDouble(energy);
		arg0.writeInt(key);
		arg0.writeDouble(loudness);
		arg0.writeDouble(tempo);
		arg0.writeInt(mode);
		arg0.writeDouble(hotness);
	}

	@Override
	public int compareTo(SongWritable o) {
		return new Double(this.hotness).compareTo(o.hotness)*-1;
	}

	public String outputFeatures() {
		String result = artist + "\t" + latitude + "\t" + longitude + "\t"
				+ danceability + "\t" + energy + "\t" + key + "\t" + loudness
				+ "\t" + tempo + "\t" + mode + "\t" + hotness;
		return result;
	}

	@Override
	public String toString() {
		String result = title + "\t" + artist + "\t" + country + "\t" + hotness;
		return result;
	}

}
