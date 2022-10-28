package com.matthey.brimjava.event;

import java.util.HashMap;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.event.util.TimeManipulation;
import com.matthey.brimjava.sql.SqlFileStatic;
import com.matthey.brimjava.util.ReturnRandom;

public class SqlFileEvent extends GenericEvent {
	private String random = ReturnRandom.getRandom();
	private String table = null;
	private String query = null;
	private String path = null;
	private TimeManipulation durations = null;
	public SqlFileEvent(Integer id, Integer attachment, Integer eventTypes, Integer event, Integer type, String table, String query, String path, TimeManipulation durations) {
		super(id, attachment, eventTypes, event, type);
		setTable(table);
		setQuery(query);
		setPath(path);
		this.durations = durations;
	}
	public SqlFileEvent(Integer id, Integer attachment, Integer eventTypes, Integer event, Integer type, String table, String query, String path, HashMap<Integer, Integer> dur) {
		super(id, attachment, eventTypes, event, type);
		setTable(table);
		setQuery(query);
		setPath(path);
		this.durations = new TimeManipulation(dur);
	}
	@Override
	public void trigger() {
		SqlFileStatic.print(getTable(), getQuery(), getPath(), getRandom(), getDurations());
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getRandom() {
		return random;
	}
	public void setRandom() {
		this.random = ReturnRandom.getRandom();
	}
	public TimeManipulation getDurations() {
		return durations;
	}
}
