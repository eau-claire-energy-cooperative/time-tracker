package com.ecec.rweber.time.tracker;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
	
	public int getElapsedMinutes(){
		int result = 0;
		
		if(current_state == IDLE){
			result = 0;
		}
		else if(current_state == RUNNING)
		{
			result = (int)((System.currentTimeMillis() - startTime)/1000)/60;
		}
		else 
		{
			result = (int)((stopTime - startTime)/1000)/60;
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		
		double total_time = 0;
		if(current_state == IDLE){
			return "Timer Idle";
		}
		else if(current_state == RUNNING)
		{
			total_time = ((double)(System.currentTimeMillis() - startTime)/1000);
		}
		else if(current_state == STOPPED)
		{	
			total_time = ((double)(stopTime - startTime)/1000);
		}
		
		//check if greater than 1 min
		NumberFormat df = DecimalFormat.getInstance();
		df.setMaximumFractionDigits(3);
		
		if(total_time > 60)
		{
			total_time = total_time / 60;
			
			result = df.format(total_time) + " minutes";
		}
		else
		{
			result = df.format(total_time) + " seconds";
		}
		
		return result;
	}
}
