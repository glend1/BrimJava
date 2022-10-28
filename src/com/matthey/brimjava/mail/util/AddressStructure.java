package com.matthey.brimjava.mail.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.Message;
import javax.mail.Message.RecipientType;

public class AddressStructure {
	private HashMap<Message.RecipientType, List<String>> addresses = new HashMap<Message.RecipientType, List<String>>(); 
	public AddressStructure() {
		addresses.put(Message.RecipientType.TO, new ArrayList<String>());
		addresses.put(Message.RecipientType.CC, new ArrayList<String>());
		addresses.put(Message.RecipientType.BCC, new ArrayList<String>());
	}
	public HashMap<Message.RecipientType, List<String>> getAddresses() {
		return addresses;
	}
	public void add(String type, String email) {
		switch (type.toLowerCase()) {
			case "to":
				addresses.get(Message.RecipientType.TO).add(email);
				break;
			case "cc":
				addresses.get(Message.RecipientType.CC).add(email);
				break;
			case "bcc":
				addresses.get(Message.RecipientType.BCC).add(email);
				break;
			default:
				break;
		}
	}
	public boolean remove(String email) {
		boolean bReturn = false;
		for (RecipientType type: addresses.keySet()) {
			if (addresses.get(type).contains(email)) {
				addresses.get(type).remove(email);
				bReturn = true;
				break;
			}
		}
		return bReturn;
	}
}
