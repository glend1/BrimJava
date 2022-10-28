package com.matthey.brimjava.event;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.loader.events.util.AddressGroup;
import com.matthey.brimjava.mail.SendMail;

public class EmailEvent extends GenericEvent {
	private AddressGroup group = null;
	private String subject = null;
	private String html = null;
	/*public EmailEvent(Integer iDb, Integer iType) {
		super(iDb, iType);
	}*/
	public EmailEvent(Integer id, Integer attachment, Integer eventTypes, Integer event, Integer type, String subject, String html, Integer group) {
		super(id, attachment, eventTypes, event, type);
		setSubject(subject);
		setHtml(html);
		this.group = new AddressGroup(group);
	}
	public EmailEvent(String subject, String html, Integer group) {
		setSubject(subject);
		setHtml(html);
		this.group = new AddressGroup(group);
	}
	@Override
	public void trigger() {
		if (html != null) {
			SendMail.send(getAddress().getEmail(), subject, html);
		}
	}
	public AddressGroup getAddress() {
		return group;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
