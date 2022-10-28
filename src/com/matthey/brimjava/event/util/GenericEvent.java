package com.matthey.brimjava.event.util;

import com.matthey.brimjava.event.EmailEvent;
import com.matthey.brimjava.event.SmsEvent;
import com.matthey.brimjava.event.SqlFileEvent;

public abstract class GenericEvent {
	private Integer id = null;
	private Integer attachment = null;
	private Integer eventTypes = null;
	private Integer event = null;
	private Integer type = null;
	public GenericEvent(Integer id, Integer attachment, Integer eventTypes, Integer event, Integer type) {
		setId(id);
		setAttachment(attachment);
		setEventTypes(eventTypes);
		setEvent(event);
		setType(type);
	}
	public GenericEvent() {
	}
	public abstract void trigger();	
	public EmailEvent getEmail() {
		if (this.getClass() == EmailEvent.class) {
			return (EmailEvent) this;
		}
		return null;
	}
	public SmsEvent getSms() {
		if (this.getClass() == SmsEvent.class) {
			return (SmsEvent) this;
		}
		return null;
	}
	/*public OeeEvent getOee() {
		if (this.getClass() == OeeEvent.class) {
			return (OeeEvent) this;
		}
		return null;
	}*/
	public SqlFileEvent getSqlFile() {
		if (this.getClass() == SqlFileEvent.class) {
			return (SqlFileEvent) this;
		}
		return null;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getAttachment() {
		return attachment;
	}
	public void setAttachment(Integer in) {
		attachment = in;
	}
	public Integer getEventTypes() {
		return eventTypes;
	}
	public void setEventTypes(Integer in) {
		eventTypes = in;
	}
	public Integer getEvent() {
		return event;
	}
	public void setEvent(Integer in) {
		event = in;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer in) {
		type = in;
	}
}
