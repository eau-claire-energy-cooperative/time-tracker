package com.ecec.rweber.time.tracker.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CSVWriter extends ModelFormatter {
	private BufferedWriter writer = null;
	
	public CSVWriter(String file) throws IOException{
		writer = new BufferedWriter(new FileWriter(file));
	}
	
	@SuppressWarnings("rawtypes")
	public void writeData(DefaultTableModel model) throws IOException{
		
		//go through the column headers
		String cacheString = "";
		
		//first go through and record the column names
		for(int count = 0; count < model.getColumnCount(); count ++)
		{
			cacheString = cacheString + "\"" + model.getColumnName(count) + "\",";
		}
		
		cacheString = cacheString.substring(0,cacheString.length() - 1);

		writer.write(cacheString);
		writer.newLine();
		
		//go through each line of data
		Iterator iter = model.getDataVector().iterator();
		
		while(iter.hasNext())
		{
			cacheString = this.formatLine((Vector)iter.next(),"\"","\"",",");
			
			//write out the cacheString
			writer.write(cacheString);
			writer.newLine();
		}
		
		//close out everything
		writer.flush();
		writer.close();
	}
	
	
}
