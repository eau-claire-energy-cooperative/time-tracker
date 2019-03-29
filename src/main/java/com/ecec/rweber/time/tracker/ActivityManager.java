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
	public static final String DEFAULT_DB = "resources/activities.db";
	
	private Logger m_log = null;
	private File m_dbFile = null;
	
	public ActivityManager(){
		this(ActivityManager.DEFAULT_DB);
	}
	
	public ActivityManager(String dbFile){
		m_log = Logger.getLogger(this.getClass());
		m_dbFile = new File(dbFile); 
	}
	
	private SQLDatasource loadDatabase(File dbFile){
		
		//first check if the database exists
		if(!dbFile.exists())
		{
			File defaultFile = new File("resources/activities_default.db");
			defaultFile.renameTo(dbFile);
		}
		
		SQLDatasource result = null;
		
		m_log.debug("Loading DB file: " + dbFile.getAbsolutePath());
		Map<String,String> props = new HashMap<String,String>();
		props.put("database",dbFile.getAbsolutePath());
		props.put("schema_name",dbFile.getAbsolutePath());
		props.put("username",dbFile.getAbsolutePath());
		props.put("password",dbFile.getAbsolutePath());
		
		result = new SQLiteDatasource("activities",DatasourceDrivers.getConnection("sql_lite", props));
		
		return result;
	}
	
	private List<Activity> loadActivities(){
		m_log.debug("loading activities");
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		List<Activity> result = new ArrayList<Activity>();
		
		Iterator<Map<String,String>> sqlQuery = database.executeQuery("select * from activities order by name asc").iterator();
		Map<String,String> aRow = null;
		
		while(sqlQuery.hasNext())
		{
			aRow = sqlQuery.next();
			
			result.add(new Activity(aRow));
		}
		
		database.disconnect();
		
		return result;
	}
	
	private void saveActivity(Activity a){
		m_log.debug("Saving activity: " + a.getName());
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		database.executeUpdate("insert into activities (name,description) values (?,?)", a.getName(), a.getDescription());
		
		database.disconnect();
	}
	
	private void saveLog(Log l){
		m_log.debug("Saving log " + l.getActivity());
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		database.executeUpdate("insert into log (activity,start,end,description) values (?,?,?,?)",l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription());
		
		database.disconnect();
	}
	
	private void updateLog(Log l){
		m_log.debug("updating log: " + l.getId());
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		database.executeUpdate("update log set activity = ?, start = ?, end = ?, description = ? where id = ?", l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription(),l.getId());
		
		database.disconnect();
	}
	
	public List<Log> generateReport(long startDate, long endDate){
		m_log.debug("Generating report: " + startDate + " to " + endDate);
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		List<Log> result = new ArrayList<Log>();
		
		Iterator<Map<String,String>> sqlQuery = database.executeQuery("select * from log where start > ? and start < ? order by start asc", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new Log(sqlQuery.next()));
		}
		
		database.disconnect();
		
		return result;
	}
	
	public List<LogGroup> generateGroupReport(long startDate, long endDate){
		m_log.debug("Generating report: " + startDate + " to " + endDate);
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		List<LogGroup> result = new ArrayList<LogGroup>();
	
		Iterator<Map<String,String>> sqlQuery = database.executeQuery("select sum(log.end - log.start) as milliseconds, activity from log where start > ? and start < ? group by activity order by activity asc", startDate, endDate).iterator();
		
		while(sqlQuery.hasNext())
		{
			result.add(new LogGroup(sqlQuery.next()));
		}
		
		database.disconnect();
		
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
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		database.executeUpdate("delete from log where id = ?", l.getId());
		
		database.disconnect();
	}
	
	public List<Activity> getActivities(){
		return this.loadActivities();
	}
	
	public Activity getActivity(int index){
		return this.getActivities().get(index);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setActivities(Vector table){
		SQLDatasource database = this.loadDatabase(m_dbFile);
		
		database.executeUpdate("delete from activities");
		
		Iterator<Vector> iter = table.iterator();
		Vector v = null;
		Activity act = null;
		
		while(iter.hasNext())
		{
			v = iter.next();
			
			act = new Activity(v.get(0).toString(),v.get(1).toString());
			
			this.saveActivity(act);
		}
		
		database.disconnect();
	}
}
