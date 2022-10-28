package com.matthey.brimjava.sms.util;

import java.util.ArrayList;

public class MessageStructure {
	protected ArrayList<String> numbers = new ArrayList<String>();
	protected String message = null;
	public MessageStructure(ArrayList<String> numbers, String message) {
		this.numbers = numbers;
		this.message = message;
	}
}
