package com.ecec.rweber.time.tracker.sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SQLDatasource {
	//private variables
	private Logger log = null;
	
	//protected for sub classing
	protected Connection connect = null; //the server connection
	private String dbName = null;

	
	public SQLDatasource(String dbName, Connection connection){
		this.dbName = dbName;
		log = Logger.getLogger("SQLDatasource");
		
		connect = connection;
		
		this.connect();
	}
	
	public Map<String,String> executeQueryGetFirst(String statement, Object ...params){
		//execute the query
		List<Map<String,String>> result = this.executeQuery(statement, params);
		
		if(result.size() > 0)
		{
			return result.get(0);
		}
		else
		{
			return new HashMap<String,String>();
		}
	}
	
	public List<Map<String,String>> executeQuery(String statement, Object ... params){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		//first create the prepared statement
		PreparedStatement prep = this.createPrep(statement,params);
		
		try{
			
			ResultSet resultSet = prep.executeQuery();

			//get the metadata so we know how many columns
			ResultSetMetaData meta = resultSet.getMetaData();
			int total_columns = meta.getColumnCount();
			
			HashMap<String,String> tempMap = null;
			while(resultSet.next())
			{
				tempMap = new HashMap<String,String>();
				
				for(int count = 1; count <= total_columns; count ++)
				{
					tempMap.put(meta.getColumnName(count),resultSet.getString(count));
				}
				
				result.add(tempMap);
			}
			
			logDebug("Found " + result.size() + " rows with " + total_columns + " columns");
			
			//this will close the result set too
			prep.close();
		}
		catch(Exception e)
		{
			logError("Error executing query: " + statement);
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int executeUpdate(String statement, Object ... params){
		int result = 0;
	
		PreparedStatement prep = this.createPrep(statement, params);

		//execute as an update, not a query
		try{
			result = prep.executeUpdate();
			
			prep.close();
		}
		catch(Exception e)
		{
			logError("Error executing update: " + statement);
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean recordExists(String statement, Object ... params){
		boolean result = false;	//assume no record
		
		PreparedStatement prep = this.createPrep(statement, params);
		
		//execute the statement, return true if we found anything
		try{
			ResultSet resultSet = prep.executeQuery();
			
			if(resultSet.next())
			{
				result = true;
			}
			
			prep.close();
		}
		catch(Exception e)
		{
			logError("Error executing record check: " + statement);
			e.printStackTrace();
		}
		return result;
	}
	
	public void disconnect(){
		logDebug("killing connection");
		this.db_disconnect();
	}
	
	@Override
	public String toString(){
		return this.dbName;
	}

	protected PreparedStatement createPrep(String statement, Object...params){
		
		PreparedStatement prep = null;
		try{
			this.connect();
	
			prep = connect.prepareStatement(statement);
			
			for(int count = 0; count < params.length; count ++)
			{
				//set the objects into the prepared statement
				prep.setObject(count + 1, params[count]);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return prep;
	}
	
	
	
	private void db_disconnect(){
		
		if(connect != null)
		{
			try{
				connect.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	private void connect(){
		try{
			if(connect == null || connect.isClosed())
			{
				
				logError("Connection is closed");
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void logInfo(Object message){
		log.info(dbName + ": " + message);
	}
	
	protected void logError(Object message){
		log.error(dbName + ": " + message);
	}
	
	protected void logDebug(Object message){
		//only really works if debugging is enabled
		log.debug(dbName + ": " + message);
	}
}
