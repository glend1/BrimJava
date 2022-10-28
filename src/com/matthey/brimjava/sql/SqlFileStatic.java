package com.matthey.brimjava.sql;

import java.io.File;
import java.util.Date;

import com.matthey.brimjava.event.util.TimeManipulation;
import com.matthey.brimjava.util.DateToCal;
import com.matthey.brimjava.util.LongToSQLDate;
import com.matthey.brimjava.util.ToFile;

public class SqlFileStatic {
	public static void print(String table, String query, String path, String filename, TimeManipulation durations) {
		Date endDateTime = new Date();
		SqlFile sql = new SqlFile(table);
		boolean run = false;
		String prepared = null;
		if (query.contains("%1$2s") && query.contains("%2$2s")) { // %1$2s ect
			System.out.println("SQL query contains datetime requirements");
			String start = LongToSQLDate.convert(durations.calculate(DateToCal.convert(endDateTime)).getTime().getTime());
			String end = LongToSQLDate.convert(endDateTime.getTime());
			prepared = String.format(query, start, end);
			if (prepared.contains(start) && prepared.contains(end) && new File(path).exists()) {
				run = true;
			} else {
				System.out.println("Datetime for SQL query cannot be substituted");
			}
		} else {
			System.out.println("SQL query can run without modification");
			prepared = query;
			run = true;
		}
		if (run) {
			sql.query(prepared); 
			ToFile.location(path + filename + ".csv", sql.resultToString());
		} else {
			System.out.println("event cannot complete");
		}
	}
}
