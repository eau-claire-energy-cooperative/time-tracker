package com.ecec.rweber.time.tracker.sql.upgrade;

import org.apache.log4j.Logger;

import com.ecec.rweber.time.tracker.sql.SQLDatasource;

public abstract class DatabaseUpgrade {
	private int m_version = 0;
	protected Logger m_log = null;
	
	public DatabaseUpgrade(int version) {
		m_log = Logger.getLogger(this.getClass().toString());
		
		m_version = version;
	}
	
	private void updateVersion(SQLDatasource database) {
		database.executeUpdate("update settings set setting_value = ? where setting_name = ?", m_version, "database_version");
	}
	
	protected abstract boolean upgradeSteps(SQLDatasource database);
	
	public void doUpgrade(SQLDatasource database) throws DatabaseUpgradeException {
		//perform the upgrade steps
		boolean result = this.upgradeSteps(database);
		
		if(result)
		{
			//update the version
			this.updateVersion(database);
		}
	}
}
