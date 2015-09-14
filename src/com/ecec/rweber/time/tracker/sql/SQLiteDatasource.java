package com.ecec.rweber.time.tracker.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteDatasource extends SQLDatasource{

	public SQLiteDatasource(String dbName, Connection connection) {
		super(dbName, connection);
	}

	@Override
	protected PreparedStatement createPrep(String statement, Object... params) {
		//SQLite requires some different parameters to create the connection
		
		PreparedStatement prep = null;
		try{
	
			prep = connect.prepareStatement(statement,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.CLOSE_CURSORS_AT_COMMIT);
			
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

}
