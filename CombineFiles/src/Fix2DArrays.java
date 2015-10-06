import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class Fix2DArrays {

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		File outfile = new File(args[1]);
		
		Scanner reader = null;
		PrintWriter writer = null;
		
		try {
		reader = new Scanner(file);
		
		String line = reader.nextLine();
		
		writer = new PrintWriter(outfile);
		
		String[] split = line.split("\t");
		
		for (int i = 0; i < split.length; i++) {
			String part = split[i];
			if (i == 38 || i==40) {
				part = convert2DArrayToProperForm(part);
			}
			writer.write(part + "\t");
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	
	private static String convert2DArrayToProperForm(String array)  {
		String proper = "";
		
		array = array.replaceAll("[\\[\\]]", "");
		
		String[] arraybits = array.split(", ");
		
		int size = arraybits.length;
		int rowSize = size/12;
		int colSize = 12;
		
		float[][] new2DArray = new float[rowSize][colSize];
		
		int stringCounter = 0;
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				new2DArray[row][col] = Float.parseFloat(arraybits[stringCounter]);
				stringCounter++;
			}
		}
		
		proper = Arrays.deepToString(new2DArray);
		
		return proper;
	}
}
