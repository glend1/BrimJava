package com.matthey.brimjava.util;

import com.matthey.brimjava.event.EventCleanup;
import com.matthey.brimjava.event.EventList;
import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.sql.util.Sql;

public abstract class EventGeneric {
	// TODO: AL: This shouldn't exist if events is empty !123
	private Integer id = null; // j_event.id
	private EventList events = new EventList(this);
	public abstract void trigger();
	public abstract Integer getEventType();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public EventList getEvents() {
		return events;
	}
	public void cleanupSelf(Sql sql) {
		for (GenericEvent triggerEvent : getEvents().getList()) {
			EventCleanup.Event(sql, triggerEvent.getId());
		}
	}
}
