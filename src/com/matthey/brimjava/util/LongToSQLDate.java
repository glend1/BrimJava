package com.matthey.brimjava.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LongToSQLDate {
	public static String convert(Long in) {
		String out = null;
		Date date = new Date(in);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		out = format.format(date);
		return out;
	}
}
