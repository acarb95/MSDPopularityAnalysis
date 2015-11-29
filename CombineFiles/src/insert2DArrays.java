import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;


public class insert2DArrays {

	public static void main(String[] args) {
		HashMap<String, String> top40Songs = new HashMap<String, String>();
		HashMap<String, String> finishedSongs = new HashMap<String, String>();
		String top40File = args[0];
		String arraysFile = args[1];
		
		try {
			Scanner reader = new Scanner(new File(top40File));
			
			while(reader.hasNext()) {
				String line = reader.nextLine();
				String[] splitLine = line.split("\t");
				String generatedData = "";
				String ident = splitLine[1] + "|" + splitLine[2] + "|" + splitLine[3] + "|" + splitLine[4] + "|" + splitLine[0];
				for (int i = 0; i < splitLine.length; i++) {
					if (i == splitLine.length - 1) {
						generatedData += splitLine[i];
					} else {
						generatedData += splitLine[i] + "\t";
					}
				}
				top40Songs.put(ident, generatedData);
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
			writer = new PrintWriter(new File(args[2]));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (reader.hasNext()) {
			String line = reader.nextLine();
			String[] split = line.split("\t");
			if (top40Songs.containsKey(split[0])) {
				String outputString = "";
				if (getLocation(split[0]).equals("US")) {
					outputString = top40Songs.get(split[0]).trim() + "\t" + split[1];
				} else {
					outputString = getLocation(split[0]) + "\t" + split[0].replaceAll("[|]", "\t") + "\t" + insert2DArrays(split[1], top40Songs.get(split[0]));
				}
				finishedSongs.put(split[0], outputString);
			}
		}
		
		for (String song : finishedSongs.keySet()) {
			writer.println(finishedSongs.get(song));
			System.out.println("Data length: " + finishedSongs.get(song).split("\t").length);
		}
		
		reader.close();
		writer.close();
	}
	
	private static String getLocation(String line) {
		return line.split("[|]")[4];
	}
	
	private static String insert2DArrays(String arrays, String data) {
		String[] dataList = data.split("[|]");
		String newArrays = arrays.replaceAll("[|]", "\t");
		
		String dataString = "";
		
		for (int i = 0; i < dataList.length; i++) {
			if (i != 7 && i != dataList.length - 1) {
				dataString += dataList[i] + "\t";
			} else if (i == dataList.length -1) {
				dataString += dataList[i];
			} else {
				// Insert 2D arrays right after the array at 7.
				dataString += dataList[i] + "\t" + newArrays + "\t";
			}
		}
		
		return dataString;
	}

}
