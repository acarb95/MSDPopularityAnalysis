import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
<<<<<<< HEAD
import java.util.HashMap;
=======
>>>>>>> 1a77d6cdd07ce10e720364a6ad65fc93bb9f2208

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
<<<<<<< HEAD
		HashMap<String, SongWritable> set = new HashMap<String, SongWritable>();
		
		for (SongWritable val : values) {
			SongWritable song = new SongWritable(val);
			set.put(song.getIdentifyingData(), song);
		}
		
		ArrayList<SongWritable> list = new ArrayList<SongWritable>(set.values());
		
=======
		ArrayList<SongWritable> list = new ArrayList<SongWritable>();
		
		for (SongWritable val : values) {
			SongWritable song = new SongWritable(val);
			list.add(song);
		}
		
>>>>>>> 1a77d6cdd07ce10e720364a6ad65fc93bb9f2208
		Collections.sort(list);
		
		int length = list.size();
		
		if (length > 40){
			length = 40;
		}
		
		for (int i = 0; i < length; i++) {
			mos.write(new Text(list.get(i).identifyingData), new Text(list.get(i).getFeatureData()), key + "Top40Songs");
		}
	}
	
	public void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}
}
