package com.ecec.rweber.time.tracker.modifer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.ecec.rweber.time.tracker.ElapsedTimer;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class MinimumsTimeModifier implements TimeModifier {
	private int m_minTime = 0;
	private int m_roundTime = 0;
	
	public MinimumsTimeModifier(int minTime, int roundTime) {
		this.m_minTime = minTime;
		this.m_roundTime = roundTime;
	}
	
	@Override
	public long modifyTime(ElapsedTimer t) {
		//get the current stop time
		long stopTime = t.getStopTime();
		
		//if we need to round
		if(m_roundTime > 0)
		{
			//get the actual minute of the stop time
			LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(stopTime), ZoneId.systemDefault());
			
			//figure out how much time to add to get to the next increment 
			//example 15 round time, 20 current time (20 / 15 = 2), equation is 15 * 2 (30) - currentTime (20) = 10 to add time
			long minutesToAdd = (m_roundTime * ((d.getMinute() / m_roundTime) + 1)) - d.getMinute();
			
			//convert that time to milliseconds
			double addTime = TimeFormatter.format(minutesToAdd, TimeFormatter.MINUTES, TimeFormatter.MILLISECONDS);
			
			stopTime = stopTime + (long)addTime;
		}
		
		//if we haven't reached the minimum time, do that
		if(m_minTime > 0)
		{
			double minTimeMillis = TimeFormatter.format(m_minTime, TimeFormatter.MINUTES, TimeFormatter.MILLISECONDS);
			
			if(stopTime - t.getStartTime() < minTimeMillis)
			{
				//figure out how many millseconds we're missing to get to the minTime
				long addTime = (long)minTimeMillis - (stopTime - t.getStartTime());
				
				//add this exact amount to the stop time
				stopTime = stopTime + addTime;
			}
		}
		
		return stopTime;
	}

}
