package com.ecec.rweber.time.tracker;

import java.util.Map;

public class Activity implements Comparable<Activity>{
	private int m_id = -1;
	private String m_name = null;
	private String m_description = null;
	
	public Activity(Map<String,String> map){
		m_id = Integer.parseInt(map.get("id"));
		m_name = map.get("name");
		m_description = map.get("description");
	}
	
	public Activity(String name, String descrip){
		m_name = name;
		m_description = descrip;
	}
	
	public int getId(){
		return m_id;
	}
	
	public String getName(){
		return m_name;
	}
	
	public String getDescription(){
		return m_description;
	}
	
	@Override
	public String toString(){
		return this.getName();
	}

	@Override
	public int compareTo(Activity arg0) {
		return this.getName().compareTo(arg0.getName());
	}
}
