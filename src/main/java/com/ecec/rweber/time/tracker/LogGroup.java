package com.ecec.rweber.time.tracker;

import java.util.Map;

import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class LogGroup {
	private long m_milliseconds = 0;
	private String m_activity = null;
	
	public LogGroup(Map<String,String> map){
		m_activity = map.get("activity");
		m_milliseconds = Long.parseLong(map.get("milliseconds"));
	}
	
	public double getTotal(int format){
		return TimeFormatter.format(m_milliseconds, TimeFormatter.MILLISECONDS, format);
	}
	
	public String getActivity(){
		return m_activity;
	}
}
