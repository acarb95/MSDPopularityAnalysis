import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class Fix2DArrays {

	public static void main(String[] args) {
		traverseDirectory(args[0], args[1]);
	}

	private static void traverseDirectory(String inputDir, String outputDir) {
		File inDir = new File(inputDir);
		File outDir = new File(outputDir);
		
		if (inDir.isDirectory()) {
			for (File file : inDir.listFiles()) {
				if (!file.isDirectory() && file.getName().endsWith(".txt")) {
					convertFile(file, new File(outDir, file.getName()));
				}
			}
		} else {
			convertFile(inDir, outDir);
		}
	}
	
	private static void convertFile(File inFile, File outFile) {
		Scanner reader = null;
		PrintWriter writer = null;
		
		try {
			reader = new Scanner(inFile);
			writer = new PrintWriter(outFile);
			
			while (reader.hasNext()) {
				String line = reader.nextLine();
				
				String[] split = line.split("\t");
				
				for (int i = 0; i < split.length; i++) {
					String part = split[i];
					if (i == 14 || i== 15) {
						part = convert2DArrayToProperForm(part);
					}
					writer.write(part + "\t");
				}
				
				writer.write("\n");
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
