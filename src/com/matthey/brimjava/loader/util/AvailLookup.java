package com.matthey.brimjava.loader.util;

public class AvailLookup {
	private Integer key;
	private String value;
	public AvailLookup(Integer inKey, String inValue) {
		key = inKey;
		value = inValue;
	}
	public void updateName(String name) {
		value = name;
	}
	public Integer getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public String toString() {
		return value;
	}
	public boolean isEmpty() {
		if (key == null && value == null) {
			return true;
		}
		return false;
	}
}