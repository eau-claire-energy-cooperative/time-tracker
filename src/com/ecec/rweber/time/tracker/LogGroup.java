package com.ecec.rweber.time.tracker;

import java.util.Map;

public class LogGroup {
	private long m_minutes = 0;
	private String m_activity = null;
	
	public LogGroup(Map<String,String> map){
		m_activity = map.get("activity");
		m_minutes = Long.parseLong(map.get("minutes"));
	}
	
	public long getTotal(){
		return m_minutes;
	}
	
	public String getActivity(){
		return m_activity;
	}
}
