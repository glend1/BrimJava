package com.matthey.brimjava.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.matthey.brimjava.sql.util.Sql;

public class SqlFile extends Sql {
	public SqlFile(String database) {
		super(database);
	}
	public String resultToString() {
		StringBuilder sbout = new StringBuilder();
		StringBuilder sbmd = new StringBuilder();
		boolean resultsFound = false;
		try {
			int numColumns = getResult().getMetaData().getColumnCount();
			while ( getResult().next() ) {
				resultsFound = true;
				StringBuilder sb = new StringBuilder();
				String sep = new String();
		        for ( int i = 1 ; i <= numColumns ; i++ ) {
		        	sb.append(sep + getResult().getObject(i));
		        	sep = ", ";
		        }
		        sbout.append(sb + "\n");
		    }
			if (resultsFound) {
				String sepmd = new String();
				ResultSetMetaData rsmd = getResult().getMetaData();
				for ( int i = 1 ; i <= numColumns ; i++ ) {
					sbmd.append(sepmd + rsmd.getColumnName(i));
					sepmd = ", ";
				}
				sbmd.append("\n");
			}
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (resultsFound) {
			sbmd.append(sbout); 
		} else {
			return null;
		}
        return sbmd.toString();
	}
}
