package com.matthey.brimjava.util;

import java.util.Calendar;
import java.util.Date;

public class DateToCal {
	public static Calendar convert(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
