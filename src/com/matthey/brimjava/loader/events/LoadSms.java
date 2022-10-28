package com.matthey.brimjava.loader.events;

import java.sql.SQLException;

import com.matthey.brimjava.event.SmsEvent;
import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.loader.util.EventGroups;
import com.matthey.brimjava.sql.util.Sql;

public class LoadSms {
	public static EventGroups loadAll(String evType, EventGroups events) {
		Sql sql = new Sql("plantavail");
		sql.query("select J_Event.id, J_Event.attachmentfk, J_Event.EventTypesFK, J_EventSms.message,"
				+ " J_EventSms.addressgroup, j_event.eventFk, j_event.typefk "
				+ "from J_Event "
				+ "join J_EventTypes on J_EventTypes.id = J_Event.EventTypesFK "
				+ "join J_Type on J_Type.id = J_Event.TypeFK "
				+ "join J_EventSms on J_EventSms.id = J_Event.eventFk "
				+ "where J_EventTypes.Name = '" + evType + "' and J_Type.Name = 'Sms'");
		try {
			while(sql.getResult().next()) {
				events.add(sql.getResult().getInt("attachmentfk"), (GenericEvent) new SmsEvent(
						sql.getResult().getInt("id"), sql.getResult().getInt("attachmentfk"), 
						sql.getResult().getInt("EventTypesFK"), sql.getResult().getInt("EventFK"),
						sql.getResult().getInt("typefk"), sql.getResult().getString("message"), 
						sql.getResult().getInt("addressgroup"))
				);
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}			
		sql.close();
		return events;
	}
}
