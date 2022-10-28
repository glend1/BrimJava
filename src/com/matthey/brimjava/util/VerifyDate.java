package com.matthey.brimjava.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class VerifyDate {
	public static boolean result(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		try {
			dateFormat.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
}
