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
import com.ecec.rweber.time.tracker.upgrade.DatabaseUpgrade;
import com.ecec.rweber.time.tracker.util.DBFile;

public class ActivityManager {
	private final int DB_VERSION = 1;
	private Logger m_log = null;
	private DBFile m_dbFile = null;  //manages storage of the DB file location
	
	public ActivityManager(){
		m_log = Logger.getLogger(this.getClass());
		m_dbFile = new DBFile();
		
		this.updateDatabase();
	}

	@SuppressWarnings("deprecation")
	private void updateDatabase() {
		SQLDatasource database = this.loadDatabase();
		
		int dbVersion = 0; //assume version 0
		if(database.recordExists("select setting_name, setting_value from settings where setting_name = ?", "database_version"))
		{
			//check the version
			List<Map<String,String>> versionQuery = database.executeQuery("select setting_name, setting_value from settings where setting_name = ?", "database_version");

			dbVersion = Integer.parseInt(versionQuery.get(0).get("setting_value"));
		}
		
		DatabaseUpgrade upgrade = null;
		for(int i = dbVersion; i < DB_VERSION; i ++)
		{
			try {
				upgrade = (DatabaseUpgrade) Class.forName(DatabaseUpgrade.class.getPackageName() + ".Version" + (i + 1)).newInstance();
				
				upgrade.doUpgrade(database);
			}
			catch(Exception e)
			{
				//can't recover from this
				m_log.error("Error updating to Database Version: " + i);
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
	
	private SQLDatasource loadDatabase(){
		String dbFile = m_dbFile.getDatabaseLocation().getAbsolutePath();
		SQLDatasource result = null;
		
		m_log.debug("Loading DB file: " + dbFile);
		Map<String,String> props = new HashMap<String,String>();
		props.put("database",dbFile);
		props.put("schema_name",dbFile);
		props.put("username",dbFile);
		props.put("password",dbFile);
		
		result = new SQLiteDatasource("activities",DatasourceDrivers.getConnection("sql_lite", props));
		
		return result;
	}

	private List<Activity> loadActivities(){
		m_log.debug("loading activities");
		SQLDatasource database = this.loadDatabase();
		
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
		SQLDatasource database = this.loadDatabase();
		
		database.executeUpdate("insert into activities (name,description) values (?,?)", a.getName(), a.getDescription());
		
		database.disconnect();
	}
	
	private void saveLog(Log l){
		m_log.debug("Saving log " + l.getActivity());
		SQLDatasource database = this.loadDatabase();
		
		database.executeUpdate("insert into log (activity,start,end,description) values (?,?,?,?)",l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription());
		
		database.disconnect();
	}
	
	private void updateLog(Log l){
		m_log.debug("updating log: " + l.getId());
		SQLDatasource database = this.loadDatabase();
		
		database.executeUpdate("update log set activity = ?, start = ?, end = ?, description = ? where id = ?", l.getActivity(),l.getStartDate().getTime(),l.getEndDate().getTime(),l.getDescription(),l.getId());
		
		database.disconnect();
	}
	
	private String getSetting(String name) {
		String result = null;
		SQLDatasource database = this.loadDatabase();

		Map<String,String> sqlQuery = database.executeQueryGetFirst("select setting_value from settings where setting_name = ?", name);
		
		if(sqlQuery.containsKey("setting_value"))
		{
			result = sqlQuery.get("setting_value").toString();
		}
		
		database.disconnect();
		
		return result;
	}
	
	private void setSetting(String name, String value) {
		SQLDatasource database = this.loadDatabase();

		database.executeUpdate("update settings set setting_value = ? where setting_name = ?", value, name);
		
		database.disconnect();
	}
	
	public List<Log> generateReport(long startDate, long endDate){
		m_log.debug("Generating report: " + startDate + " to " + endDate);
		SQLDatasource database = this.loadDatabase();
		
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
		SQLDatasource database = this.loadDatabase();
		
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
		SQLDatasource database = this.loadDatabase();
		
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
		SQLDatasource database = this.loadDatabase();
		
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
	
	public Integer getMinTime() {
		return Integer.valueOf(this.getSetting("minimum_time"));
	}
	
	public void setMinTime(Integer minTime) {
		this.setSetting("minimum_time", minTime.toString());
	}
	
	public Integer getRoundTime() {
		return Integer.valueOf(this.getSetting("round_time"));
	}
	
	public void setRoundTime(Integer roundTime) {
		this.setSetting("round_time", roundTime.toString());
	}
	
	public File getDatabaseLocation() {
		return this.m_dbFile.getDatabaseLocation();
	}
	
	public boolean setDatabaseLocation(File f) {
		boolean result = this.m_dbFile.saveDatabaseLocation(f);
		
		if(result)
		{
			this.updateDatabase();
		}
		
		return result;
	}
}
