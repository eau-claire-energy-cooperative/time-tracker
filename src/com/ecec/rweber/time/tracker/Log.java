package com.ecec.rweber.time.tracker;

import java.util.Date;
import java.util.Map;


public class Log {
	private int m_id = -1;
	private long m_start = 0;
	private long m_end = 0;
	private String m_activity = null;
	private String m_description = null;
	
	public Log(Map<String,String> map){
		m_id = Integer.parseInt(map.get("id"));
		m_start = Long.parseLong(map.get("start"));
		m_end = Long.parseLong(map.get("end"));
		m_activity = map.get("activity");
		m_description = map.get("description");
		
		if(m_description == null || m_description.equals("null")){
			m_description = "";
		}
	}
	
	public Log(Activity a, Timer t){
		m_start = t.getStartTime();
		m_end = t.getStopTime();
		m_activity = a.getName();
	}
	
	public Log(Activity a, Timer t, String description){
		this(a,t);
		m_description = description;
	}
	
	public Date getStartDate(){
		return new Date(m_start);
	}
	
	public Date getEndDate(){
		return new Date(m_end);
	}

	public long getTotal(){
		return (m_end - m_start)/1000/60;
	}
	
	public String getActivity(){
		return m_activity;
	}

	public String getDescription(){
		return m_description;
	}
	
	public String getShortDescription(){
		String result = m_description;
		
		if(m_description.length() > 25)
		{
			result = m_description.substring(0,20) + "....";
		}
			
		return result;
	}
}
