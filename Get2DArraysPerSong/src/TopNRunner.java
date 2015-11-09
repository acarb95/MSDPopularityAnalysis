import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

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
		// String fileList = args[3];
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Top 100 Per Country");
		job.addCacheFile(new URI(lookupFile + "#theFile0"));
		//Iterate through directory
		//File[] files = new File(s3folder).listFiles();
//		ArrayList<String> files = getFiles(fileList);
//		if (!files.isEmpty()) {
//			for (int i = 0; i < files.size(); i++) {
//				String file = files.get(i);
//				job.addCacheFile(new URI(file + "#theFile" + (i+1)));
//			}
//		}
		
		job.addCacheFile(new URI("s3://honor-thesis-data/CATop100.txt#theFile1"));
		job.addCacheFile(new URI("s3://honor-thesis-data/DETop100.txt#theFile2"));
		job.addCacheFile(new URI("s3://honor-thesis-data/GBTop100.txt#theFile3"));
		job.addCacheFile(new URI("s3://honor-thesis-data/HRTop100.txt#theFile4"));
		job.addCacheFile(new URI("s3://honor-thesis-data/PTTop100.txt#theFile5"));
		job.addCacheFile(new URI("s3://honor-thesis-data/USTop100.txt#theFile6"));
		
		job.setNumReduceTasks(0);
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
	
	public static ArrayList<String> getFiles(String fileList) {
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			Scanner reader = new Scanner(new File(fileList));
			
			while(reader.hasNext()) {
				String line = reader.nextLine().trim();
				list.add("s3://honor-thesis-data/" + line);
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
}
