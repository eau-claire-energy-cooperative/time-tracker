package com.ecec.rweber.time.tracker.upgrade;

import com.ecec.rweber.time.tracker.sql.SQLDatasource;

public class Version1 extends DatabaseUpgrade {

	public Version1() {
		super(1);
	}

	@Override
	protected boolean upgradeSteps(SQLDatasource database) {
		m_log.info("Upgrading database to version 1");
		
		//create the settings table
		database.executeUpdate("CREATE TABLE \"settings\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT, \"setting_name\"	TEXT, \"setting_value\"	TEXT)");
		
		//add the values we need
		database.executeUpdate("insert into settings (setting_name, setting_value) values (?,?)", "database_version", "0");
		database.executeUpdate("insert into settings (setting_name, setting_value) values (?,?)", "minimum_time", "0");
		database.executeUpdate("insert into settings (setting_name, setting_value) values (?,?)", "round_time", "0");
		
		return true;
	}

}
