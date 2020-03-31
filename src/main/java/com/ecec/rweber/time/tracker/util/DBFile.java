package com.ecec.rweber.time.tracker.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

public class DBFile {
	private static final String DEFAULT_DB = "resources/activities.db"; //location of the default DB file
	private static final String TEMPLATE_DB = "resources/activities_default.db"; //the the location of the DB template file
	
	private Logger m_log = null;
	private final File m_dbFile = new File("resources/db.conf");
	
	public DBFile() {
		m_log =  Logger.getLogger(this.getClass());
		
		if(!m_dbFile.exists())
		{
			//save the default database location
			this.saveDatabaseLocation(new File(DBFile.DEFAULT_DB));
		}
		
		//copy the database template file if it's not already there
		this.createDatabase(this.getDatabaseLocation());
	}
	
	private boolean createDatabase(File location) {
		boolean result = true; 
		
		if(!location.exists())
		{
			//if not copy a default database
			File templateFile = new File(DBFile.TEMPLATE_DB);
		
			try {
				m_log.info("creating DB template file at: " + location);
				Files.copy(templateFile.toPath(), location.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {

				m_log.error("error creating template file");
				e.printStackTrace();
				
				result = false; //fail operation
			}
		}
		
		return result;
	}
	
	public File getDatabaseLocation() {
		File dbFile = null; //the location of the DB file read from the text file
		
		try {
			
			byte[] encoded = Files.readAllBytes(Paths.get(m_dbFile.getAbsolutePath()));
			dbFile = new File(new String(encoded, Charset.defaultCharset()));
			 
		} catch (IOException e) {
			m_log.error("Cannot read custom DB file path");
			e.printStackTrace();
			
			//reset to the default
			dbFile = new File(DBFile.DEFAULT_DB);
		}
		
		return dbFile;
	}
	
	public boolean saveDatabaseLocation(File dbLocation) {
		boolean result = true;

		//if the db.conf files is missing create it, otherwise make sure the location is different
		if(!m_dbFile.exists() || !dbLocation.equals(this.getDatabaseLocation()))
		{
			result = this.createDatabase(dbLocation);
			
			if(result)
			{
				try {
					//save the location of the DB file
					BufferedWriter writer = new BufferedWriter(new FileWriter(m_dbFile));
					
					writer.write(dbLocation.getAbsolutePath());
					
					writer.flush();
					writer.close();
				}
				catch(Exception e)
				{
					//save the error but not worth crashing the program here
					m_log.error("Error writing DB location file: " + m_dbFile);
					e.printStackTrace();
					
					result = false;
				}
			}
		}
		
		return result;
	}
}
