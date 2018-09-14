package com.ecec.rweber.time.tracker;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ecec.rweber.time.tracker.sql.DatasourceDrivers;
import com.ecec.rweber.time.tracker.sql.SQLDatasource;
import com.ecec.rweber.time.tracker.sql.SQLiteDatasource;

public class ActivityManager {
	private Logger m_log = null;
	private SQLDatasource m_database = null;
	
	public ActivityManager(){
		m_log = Logger.getLogger(this.getClass());
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
		m_log.debug("loading activities");
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
		m_log.debug("Saving activity: " + a.getName());
		m_database.executeUpdate("insert into activities (name,description) values (?,?)", a.getName(), a.getDescription());
	}
	
	private void saveLog(Log l){
		m_log.debug("Saving log " + l.getActivity());
		m_database.executeUpdate("insert into log (activity,start,end,description) values (?,?,?,?)",l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription());
	}
	
	private void updateLog(Log l){
		m_log.debug("updating log: " + l.getId());
		m_database.executeUpdate("update log set activity = ?, start = ?, end = ?, description = ? where id = ?", l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription(),l.getId());
	}
	
	public List<Log> generateReport(long startDate, long endDate){
		m_log.debug("Generating report: " + startDate + " to " + endDate);
		List<Log> result = new ArrayList<Log>();
		
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select * from log where start > ? and start < ? order by start asc", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new Log(sqlQuery.next()));
		}
		
		return result;
	}
	
	public List<LogGroup> generateGroupReport(long startDate, long endDate){
		m_log.debug("Generating report: " + startDate + " to " + endDate);
		List<LogGroup> result = new ArrayList<LogGroup>();
	
		Iterator<Map<String,String>> sqlQuery = m_database.executeQuery("select sum(log.end - log.start) as milliseconds, activity from log where start > ? and start < ? group by activity order by activity asc", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new LogGroup(sqlQuery.next()));
		}
		
		return result;
	}
	
	public void saveEntries(List<Log> logs){
		Iterator<Log> iter = logs.iterator();
		
		while(iter.hasNext())
		{
			this.saveEntry(iter.next());
		}
	}
	
	public void  saveEntry(Log l){
		if(l.getId() != -1)
		{
			updateLog(l);
		}
		else
		{
			saveLog(l);
		}
	}
	
	public void deleteEntry(Log l){
		m_log.debug("deleting log " + l.getId());
		m_database.executeUpdate("delete from log where id = ?", l.getId());
	}
	
	public List<Activity> getActivities(){
		return this.loadActivities();
	}
	
	public Activity getActivity(int index){
		return this.getActivities().get(index);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
