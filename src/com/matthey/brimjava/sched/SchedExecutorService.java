package com.matthey.brimjava.sched;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.matthey.brimjava.sched.util.RunningSchedule;
import com.matthey.brimjava.sql.util.Sql;
import com.matthey.brimjava.util.DateToCal;
import com.matthey.brimjava.util.Time;

public class SchedExecutorService {
	private static ScheduledExecutorService schedExec = null;
	private static boolean status = false;
	private static SortedMap<Timestamp, ArrayList<SchedEvent>> events = Collections.synchronizedSortedMap(new TreeMap<Timestamp, ArrayList<SchedEvent>>());
	private static ArrayList<SchedEvent> eventListName = new ArrayList<SchedEvent>();
	private static Timestamp runTimestamp = null;
	private static RunningSchedule rs = new RunningSchedule();
	private static ScheduledFuture<?> future = null;
	private static Integer intervalMin = 15;
	public static void start() {
		if (!status) {
			schedExec = Executors.newSingleThreadScheduledExecutor();
			Calendar cal = DateToCal.convert(new Date());
			Long realTime = cal.getTimeInMillis();
			cal.set(Calendar.SECOND, 0);
			Long lowest = null;
			Long testTime = null;
			for (Integer i = 0; i <= 60; i += getInterval()) {
				cal.set(Calendar.MINUTE, i);
				testTime = cal.getTimeInMillis() - realTime;
				if (lowest == null) {
					lowest = testTime;
				} else if (lowest < 0) {
					lowest = testTime;
				} else if (testTime < lowest) {
					lowest = testTime;
				}
			}
			System.out.println("Scheduler will start in: " + lowest + "ms and run every " + getInterval() + " minutes");
			setFuture( schedExec.scheduleAtFixedRate( getSchedule(), lowest, getIntervalAsLong(), TimeUnit.MILLISECONDS ) );
			setRunTimestamp(Time.getNow());
			System.out.println("Scheduler started at " + getRunTimestamp());
			status = true;
		} else {
			System.out.println("Scheduler already started");
		}
	}
	public static SortedMap<Timestamp, ArrayList<SchedEvent>> getEvents() {
		return events;
	}
	public static void setEvents(SortedMap<Timestamp, ArrayList<SchedEvent>> events) {
		SchedExecutorService.events = events;
	}
	public static ScheduledExecutorService getSched() {
		return schedExec;
	}
	public static ArrayList<SchedEvent> getEventList() {
		return eventListName;
	}
	public static SchedEvent getEventFromList(String str) {
		SchedEvent out = null;
		for (SchedEvent event : getEventList()) {
			if (event.getName().equalsIgnoreCase(str)) {
				out = event;
			}
		}
		return out;
	}
	public static ArrayList<SchedEvent> getRun(Timestamp date) {
		ArrayList<SchedEvent> out = null;
		if (getEvents().containsKey(date)) {
			out = getEvents().get(date);
			System.out.println("getRun(): Found");
		}
		if (out == null) {
			System.out.println("getRun(): Not Found");
		}
		return out;
	}
	public static SchedEvent getEvent(String name) {
		SchedEvent out = null;
		for (SchedEvent event : getEventList()) {
			if (event.getName().equalsIgnoreCase(name)) {
				out = event;
				System.out.println("getEvent() 0: Found");
				break;
			}
		}
		if (out == null) {
			System.out.println("getEvent() 0: Not Found");
		}
		return out;
	}
	public static SchedEvent getEvent(Timestamp date, String name) {
		ArrayList<SchedEvent> run = getRun(date);
		SchedEvent out = null;
		if (run != null) {
			out = getEvent(run, name);
			System.out.println("getEvent() 1: Found");
		}
		if (out == null) {
			System.out.println("getEvent() 1: Not Found");
		}
		return out;
	}
	public static SchedEvent getEvent(ArrayList<SchedEvent> run, String name) {
		SchedEvent out = null;
		if (run != null) {
			for (SchedEvent event : run) {
				if (name.equalsIgnoreCase(event.getName())) {
					out = event;
					System.out.println("getEvent() 2: Found");
					break;
				}
			}
		}
		if (out == null) {
			System.out.println("getEvents() 2: Not Found");
		}
		return out;
	}
	public static void eventMove(SchedEvent event, Timestamp timestamp) {
		boolean pass = removeEvent(event.getScheduledRun(), event.getName());
		if (pass) {
			event.setScheduledRun(timestamp);
			addEvent(timestamp, event);
			System.out.println("eventMove: event moved");
		} else {
			System.out.println("eventMove: event not moved");
		}
	}
	private static boolean databaseMove(SchedEvent event, Timestamp timestamp) {
		boolean pass = false;
		Sql sql = new Sql("plantavail");
		sql.query("select top 1 id from J_SchedEvent where id = " + event.getId());
		try {
			sql.getResult().next();
			sql.mod("update J_SchedEvent set ScheduledRun = '" + timestamp + "' where id = " + sql.getResult().getInt("id"));
			pass = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		sql.close();
		return pass;
	}
	public static void eventMoveWithDatabase(SchedEvent event, Timestamp timestamp) {
		if (databaseMove(event, timestamp)) {
			System.out.println("eventMoveWithDatabase(): Moving " + event.getScheduledRun() + " to " + timestamp);
			eventMove(event, timestamp);
		}
	}
	private static void eventListAdd(SchedEvent event) {
		getEventList().add(event);
		System.out.println("eventListAdd()");
	}
	private static ArrayList<SchedEvent> addRun(Timestamp date) {
		ArrayList<SchedEvent> time = getRun(date);
		if (time == null) {
			getEvents().put(date, new ArrayList<SchedEvent>());
			time = getRun(date);
			System.out.println("addRun(): Added");
		}
		return time;
	}
	public static void add(Timestamp date, SchedEvent event) {
		ArrayList<SchedEvent> time = addRun(date);
		addEvent(time, event);
		eventListAdd(event);
		System.out.println("add(): Added");
	}
	private static void addEvent(ArrayList<SchedEvent> run, SchedEvent newEvent) {
		if (run != null) {
			SchedEvent event = getEvent(run, newEvent.getName());
			if (event == null) {
				run.add(newEvent);
				System.out.println("addEvent(): Added");
			}
		}
	}
	private static void addEvent(Timestamp date, SchedEvent newEvent) {
		ArrayList<SchedEvent> time = addRun(date);
		addEvent(time, newEvent);
	}
	private static void eventListRemove(SchedEvent event) {
		getEventList().remove(event);
		System.out.println("eventListRemove(): Removed");
	}
	private static void removeRun(Timestamp date) {
		ArrayList<SchedEvent> time = getRun(date);
		if (time != null) {
			if (time.size() == 0) {
				getEvents().remove(date);
				System.out.println("removeRun(): Removed");
			}
		}
	}
	public static void remove(Timestamp time, String name) {
		SchedEvent event = getEvent(time, name);
		if (event != null) {
			eventListRemove(event);
			removeEvent(time, name);
			removeRun(time);
			System.out.println("remove(): Removed");
		} else {
			System.out.println("remove(): Not Removed");
		}
	}
	private static boolean removeEvent(Timestamp time, String name) {
		boolean out = false; 
		ArrayList<SchedEvent> run = getRun(time);
		if (run != null) {
			SchedEvent event = getEvent(run, name);
			if (event != null) {
				run.remove(event);
				removeRun(time);
				System.out.println("removeEvent(): Removed");
				out = true;
			}
		}
		return out;
	}
	public static void trigger(Timestamp date) {
		ArrayList<SchedEvent> event = getRun(date);
		if (event != null) {
			for (SchedEvent item: event) {
				item.trigger();
				reschedule(item);
			}
		}
	}
	public static void reschedule(SchedEvent event) {
		eventMoveWithDatabase(event, event.getAdjustScheduledRunAsTimestamp());
	}
	public static void shutdown() {
		schedExec.shutdown(); // Disable new tasks from being submitted
		try {
			label: {
				// Wait a while for existing tasks to terminate
				if (!schedExec.awaitTermination(60, TimeUnit.SECONDS)) {
					schedExec.shutdownNow(); // Cancel currently executing tasks
					// Wait a while for tasks to respond to being cancelled
					if (!schedExec.awaitTermination(60, TimeUnit.SECONDS)) {
						System.out.println("Scheduler did not terminate");
						break label;
					}
				}
				status = false;
				System.out.println("Scheduler terminated");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			schedExec.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			System.out.println("Scheduler shutdown exception");
		}
	}
	public static Timestamp getRunTimestamp() {
		return runTimestamp;
	}
	public static Long getRunTimestampAsLong() {
		return runTimestamp.getTime();
	}
	private static void setRunTimestamp(Long time) {
		runTimestamp = new Timestamp(Time.removeSeconds(time));
	}
	public static void updateRunTimestamp() {
		setRunTimestamp(getRunTimestampAsLong() + getIntervalAsLong());
	}
	public static void setInterval(Integer inter) {
		intervalMin = inter;
	}
	public static Integer getInterval() {
		return intervalMin;
	}
	public static long getIntervalAsLong() {
		return (long) (intervalMin * 60 * 1000);
	}
	public static RunningSchedule getSchedule() {
		return rs;
	}
	public static void setSchedule(RunningSchedule rs) {
		SchedExecutorService.rs = rs;
	}
	public static ScheduledFuture<?> getFuture() {
		return future;
	}
	public static void setFuture(ScheduledFuture<?> future) {
		SchedExecutorService.future = future;
	}
}
