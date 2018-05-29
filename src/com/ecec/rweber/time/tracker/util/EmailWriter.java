package com.ecec.rweber.time.tracker.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class EmailWriter extends ModelFormatter {
	
	public EmailWriter(){
		
	}
	
	public String writeData(DefaultTableModel model) throws IOException {
		String result = "";
		
		//go through each line of data
		Iterator iter = model.getDataVector().iterator();
				
		while(iter.hasNext())
		{
			//write line with newline at the end
			result = result + this.formatLine((Vector)iter.next(),"","","\t\t") + "\n";
		}
		
		return result;
	}
}
