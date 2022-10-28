package com.matthey.brimjava.event;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.loader.events.util.AddressGroup;
import com.matthey.brimjava.sms.Sms;

public class SmsEvent extends GenericEvent {
	private AddressGroup group = null;
	private String textMessage = null;
	public SmsEvent(Integer id, Integer attachment, Integer eventTypes, Integer event, Integer type, String message, Integer group) {
		super(id, attachment, eventTypes, event, type);
		setTextMessage(message);
		this.group = new AddressGroup(group);
	}
	@Override
	public void trigger() {
		if (textMessage != null) {
			Sms.add(getNumbers().getMobile(), textMessage);
		}
	}
	public AddressGroup getNumbers() {
		return group;
	}
	public String getTextMessage() {
		return textMessage;
	}
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
}
