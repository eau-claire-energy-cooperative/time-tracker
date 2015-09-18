package com.ecec.rweber.time.tracker.util;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
	public static final int MILLISECONDS = 0;
	public static final int SECONDS = 1;
	public static final int MINUTES = 2;
	public static final int HOURS = 3;
	public static final int DAYS = 4;
	
	public static String toString(int format){
		String result = "";
		
		switch(format){
			case MILLISECONDS:
				result = "milliseconds";
				break;
			case SECONDS:
				result = "seconds";
				break;
			case MINUTES:
				result = "minutes";
				break;
			case HOURS:
				result = "hours";
				break;
			case DAYS:
				result = "days";
				break;
		}
		
		return result;
	}
	
	public static final int guessBestFormat(long time, int currentFormat){
		int result = MILLISECONDS;
		
		//first convert this to milliseconds
		double start = TimeFormatter.format(time, currentFormat, TimeFormatter.MILLISECONDS);
		
		if(TimeFormatter.format((long)start, TimeFormatter.MILLISECONDS, TimeFormatter.SECONDS) > 1)
		{
			//we have at least one second
			result = SECONDS;
			start = TimeFormatter.format(time, result, TimeFormatter.SECONDS);
			
			if(TimeFormatter.format((long)start, TimeFormatter.MILLISECONDS, TimeFormatter.MINUTES) > 1)
			{
				//we have at least one minute
				result = MINUTES;
				start = TimeFormatter.format(time, result, TimeFormatter.MINUTES);
				
				if(TimeFormatter.format((long)start, TimeFormatter.MILLISECONDS, TimeFormatter.HOURS) > 1)
				{
					//we have at least one hour
					result = HOURS;
					start = TimeFormatter.format(time, result, TimeFormatter.HOURS);
					
					if(TimeFormatter.format((long)start, TimeFormatter.MILLISECONDS, TimeFormatter.DAYS) > 1)
					{
						//we have at least one day
						result = DAYS;
						start = TimeFormatter.format(time, result, TimeFormatter.DAYS);
					}
				}
			}
		}
		
		return result;
	}
	
	public static final double format(long start, long end,int format){
		//first get the elapsed time
		long milliseconds = end - start;
		
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
				result = (double)start/1000;
				break;
			case TimeFormatter.MINUTES:
				result = (double)start/1000/60;
				break;
			case TimeFormatter.HOURS:
				result = (double)start/1000/60/60;
				break;
			case TimeFormatter.DAYS:
				result = (double)start/1000/60/60/24;
				break;
		}
		
		return Double.parseDouble(formatter.format(result));
		
	}
}
