package com.matthey.brimjava.event.util;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import com.matthey.brimjava.sql.util.Sql;

public class TimeManipulation {
	private HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
	public TimeManipulation(HashMap<Integer, Integer> in) {
		data = in;
	}
	private Integer getType(String type) {
		Integer out = null; 
		try {
			Sql sql = new Sql("brimjava");
			sql.query("select top 1 javavalue from J_EventFileSqlDurationType where name = '" + type + "'");
			sql.getResult().next();
			out = (Integer) sql.getResult().getInt(0);
		} catch (SQLException e) {
			System.out.println("unsupported calendar type");
		}
		return out;
	}
	public void add(String type, Integer value) {
		Integer usedType = getType(type);
		if (usedType != null) {
			data.put(usedType, value * -1);
		}
	}
	public void remove(String type) {
		Integer usedType = getType(type);
		if (usedType != null) {
			remove(usedType);
		}
	}
	public void remove(Integer type) {
		if (data.get(type) != null) {
			data.remove(type);
		} else {
			System.out.println("calendar type not in the data");
		}
	}
	public Calendar calculate(Calendar cal) {
		for (Integer type: data.keySet()) {
			cal.add(type, data.get(type));
		}
		return cal;
	}
	public void update(Integer type, Integer value) {
		if (data.get(type) != null) {
			data.replace(type, value);
		}
	}
}
