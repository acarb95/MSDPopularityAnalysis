import geocode.ReverseGeoCode;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Top10Mapper extends Mapper<LongWritable, Text, Text, SongWritable> {
	public ReverseGeoCode reverseGeoCode;

	public static final Log LOG = LogFactory.getLog(Top10Mapper.class);
	
	public void setup(Context context) {
		try {
			reverseGeoCode = new ReverseGeoCode(context, "hdfs:///countries/part-r-00000", true, LOG);
		} catch (IOException | URISyntaxException e) {
			LOG.error("Error when creating reverseGeoCode object.");
			System.err.println("Error when creating reverseGeoCode object.");
			System.err.println("Message: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] split = value.toString().split("\t");

		if (!split[0].toLowerCase().contains("nan") && !split[8].toLowerCase().contains("nan") && split.length == 11) {
			String song_title = split[10];
			String artist = split[2];
			double latitude = Double.parseDouble(split[0]);
			double longitude = Double.parseDouble(split[1]);
			String location = reverseGeoCode.nearestPlace(latitude, longitude).country;
			double danceability = Double.parseDouble(split[3]);
			double energy = Double.parseDouble(split[4]);
			int song_key = Integer.parseInt(split[5]);
			double loudness = Double.parseDouble(split[6]);
			double tempo = Double.parseDouble(split[9]);
			int mode = Integer.parseInt(split[7]);
			Double hotness = Double.parseDouble(split[8]);
			
			if (hotness > 0) {
				context.write(new Text(location), new SongWritable(song_title,
						artist, latitude, longitude, location, danceability,
						energy, song_key, loudness, tempo, mode, hotness));
				
				//context.write(new Text(location), new Text(song_title + ":" + hotness));
			}
		}
	}
}
