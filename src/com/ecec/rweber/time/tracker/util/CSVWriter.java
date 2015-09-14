package com.ecec.rweber.time.tracker.util;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CSVWriter {
	private BufferedWriter writer = null;
	
	public CSVWriter(String file) throws IOException{
		writer = new BufferedWriter(new FileWriter(file));
	}
	
	public static String makeCSV(String[] data){
		String result = "";
		
		for(int count = 0; count < data.length; count ++)
		{
			result = result + "\"" + data[count] + "\",";
		}
		
		result = result.substring(0,result.length() - 1);
		
		return result;
	}
	
	public void writeData(String[] columns, List<? extends CSVWriteable> data) throws IOException{
		
		//go through the column headers
		String cacheString = "";
		
		//first go through and record the column names
		for(int count = 0; count < columns.length; count ++)
		{
			cacheString = cacheString + "\"" + columns[count] + "\",";
		}
		
		cacheString = cacheString.substring(0,cacheString.length() - 1);

		writer.write(cacheString);
		writer.newLine();
		
		//go through each line of data
		Iterator<? extends CSVWriteable> iter = data.iterator();
		while(iter.hasNext())
		{
			cacheString = iter.next().formatCSV();
			
			//write out the cacheString
			writer.write(cacheString);
			writer.newLine();
		}
		
		//close out everything
		writer.flush();
		writer.close();
	}
	
	
}
