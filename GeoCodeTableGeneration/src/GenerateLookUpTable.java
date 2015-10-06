import geocode.ReverseGeoCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipInputStream;


public class GenerateLookUpTable {

	public static void main(String[] args) {
		try {
			System.out.println("Creating mapping...");
			HashMap<String, String> coordinateMapping = new HashMap<String, String>();
			System.out.println("Creating reverse geocode object (this may take a while)...");
			ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new ZipInputStream(new FileInputStream(new File(args[0]))), true);
			
			System.out.println("Reading in artist location file and adding each LAT, LONG to map.");
			Scanner reader = new Scanner(new File(args[1]));
			while (reader.hasNext()) {
				String line = reader.nextLine();
				String[] splitLine = line.split("<SEP>");
				String lat = splitLine[1];
				String longt = splitLine[2];
				
				String key = lat + "," + longt;
				
				coordinateMapping.put(key, "");
			}
				
			reader.close();
			
			System.out.println("Using reverseGeoCode to add country values to each key in the map...");
			for (String key : coordinateMapping.keySet()) {
				double lat = Double.parseDouble(key.split(",")[0]);
				double longt = Double.parseDouble(key.split(",")[1]);
				
				String country = reverseGeoCode.nearestPlace(lat, longt).country;
				
				coordinateMapping.put(key, country);
			}
			
			
			System.out.println("Outputting to file...");
			PrintWriter writer = new PrintWriter(new File(args[2]));
			for (String key : coordinateMapping.keySet()) {
				writer.println(key + "=" + coordinateMapping.get(key));
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
