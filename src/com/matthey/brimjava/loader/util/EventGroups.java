package com.matthey.brimjava.loader.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.matthey.brimjava.event.util.GenericEvent;

public class EventGroups {
	//index here is the eventid
	private HashMap<Integer, ArrayList<GenericEvent>> group = new HashMap<Integer, ArrayList<GenericEvent>>(); 
	public HashMap<Integer, ArrayList<GenericEvent>> getAddresses() {
		return group;
	}
	private void addIndex(Integer id) {
		if (group.get(id) == null) {
			group.put(id, new ArrayList<GenericEvent>());
		} else {
			System.out.println("Event: " + id + " already listed");
		}		
	}
	public void add(Integer id, GenericEvent event) {
		addIndex(id);
		group.get(id).add(event);
	}
	public void add(Integer id, ArrayList<GenericEvent> eventList) {
		addIndex(id);
		group.get(id).addAll(eventList);
	}
	public boolean remove(GenericEvent event) {
		boolean bReturn = false;
		if (group.containsValue(event)) {
			group.remove(event);
			bReturn = true;
		}
		return bReturn;
	}
	public boolean remove(Integer id) {
		boolean bReturn = false;
		if (group.containsKey(id)) {
			group.remove(group.get(id));
			bReturn = true;
		}
		return bReturn;
	}
	public ArrayList<GenericEvent> getEvent(Integer key) {
		ArrayList<GenericEvent> event = null;
		if (group.containsKey(key)) {
			event = group.get(key);
		}
		return event;
	}
}
