package com.ecec.rweber.time.tracker;

import java.util.ArrayList;
import java.util.List;

import com.ecec.rweber.time.tracker.modifer.TimeModifier;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class ElapsedTimer implements Timer{
	private TimerState current_state = TimerState.IDLE;
	private long startTime = 0;
	private long stopTime = 0;
	
	public ElapsedTimer(){
		//do nothing, just wait
	}
	
	public ElapsedTimer(long start, long stop){
		//initialize right away
		current_state = TimerState.STOPPED;
		startTime = start;
		stopTime = stop;
	}
	
	public static ElapsedTimer[] splitTime(ElapsedTimer orig, int split){
		List<ElapsedTimer> timers = new ArrayList<ElapsedTimer>();
		
		//make sure we have a start/stop time
		if(orig.getState() == TimerState.STOPPED)
		{
			if(split > 1)
			{
				//get the first series of start/stop values (orig start, start/split)
				long startTime = orig.getStartTime();
				long stopTime = orig.getStartTime() + (long)(orig.getElapsedTime(TimeFormatter.MILLISECONDS)/split);
				ElapsedTimer temp = null;
				
				for(int count = 0; count < split; count ++)
				{
					temp = new ElapsedTimer(startTime,stopTime);
					
					//stop time becomes new startTime
					startTime = stopTime;
					
					//stopTime is old stopTime + elapsedTime
					stopTime = stopTime + (long)temp.getElapsedTime(TimeFormatter.MILLISECONDS);
					
					timers.add(temp);
				}
			}
			else
			{
				//nothing to split
				timers.add(orig);
			}
		}

		return timers.toArray(new ElapsedTimer[0]);
	}
	
	@Override
	public void start(){
		if(current_state == TimerState.IDLE || current_state == TimerState.STOPPED)
		{
			current_state = TimerState.RUNNING;
			startTime = System.currentTimeMillis();
			stopTime = 0;
		}
		else if (current_state == TimerState.RUNNING)
		{
			//reset the start time
			startTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public void stop(){
		if(current_state == TimerState.IDLE){
			startTime = 0;
			stopTime = 0;
			
			current_state = TimerState.STOPPED;
		}
		else if(current_state == TimerState.RUNNING)
		{
			stopTime = System.currentTimeMillis();
			current_state = TimerState.STOPPED;
		}
		else if(current_state == TimerState.STOPPED)
		{
			stopTime = 0;
			startTime = 0;
		}
	}
	
	public void stop(TimeModifier m) {
		//first stop the time normally
		this.stop();
		
		//modify the time if needed
		stopTime = m.modifyTime(this);
	}
	
	@Override
	public void reset(){
		if(current_state == TimerState.IDLE)
		{
			startTime = 0;
			stopTime = 0;
		}
		else if(current_state == TimerState.RUNNING || current_state == TimerState.STOPPED)
		{
			current_state = TimerState.IDLE;
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
	
	@Override
	public TimerState getState(){
		return current_state;
	}
	
	public double getElapsedTime(int format){
		double result = 0;
		
		if(current_state == TimerState.IDLE){
			result = 0;
		}
		else if(current_state == TimerState.RUNNING)
		{
			result = TimeFormatter.formatElapsed(startTime, System.currentTimeMillis(), format);
		}
		else 
		{
			result = TimeFormatter.formatElapsed(startTime, stopTime, format);
		}
		return result;
	}
	
	@Override
	public String toString(){
		String result = "";
		
		double total_time = this.getElapsedTime(TimeFormatter.MILLISECONDS);
		
		//figure out the best format for this time 
		int bestFormat = TimeFormatter.guessBestFormat((long)total_time,TimeFormatter.MILLISECONDS);
		
		result = TimeFormatter.format((long)total_time, TimeFormatter.MILLISECONDS, bestFormat) + " " + TimeFormatter.toString(bestFormat);
		
		return result;
	}
}
