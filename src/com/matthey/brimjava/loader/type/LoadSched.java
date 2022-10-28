package com.matthey.brimjava.loader.type;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.loader.util.EventGroups;
import com.matthey.brimjava.loader.util.GetEvents;
import com.matthey.brimjava.sched.SchedEvent;
import com.matthey.brimjava.sched.SchedExecutorService;
import com.matthey.brimjava.sql.util.Sql;
import com.matthey.brimjava.util.Time;

//TODO: AL: close sched gracefully
public class LoadSched {
	public static void loadAll() {
		EventGroups events = GetEvents.byType("Scheduler");
		SchedEvent newEvent = null;
		Sql obj = new Sql("plantavail");
		obj.query("select id, name, scheduledrun, interval from j_schedevent");
		try {
			while( obj.getResult().next() ) {
				newEvent = new SchedEvent(obj.getResult().getInt("id"), obj.getResult().getString("name"), 
						obj.getResult().getTimestamp("scheduledrun"), obj.getResult().getLong("interval"));
				ArrayList<GenericEvent> eventList = events.getEvent(newEvent.getId());
				if (eventList != null) {
					newEvent.getEvents().addEvent(eventList);
					SchedExecutorService.add(newEvent.getScheduledRun(), newEvent);
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		SchedExecutorService.start();
	}
	private static SchedEvent get(Long date, String name) {
		Timestamp newRun = new Timestamp(date);
		return SchedExecutorService.getEvent(newRun, name);
	}
	private static SchedEvent get(String name) {
		return SchedExecutorService.getEvent(name);
	}
	public static void add(String name, Long run, Long interval) {
		run = Time.removeSeconds(run);
		SchedEvent event = get(run, name);
		Timestamp newRun = new Timestamp(run);
		if (event == null) {
			SchedEvent newEvent = new SchedEvent(name, newRun, interval);
			Sql obj = new Sql("plantavail");
			// TODO: AL: this causes errors if the report name is already in use due to a contraint on the the db for db checking
			obj.mod("insert into j_schedevent (name, scheduledrun, interval) values ('" + name + "', '" + newRun + "', " + interval + ")");
			obj.query("select top 1 id from j_schedevent where name = '" + name + "'");
			try {
				obj.getResult().next();
				newEvent.setId(obj.getResult().getInt("id"));
			} catch (SQLException e) {
				System.out.println("SQL Error");
			}
			obj.close();
			SchedExecutorService.add(newRun, newEvent);
			System.out.println("Event: " + name + " added!");
		}
	}
	public static void remove(String name) {
		SchedEvent event = get(name);
		if (event != null) {
			Sql obj = new Sql("plantavail");
			obj.mod("delete from j_schedevent where id = '" + event.getId() + "'");
			event.cleanupSelf(obj);
			obj.close();
			SchedExecutorService.remove(event.getScheduledRun(), event.getName());
		}
	}
	public static void update(String oldName, String newName, Long interval, Long time) {
		SchedEvent event = get(oldName);
		if (event != null) {
			Timestamp ts = new Timestamp(time); 
			Sql obj = new Sql("plantavail");
			obj.mod("update j_schedevent set interval = " + interval + ", name = '" + newName + "', ScheduledRun = '" + ts + "' where name = '" + oldName + "'");
			obj.close();
			event.setInterval(interval);
			event.setName(newName);
			SchedExecutorService.eventMove(event, ts);
		}
	}
}