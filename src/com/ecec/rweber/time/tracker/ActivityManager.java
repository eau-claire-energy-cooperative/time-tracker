package com.ecec.rweber.time.tracker;

import java.io.File;
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
		
		//first check if the database exists
		File dbFile = new File("resources/activities.db");
		
		if(!dbFile.exists())
		{
			File defaultFile = new File("resources/activities_default.db");
			defaultFile.renameTo(dbFile);
		}
		
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
		
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select * from activities order by name asc").iterator();
		Map<String,String> aRow = null;
		
		while(sqlQuery.hasNext())
		{
			aRow = sqlQuery.next();
			
			result.add(new Activity(aRow));
		}
		
		return result;
	}
	
	private void saveActivity(Activity a){
		m_database.executeUpdate("insert into activities (name,description) values (?,?)", a.getName(), a.getDescription());
	}
	
	private void saveLog(String act, String description, long startTime, long endTime){
		m_database.executeUpdate("insert into log (activity,start,end,description) values (?,?,?,?)",act,startTime,endTime,description);
	}
	
	public List<Log> generateReport(long startDate, long endDate){
		List<Log> result = new ArrayList<Log>();
		
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select * from log where start > ? and start < ? order by start asc", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new Log(sqlQuery.next()));
		}
		
		return result;
	}
	
	public void  doActivity(Log l){
		saveLog(l.getActivity(),l.getDescription(), l.getStartDate().getTime(), l.getEndDate().getTime());
	}
	
	public List<Activity> getActivities(){
		return this.loadActivities();
	}
	
	public Activity getActivity(int index){
		return this.getActivities().get(index);
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
