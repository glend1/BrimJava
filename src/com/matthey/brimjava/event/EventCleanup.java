package com.matthey.brimjava.event;

import java.sql.SQLException;

import com.matthey.brimjava.sql.util.Sql;

public class EventCleanup {
	public static void Event(Sql sql, Integer id) {
		Integer typeId = null;
		Integer eventId = null;
		sql.query("select top 1 typefk, eventfk from j_event where id = " + id);
		try {
			sql.getResult().next();
			typeId = sql.getResult().getInt("typefk");
			eventId = sql.getResult().getInt("eventfk");
			switch (typeId) {
				case 1:
					Email(sql, eventId);
					break;
				case 2:
					Sms(sql, eventId);
					break;
				case 4:
					Sql(sql, eventId);
					break;
				default:
					break;
			}	
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		sql.mod("delete from j_event where id = " + id);
	}
	protected static void Email(Sql sql, Integer eventId) {
		sql.mod("delete from J_eventEmail where id = " + eventId);
	}
	protected static void Sms(Sql sql, Integer eventId) {
		sql.mod("delete from J_eventSms where id = " + eventId);
	}
	protected static void Sql(Sql sql, Integer eventId) {
		sql.mod("delete from J_EventFileSql where id = " + eventId);
		sql.mod("delete from J_EventFileSqlDuration where EventFileSqlFK = " + eventId);
	}
}
