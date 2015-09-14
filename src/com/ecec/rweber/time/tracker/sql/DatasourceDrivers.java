package com.ecec.rweber.time.tracker.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

/*
 * This class encapsulates all the information about a particular database driver so that other classes don't have to know
 */
public class DatasourceDrivers {
	protected static final String MSACCESS = "sun.jdbc.odbc.JdbcOdbcDriver";
	protected  static final String MYSQL = "org.gjt.mm.mysql.Driver";
	protected  static final String SQLEXPRESS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	protected  static final String SQLITE = "org.sqlite.JDBC";
	protected  static final String SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	protected static String allDrivers(){
		return MSACCESS + "," + MYSQL + "," + SQLEXPRESS + "," + SQLITE + "," + SQLSERVER;
	}
	
	protected static String getDriver(String type){
		String result = null;
		
		if(type.equals("mysql"))
		{
			result = MYSQL;
		}
		else if (type.equals("access"))
		{
			result = MSACCESS;
		}
		else if (type.equals("sql_server"))
		{
			result = SQLSERVER;
		}
		else if(type.equals("sql_express"))
		{
			result = SQLEXPRESS;
		}
		else if(type.equals("sql_lite"))
		{
			result = SQLITE;
		}
		
		return result;
	}
	
	public static Connection getConnection(String type, Map<String,String> connectionProps){
		Connection result = null;
		
		//attempt to register the driver
		String driver = DatasourceDrivers.getDriver(type);
		try{
			Class.forName(driver);
			
			result = DriverManager.getConnection(DatasourceDrivers.getDriverURL(driver, connectionProps.get("database"), connectionProps.get("schema_name")),
					connectionProps.get("username"),connectionProps.get("password"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected static String getDriverURL(String dbType, String database, String schemaName){
		String result = "";
		
		if(dbType.equals(MSACCESS))
		{
			result = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	        result += database.trim() + ";DriverID=22;READONLY=false}";
		}
		else if(dbType.equals(MYSQL)){
			result = "jdbc:mysql://" + database + ":3306/" + schemaName;
		}
		else if(dbType.equals(SQLEXPRESS)){
			result = "jdbc:sqlserver://" + database + ";DatabaseName=" + schemaName;
		}
		else if(dbType.equals(SQLITE)){
			result = "jdbc:sqlite:" + database;
		}
		else if (dbType.equals(SQLSERVER)){
			result = "jdbc:sqlserver://" + database + ":1433;DatabaseName=" + schemaName;
		}
	
		return result;
	}
}
