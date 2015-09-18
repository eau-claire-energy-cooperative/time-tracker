package com.ecec.rweber.time.tracker;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class Timer {
	public static final int IDLE = 0;
	public static final int RUNNING = 1;
	public static final int STOPPED = 2;
	
	private int current_state = IDLE;
	private long startTime = 0;
	private long stopTime = 0;
	
	public Timer(){
		//do nothing, just wait
	}
	
	public void start(){
		if(current_state == IDLE || current_state == STOPPED)
		{
			current_state = RUNNING;
			startTime = System.currentTimeMillis();
			stopTime = 0;
		}
		else if (current_state == RUNNING)
		{
			//reset the start time
			startTime = System.currentTimeMillis();
		}
	}
	
	public void stop(){
		if(current_state == IDLE){
			startTime = 0;
			stopTime = 0;
			
			current_state = STOPPED;
		}
		else if(current_state == RUNNING)
		{
			stopTime = System.currentTimeMillis();
			current_state = STOPPED;
		}
		else if(current_state == STOPPED)
		{
			stopTime = 0;
			startTime = 0;
		}
	}
	
	public void reset(){
		if(current_state == IDLE)
		{
			startTime = 0;
			stopTime = 0;
		}
		else if(current_state == RUNNING || current_state == STOPPED)
		{
			current_state = IDLE;
			stopTime = 0; 
			startTime =0;
		}
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getStopTime(){
		return stopTime;
	}
	
	public int getState(){
		return current_state;
	}
	
	public double getElapsedTime(int format){
		double result = 0;
		
		if(current_state == IDLE){
			result = 0;
		}
		else if(current_state == RUNNING)
		{
			result = TimeFormatter.format(startTime, System.currentTimeMillis(), format);
		}
		else 
		{
			result = TimeFormatter.format(startTime, stopTime, format);
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		
		double total_time = this.getElapsedTime(TimeFormatter.MILLISECONDS);
		
		//figure out the best format for this time 
		int bestFormat = TimeFormatter.guessBestFormat((long)total_time,TimeFormatter.MILLISECONDS);
		
		result = TimeFormatter.format((long)total_time, TimeFormatter.MILLISECONDS, bestFormat) + " " + TimeFormatter.toString(bestFormat);
		
		return result;
	}
}
