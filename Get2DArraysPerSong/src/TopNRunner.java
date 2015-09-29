import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class TopNRunner {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
		String lookupFile = args[2];
		String s3folder = args[3];
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Top 40 Per Country");
		job.addCacheFile(new URI(lookupFile + "#theFile0"));
		//Iterate through directory
		File[] files = new File(s3folder).listFiles();
		// NULL POINTER EXCEPTION
		if (files != null) {
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				job.addCacheFile(new URI(file + "#theFile" + (i+1)));
			}
		}
		} else if (!s3folder.isEmpty()){
			job.addCacheFile(new URI(s3folder+"#theFile1"));
		}
		
		job.setJarByClass(TopNRunner.class);
		job.setMapperClass(TopNMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileInputFormat.setInputDirRecursive(job, true);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
	}
}
