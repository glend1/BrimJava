package com.matthey.brimjava.event.util;

public class SqlDurationType {
	public Integer eventFileSqlFK = null;
	public Integer eventFileSqlDurationTypeFK = null;
	public SqlDurationType(Integer eventFileSqlFK, Integer eventFileSqlDurationTypeFK) {
		this.eventFileSqlDurationTypeFK = eventFileSqlDurationTypeFK;
		this.eventFileSqlFK = eventFileSqlFK;
	}
}
