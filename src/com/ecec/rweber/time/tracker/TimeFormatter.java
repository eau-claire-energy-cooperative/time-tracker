package com.ecec.rweber.time.tracker;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
	public static final int MILLISECONDS = 0;
	public static final int SECONDS = 1;
	public static final int MINUTES = 2;
	public static final int HOURS = 3;
	public static final int DAYS = 4;
	
	public static final double format(long a, long b,int format){
		//first get the elapsed time
		long milliseconds = b - a;
		
		return TimeFormatter.format(milliseconds, TimeFormatter.MILLISECONDS, format);
		
	}
	
	public static final double format(long start, int current, int format){
		DecimalFormat formatter = new DecimalFormat("#.##");
		
		double result = (double)start;
		
		//first get milliseconds
		switch(current){
			case TimeFormatter.MILLISECONDS:
				//do nothing
				break;
			case TimeFormatter.SECONDS:
				start = TimeUnit.SECONDS.toMillis(start);
				break;
			case TimeFormatter.MINUTES:
				start = TimeUnit.MINUTES.toMillis(start);
				break;
			case TimeFormatter.HOURS:
				start = TimeUnit.HOURS.toMillis(start);
				break;
			case TimeFormatter.DAYS:
				start = TimeUnit.DAYS.toMillis(start);
				break;
		}
		
		//now get the ending conversion
		switch(format){
			case TimeFormatter.MILLISECONDS:
				//do nothing
				break;
			case TimeFormatter.SECONDS:
				result = TimeUnit.MILLISECONDS.toSeconds(start);
				break;
			case TimeFormatter.MINUTES:
				result = TimeUnit.MILLISECONDS.toMinutes(start);
				break;
			case TimeFormatter.HOURS:
				result = TimeUnit.MILLISECONDS.toHours(start);
				break;
			case TimeFormatter.DAYS:
				result = TimeUnit.MILLISECONDS.toDays(start);
				break;
		}
		System.out.println(result);
		return Double.parseDouble(formatter.format(result));
		
	}
}
