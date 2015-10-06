import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;


public class Runner {

	public static void main(String[] args) {
		HashMap<String, String> top40Songs = new HashMap<String, String>();
		String top40File = args[0];
		String arraysFile = args[1];
		
		try {
			Scanner reader = new Scanner(new File(top40File));
			
			while(reader.hasNext()) {
				String line = reader.nextLine();
				String[] splitLine = line.split("\t");
				top40Songs.put(splitLine[0], splitLine[1]);
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner reader=null;
		PrintWriter writer = null;
		try {
			reader = new Scanner(new File(arraysFile));
			writer = new PrintWriter(new File("./USTop40Songs.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (reader.hasNext()) {
			String line = reader.nextLine();
			String[] split = line.split("\t");
			if (top40Songs.containsKey(split[0])) {
				top40Songs.put(split[0], getLocation(split[0]) + "\t" + split[0].replaceAll("[|]", "\t") + "\t" + insert2DArrays(split[1], top40Songs.get(split[0])));
			}
		}
		
		for (String song : top40Songs.keySet()) {
			writer.println(top40Songs.get(song));
		}
		
		reader.close();
		writer.close();
	}
	
	private static String getLocation(String line) {
		return line.split("[|]")[4];
	}
	
	private static String insert2DArrays(String arrays, String data) {
		String[] dataList = data.split("[|]");
		String[] split = arrays.split("[|]");
		String timbre = split[0];
		String pitch = split[1];
		String newArrays = arrays.replaceAll("[|]", "\t");
		
		String dataString = "";
		
		for (int i = 0; i < dataList.length; i++) {
			if (i != 7 && i != dataList.length - 1) {
				dataString += dataList[i] + "\t";
			} else if (i == dataList.length -1) {
				dataString += dataList[i];
			} else {
				// Insert 2D arrays first
				dataString += newArrays + "\t";
			}
		}
		
		return dataString;
	}

}
