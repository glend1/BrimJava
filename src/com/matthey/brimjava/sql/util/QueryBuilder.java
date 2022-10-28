package com.matthey.brimjava.sql.util;

public class QueryBuilder {
	protected StringBuilder sb = new StringBuilder();
	protected Integer i = 0;
	private String sepStr = "";
	public QueryBuilder(String s, String str) {
		sb.append(str);
		setSep(s);
	}
	protected String getSep() {
		return sepStr;
	}
	protected void setSep(String sep) {
		sepStr = sep;
	}
	private String sep() {
		String out = " ";
		if (i >= 1) {
			out = sepStr;
		}
		return out;
	}
	public void append(String str) {
		sb.append(sep() + str);
		i++;
	}
	public String get() {
		return sb.toString();
	}
	public void reset() {
		sb = new StringBuilder();
		i = 0;
	}
}
