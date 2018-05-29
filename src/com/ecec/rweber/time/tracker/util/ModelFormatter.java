package com.ecec.rweber.time.tracker.util;

import java.util.Vector;

public abstract class ModelFormatter {
	
	protected String formatLine(Vector data, String pre, String post, String delmit){
		String result = "";
		
		for(int count = 0; count < data.size(); count ++)
		{
			result = result + pre + data.get(count) + post + delmit;
		}
		
		result = result.substring(0,result.length() - 1);
		
		return result;
	}
}
