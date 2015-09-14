package com.ecec.rweber.time.tracker;

import java.util.Date;
import java.util.Map;

import com.ecec.rweber.time.tracker.util.CSVWriteable;
import com.ecec.rweber.time.tracker.util.CSVWriter;

public class Log implements CSVWriteable{
	private long m_start = 0;
	private long m_end = 0;
	private String m_activity = null;
	private String m_description = null;
	
	public Log(Map<String,String> map){
		m_start = Long.parseLong(map.get("start"));
		m_end = Long.parseLong(map.get("end"));
		m_activity = map.get("activity");
		m_description = map.get("description");
		
		if(m_description == null || m_description.equals("null")){
			m_description = "";
		}
	}
	
	public Log(){
		
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
	
	@Override
	public String formatCSV() {
		return CSVWriter.makeCSV(new String[]{this.m_activity,this.getStartDate().toString(),this.getEndDate().toString(), this.getTotal() + "",this.getDescription()});
	}
}
