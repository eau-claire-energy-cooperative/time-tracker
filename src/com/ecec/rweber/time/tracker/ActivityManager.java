package com.ecec.rweber.time.tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ecec.rweber.time.tracker.sql.DatasourceDrivers;
import com.ecec.rweber.time.tracker.sql.SQLDatasource;
import com.ecec.rweber.time.tracker.sql.SQLiteDatasource;
import com.ecec.rweber.time.tracker.util.CSVWriter;

public class ActivityManager {
	private SQLDatasource m_database = null;
	
	public ActivityManager(){
		m_database = this.loadDatabase(); 
	}
	
	private SQLDatasource loadDatabase(){
		SQLDatasource result = null;
		
		Map<String,String> props = new HashMap<String,String>();
		props.put("database","resources/activities.db");
		props.put("schema_name","resources/activities.db");
		props.put("username","resources/activities.db");
		props.put("password","resources/activities.db");
		
		result = new SQLiteDatasource("activities",DatasourceDrivers.getConnection("sql_lite", props));
		
		return result;
	}
	
	private List<Activity> loadActivities(){
		List<Activity> result = new ArrayList<Activity>();
		
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select * from activities").iterator();
		Map<String,String> aRow = null;
		
		while(sqlQuery.hasNext())
		{
			aRow = sqlQuery.next();
			
			result.add(new Activity(aRow));
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	private void saveActivity(Activity a){
		m_database.executeUpdate("insert into activities (name,description) values (?,?)", a.getName(), a.getDescription());
	}
	
	private void saveLog(Activity act, long startTime, long endTime){
		m_database.executeUpdate("insert into log (activity,start,end) values (?,?,?)", act.getName(),startTime,endTime);
	}
	
	public List<Log> generateReport(long startDate, long endDate){
		List<Log> result = new ArrayList<Log>();
		
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select * from log where start > ? and start < ?", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new Log(sqlQuery.next()));
		}
		
		return result;
	}
	
	public boolean saveReport(String filename, long startDate, long endDate){
		boolean result = true;
		
		//generate the report like normal
		List<Log> report = this.generateReport(startDate, endDate);
		
		//create the CSVWriter
		CSVWriter writer;
		try {
			writer = new CSVWriter(filename);
			writer.writeData(new String[]{"Activity","Start Date", "End Date","Total Minutes","Description"}, report);
			
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	public void  doActivity(int actIndex, Timer t){
		Activity a = this.getActivities().get(actIndex);
		
		saveLog(a,t.getStartTime(),t.getStopTime());
	}
	
	public List<Activity> getActivities(){
		return this.loadActivities();
	}
	
	public void setActivities(Vector table){
		
		m_database.executeUpdate("delete from activities");
		
		Iterator<Vector> iter = table.iterator();
		Vector v = null;
		Activity act = null;
		
		while(iter.hasNext())
		{
			v = iter.next();
			
			act = new Activity(v.get(0).toString(),v.get(1).toString());
			
			this.saveActivity(act);
		}
	}
}
