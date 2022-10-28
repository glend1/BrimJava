package com.matthey.brimjava.loader.events;

import java.sql.SQLException;
import java.util.HashMap;

import com.matthey.brimjava.event.SqlFileEvent;
import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.event.util.TimeManipulation;
import com.matthey.brimjava.loader.util.EventGroups;
import com.matthey.brimjava.sql.util.Sql;

public class LoadFile {
	public static EventGroups loadAll(String evType, EventGroups events) {
		Sql sql = new Sql("plantavail");
		sql.query("select J_Event.id, J_Event.attachmentfk, J_Event.EventTypesFK, j_event.eventFk, j_event.typefk, "
				+ "j_eventfilesql.query, j_eventfilesql.tablecol, j_eventfilesql.path, "
				+ "cast(stuff(( "
				+ "select distinct '-' + convert(varchar(11), J_EventFileSqlDuration.EventFileSqlDurationTypeFK) + ',' "
				+ "+ convert(varchar(11), J_EventFileSqlDuration.value) from J_EventFileSqlDuration "
				+ "where EventFileSqlFK = j_eventfilesql.id for xml path('')),1,1,'') as varchar(max)) as arr "
				+ "from J_Event "
				+ "join J_EventTypes on J_EventTypes.id = J_Event.EventTypesFK "
				+ "join J_Type on J_Type.id = J_Event.TypeFK "
				+ "join j_eventfilesql on j_eventfilesql.id = J_Event.eventFk "
				+ "where J_EventTypes.Name = '" + evType + "' and J_Type.Name = 'File'");
		try {
			while(sql.getResult().next()) {
				TimeManipulation durations = new TimeManipulation(doubleSplit(sql.getResult().getString("arr")));
				events.add(sql.getResult().getInt("attachmentfk"), (GenericEvent) new SqlFileEvent(
						sql.getResult().getInt("id"), sql.getResult().getInt("attachmentfk"), 
						sql.getResult().getInt("EventTypesFK"), sql.getResult().getInt("EventFK"),
						sql.getResult().getInt("typefk"), sql.getResult().getString("tablecol"), 
						sql.getResult().getString("query"), sql.getResult().getString("path"),
						durations)
				);
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}			
		sql.close();
		return events;
	}
	private static HashMap<Integer, Integer> doubleSplit(String in) {
		HashMap<Integer, Integer> out = new HashMap<Integer, Integer>();
		if (in != null) {
			String[] split = in.split("-");
			String[] holder = null;
			for (String s : split) {
				holder = s.split(",");
				out.put(Integer.parseInt(holder[0]), Integer.parseInt(holder[1]));				
			}
			System.out.println("Query column was converted to an array");
		} else {
			System.out.println("Query column wasn't converted to an array");
		}
		return out;
	}
}