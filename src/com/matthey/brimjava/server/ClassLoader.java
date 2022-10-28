package com.matthey.brimjava.server;

public class ClassLoader {
	static public boolean load(String name, String className) {
		boolean out = true;
		try {
			Class.forName(className);
			out = true;
		} catch (ClassNotFoundException e) {
			System.out.println(name + " not found");
			out = false;
		}
		return out;
	}
}
