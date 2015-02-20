package csvparse;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ReadPubs {
	
	final double NO_MATCH_FACTOR = 1.3;
	final double MATCH_FACTOR = 1;
	

	public void createDataSet(){
		
		String csvFileScopus = "Scopus_cleaned.csv";
		String csvFileDBLP = "DBLP_cleaned.csv";
		String csv = "DataSet.csv";
		
		try {
			FileWriter file = new FileWriter(csv);
			CSVWriter writer = new CSVWriter(file); 
			
			String[] headers= "DOI_SCOPUS,DOI_DBLP,DOI_SIM,TITLE,YEAR,JOURNAL, MATCH".split(",");
			writer.writeNext(headers);
			
			CSVReader reader = new CSVReader(new FileReader(csvFileScopus));
			CSVReader readerDBLP;
			reader.readNext();
			String [] nextLine;
		    String [] nextLineDBLP;
		    
		    while ((nextLine = reader.readNext()) != null) {
		        
		    	// nextLine[] is an array of values from the line
	        	readerDBLP = new CSVReader(new FileReader(csvFileDBLP));
				readerDBLP.readNext();
	        	while ((nextLineDBLP = readerDBLP.readNext()) != null) {
	        		
	        		int levenshteinDistanceDOI = Levenshtein.computeLevenshteinDistance(nextLine[0], nextLineDBLP[0]);
	        		
					int levenshteinDistanceTITLE = Levenshtein.computeLevenshteinDistance(nextLine[1], nextLineDBLP[1]);
					double levenshteinDistanceYEAR = factorSimilitudAno(MATCH_FACTOR, NO_MATCH_FACTOR, nextLine[2], nextLineDBLP[2]);
					int levenshteinDistanceJOURNAL = Levenshtein.computeLevenshteinDistance(nextLine[3], nextLineDBLP[3]);
					
					String classAttributeString = "FALSE";
	        		if (nextLine[0].equals(nextLineDBLP[0])) {
		        		//System.out.println( nextLine[0] + ": " + nextLine[1].length() + " | " + nextLineDBLP[1].length() + " LD: " + levenshteinDistanceTITLE);
		        		classAttributeString = "TRUE";
					}						
	        		System.out.println( nextLine[0] + ": " + nextLine[1].length() + " | " + nextLineDBLP[1].length() + " LD: " + levenshteinDistanceYEAR);

					String var= nextLine[0] + "," + nextLineDBLP[0] + "," + levenshteinDistanceDOI + "," +  levenshteinDistanceTITLE + "," + levenshteinDistanceYEAR + "," + levenshteinDistanceJOURNAL + "," + classAttributeString;
					String[] dataStrings= var.split(",");
					writer.writeNext(dataStrings);

		        }
	        	readerDBLP.close();
		    }
		    writer.close();
		    reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public double factorSimilitudAno(double factorMatch, double factorNoMatch, String yearScopus, String yearDBLP){
		
		double diference = Math.abs(Integer.parseInt(yearScopus) - Integer.parseInt(yearDBLP));
		
		if (diference <= 2) {
			diference = diference*factorMatch;
		}else if (diference > 2){
			diference = diference*factorNoMatch;
		}
		
		return diference;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadPubs readPubs = new ReadPubs();
		readPubs.createDataSet();
	}
}