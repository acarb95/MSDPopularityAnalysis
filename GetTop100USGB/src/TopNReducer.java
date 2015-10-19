import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class TopNReducer extends Reducer<Text, SongWritable, Text, Text> {
	private MultipleOutputs<Text, Text> mos;
	
	public void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		mos = new MultipleOutputs<Text, Text>(context);
	}
	
	public void reduce(Text key, Iterable<SongWritable> values, Context context) throws IOException, InterruptedException {
		ArrayList<SongWritable> list = new ArrayList<SongWritable>();
		
		for (SongWritable val : values) {
			SongWritable song = new SongWritable(val);
			list.add(song);
		}
		
		Collections.sort(list);
		
		int length = list.size();
		
		if (length > 100){
			length = 100;
		}
		
		for (int i = 0; i < length; i++) {
			// new Text(Double.toString(list.get(i).hotness))
			mos.write(new Text(list.get(i).title), new Text(list.get(i).outputFeatures()), key + "Top100Songs");
		}
	}
	
	public void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}
}
