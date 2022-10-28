package com.matthey.brimjava.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Time {
	// TODO: AL: absorb datetocal
	// TODO: AL: absorb longtosqldate
	static public Long getNow() {
		return new Date().getTime();
	}
	public static Long removeSeconds(Long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	public static Timestamp longToTimestamp(Long date) {
		return new Timestamp(date);
	}
}
