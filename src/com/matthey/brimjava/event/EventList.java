package com.matthey.brimjava.event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.event.util.SqlDurationType;
import com.matthey.brimjava.sql.util.QueryBuilder;
import com.matthey.brimjava.sql.util.Sql;
import com.matthey.brimjava.util.EventGeneric;

public class EventList {
	private ArrayList<GenericEvent> events = new ArrayList<GenericEvent>();
	private Integer emailType = 1;
	private Integer smsType = 2;
	private Integer sqlType = 4;
	private EventGeneric eventParent = null;
	/*public GenericEvent getEvent(Integer num) {
		GenericEvent match = null; 
		for (GenericEvent event: events) {
			if (event.getId() == num) {
				match = event;
			}
		}
		if (match == null) {
			System.out.println("no event found");
		}
		return match;
	}*/
	public EventList(EventGeneric obj) {
		eventParent = obj;
	}
	public boolean isEmpty() {
		boolean out = false;
		if (getList().isEmpty()) {
			out = true;
		}
		return out;
	}
	public Integer getAttachmentId() {
		return eventParent.getId();
	}
	public Integer getEventTypeId() {
		return eventParent.getEventType();
	}
	public void trigger() {
		for (GenericEvent event: events) {
			event.trigger();
		}
	}
	public void removeEvent(Integer num) {
		GenericEvent event = getEvent(num);
		if (event != null) {
			events.remove(event);
			System.out.println("event removed");
		}
	}
	private void addEvent(GenericEvent event) {
		events.add(event);
		System.out.println("EventList: event " + event.getId() + " added");
	}
	public void addEvent(ArrayList<GenericEvent> eventList) {
		events.addAll(eventList);
		System.out.println("EventList: all events added");
	}
	public ArrayList<GenericEvent> getList() {
		return events;
	}
	private GenericEvent getEvent(Integer id) {
		GenericEvent out = null;
		for (GenericEvent event : getList()) {
			if (event.getId() == id) {
				out = event;
				System.out.println("findEvent: event found");
			}
		}
		if (out == null) {
			System.out.println("findEvent: event not found");
		}
		return out;
	}
	public Integer getEmailType() {
		return emailType;
	}
	public void setEmailType(Integer emailType) {
		this.emailType = emailType;
	}
	public Integer getSmsType() {
		return smsType;
	}
	public void setSmsType(Integer smsType) {
		this.smsType = smsType;
	}
	public Integer getSqlType() {
		return sqlType;
	}
	public void setSqlType(Integer sqlType) {
		this.sqlType = sqlType;
	}
	private Integer addEventTable(Sql obj, Integer eventId, Integer typeId) {
		Integer id = null;
		obj.mod("insert into j_event (eventtypesfk, attachmentfk, typefk, eventfk) values (" + getEventTypeId() + ", "
				+ getAttachmentId() + ", " + typeId + ", " + eventId + ")");
		obj.query("select top 1 id from J_event where eventtypesfk = " + getEventTypeId() + " and AttachmentFK = " 
				+ getAttachmentId() + " and typefk = " + typeId + " and eventfk = " + eventId);
		try {
			obj.getResult().next();
			id = obj.getResult().getInt("id");
			System.out.println("addEventTable: Event added to table");
		} catch (SQLException e) {
			System.out.println("addEventTable: Event no added to table");
			System.out.println("SQL Error");
		}
		return id;
	}
	private Integer removeEventTable(Sql obj, Integer id) {
		Integer eventId = null; 
		obj.query("select eventfk from J_event where id = " + id + " and attachmentfk = " + getAttachmentId()
				+ " and eventtypesfk = " + getEventTypeId());
		try {
			while(obj.getResult().next()) {
				eventId = obj.getResult().getInt("eventfk");
			}
		} catch (SQLException e) {
			System.out.println("removeEventTable: Event not removed from table");
			System.out.println("SQL Error");
		}
		if (eventId != null) {
			removeEvent(id);
			obj.mod("delete from J_event where id = " + id);
			System.out.println("removeEventTable: Event removed from table");
		}
		return eventId;
	}
	public EmailEvent emailGet(Integer id) {
		EmailEvent out = null;
		GenericEvent event = getEvent(id);
		if (event != null) {
			out = event.getEmail();
			System.out.println("emailGet: Event found");
		} else {
			System.out.println("emailGet: Event not found");
		}
		return out;
	}
	public void emailAdd(String subject, String html, Integer group) {
		Sql obj = new Sql("plantavail");
		Integer eventId = null;
		obj.mod("insert into J_EventEmail (addressgroup, subject, html) values (" + group + ", '" + subject 
				+ "', '" + html + "')");
		obj.query("select top 1 id from J_EventEmail where addressgroup = " + group + " and subject = '"
				+ subject + "' and html = '" + html + "'");
		try {
			obj.getResult().next();
			eventId = obj.getResult().getInt("id");
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (eventId != null) {
			Integer id = addEventTable(obj, eventId, getEmailType());
			obj.close();
			EmailEvent newEvent = new EmailEvent(id, getAttachmentId(), getEventTypeId(), eventId, getEmailType(), subject, html, group);
			addEvent(newEvent);
			System.out.println("emailAdd: event added");
		} else {
			System.out.println("emailAdd: event not added");
		}
	}
	public void emailRemove(Integer id) {
		Sql obj = new Sql("plantavail");
		Integer tableId = removeEventTable(obj, id);
		if (tableId != null) {
			EventCleanup.Email(obj, tableId);
			System.out.println("emailRemove: event removed from table");
		} else {
			System.out.println("emailRemove: event not removed from table");
		}
		obj.close();
	}
	public void emailUpdate(Integer id, Integer addressGroup, String subject, String html) {
		EmailEvent event = emailGet(id);
		if (event != null) {
			event.getAddress().setGroup(addressGroup);
			event.setSubject(subject);
			event.setHtml(html);
			Sql obj = new Sql("plantavail");
			obj.mod("update J_EventEmail set addressgroup = " + addressGroup + ", subject = '" + subject + "', html = '" + html + "' where id = " + event.getEvent());
			obj.close();
			System.out.println("emailUpdate: Email event updated");
		} else {
			System.out.println("emailUpdate: Email event not updated");
		}
	}
	public SmsEvent smsGet(Integer id) {
		SmsEvent out = null;
		GenericEvent event = getEvent(id);
		if (event != null) {
			out = event.getSms();
			System.out.println("smsGet: Event found");
		} else {
			System.out.println("smsGet: Event not found");
		}
		return out;
	}
	public void smsAdd(String message, Integer group) {
		Sql obj = new Sql("plantavail");
		Integer eventId = null;
		obj.mod("insert into J_EventSms (addressgroup, message) values (" + group + ", '" + message + "')");
		obj.query("select top 1 id from J_EventSms where addressgroup = " + group + " and message = '" + message + "'");
		try {
			obj.getResult().next();
			eventId = obj.getResult().getInt("id");
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (eventId != null) {
			Integer id = addEventTable(obj, eventId, getSmsType());
			obj.close();
			SmsEvent newEvent = new SmsEvent(id, getAttachmentId(), getEventTypeId(), eventId, getSmsType(), message, group);
			addEvent(newEvent);
			System.out.println("smsAdd: event added");
		} else {
			System.out.println("smsAdd: event not added");
		}
	}
	public void smsRemove(Integer id) {
		Sql obj = new Sql("plantavail");
		Integer tableId = removeEventTable(obj, id);
		if (tableId != null) {
			EventCleanup.Sms(obj, tableId);
			System.out.println("emailRemove: event removed from table");
		} else {
			System.out.println("emailRemove: event not removed from table");
		}
		obj.close();
	}
	public void smsUpdate(Integer id, Integer addressGroup, String message) {
		SmsEvent event = smsGet(id);
		if (event != null) {
			event.getNumbers().setGroup(addressGroup);
			event.setTextMessage(message);
			Sql obj = new Sql("plantavail");
			obj.mod("update J_eventSms set addressgroup = " + addressGroup + ", message = '" + message + "' where id = " + event.getEvent());
			obj.close();
			System.out.println("smsUpdate: SMS event updated");
		} else {
			System.out.println("smsUpdate: SMS event not updated");
		}
	}
	public SqlFileEvent sqlGet(Integer id) {
		SqlFileEvent out = null;
		GenericEvent event = getEvent(id);
		if (event != null) {
			out = event.getSqlFile();
			System.out.println("sqlGet: Event found");
		} else {
			System.out.println("sqlGet: Event not found");
		}
		return out;
	}
	public void sqlAdd(String tableCol, String query, String path, HashMap<Integer, Integer> duration) {
		Sql obj = new Sql("plantavail");
		Integer eventId = null;
		obj.mod("insert into J_EventFileSql (tablecol, query, path) values ('" + tableCol + "', '" + query + "', '" + path + "')");
		obj.query("select top 1 id from J_EventFileSql where tablecol = '" + tableCol + "' and query = '" + query + "' and path = '" + path + "'");
		try {
			obj.getResult().next();
			eventId = obj.getResult().getInt("id");
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (eventId != null) {
			Integer id = addEventTable(obj, eventId, getSqlType());
			obj.close();
			sqlDurationAdd(eventId, duration);
			SqlFileEvent newEvent = new SqlFileEvent(id, getAttachmentId(), getEventTypeId(), eventId, getSmsType(), tableCol, query, path, duration);
			addEvent(newEvent);
			System.out.println("sqlAdd: event added");
		} else {
			System.out.println("sqlAdd: event not added");
		}
	}
	public void sqlRemove(Integer id) {
		Sql obj = new Sql("plantavail");
		Integer tableId = removeEventTable(obj, id);
		if (tableId != null) {
			EventCleanup.Sql(obj, tableId);
			System.out.println("emailRemove: event removed from table");
		} else {
			System.out.println("emailRemove: event not removed from table");
		}
		obj.close();
	}
	public void sqlUpdate(Integer id, String tableCol, String query, String path) {
		SqlFileEvent event = sqlGet(id);
		if (event != null) {
			event.setTable(tableCol);
			event.setQuery(query);
			event.setPath(path);
			Sql obj = new Sql("plantavail");
			obj.mod("update J_EventFileSql set tablecol = '" + tableCol + "', query = '" + query + "', path = '" + path + "' where id = " + id);
			obj.close();
			System.out.println("sqlUpdate: Sql event updated");
		} else {
			System.out.println("sqlUpdate: Sql event didn't update");
		}
	}
	public void sqlDurationAdd(Integer id, HashMap<Integer, Integer> duration) {
		Sql obj = new Sql("plantavail");
		QueryBuilder qb = new QueryBuilder(" or ", "select EventFileSqlDurationTypeFK from J_EventFileSqlDuration where EventFileSqlFK = " + id + " and (");
		ArrayList<Integer> found = new ArrayList<Integer>();
		boolean bFound = false;
		QueryBuilder insertQuery = new QueryBuilder(", ", "insert into J_EventFileSqlDuration (EventFileSqlFK, EventFileSqlDurationTypeFK, value) VALUES ");
		for (Integer durType : duration.keySet()) {
			qb.append("EventFileSqlDurationTypeFK = " + durType);
		}
		obj.query(qb.get() + ")");
		try {
			while(obj.getResult().next()) {
				found.add(obj.getResult().getInt("EventFileSqlDurationTypeFK"));
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		for (Integer durType : duration.keySet()) {
			bFound = false;
			for (Integer i : found) {
				if (duration.get(i) != null) {
					bFound = true;
					break;
				}
			}
			if (bFound) {
				System.out.println("Duration add: " + id + " and " + durType + " already added");			
			} else {			
				insertQuery.append("(" + id + ", " + durType + ", " + duration.get(durType) + ")");
				System.out.println("Duration add: " + id + " and " + durType + " added value of " + duration.get(durType));
			}
		}
		obj.mod(insertQuery.get());
		obj.close();
	}
	public void sqlDurationUpdate(Integer id, Integer type, Integer value) {
		Sql obj = new Sql("plantavail");
		SqlDurationType eventFileSqlDuration = sqlDurationTypeGet(obj, id);
		if (eventFileSqlDuration != null) {
			SqlFileEvent event = sqlGet(eventFileSqlDuration.eventFileSqlFK);
			event.getDurations().update(type, value);
			obj.mod("update J_EventFileSqlDuration set value = " + value + " where EventFileSqlDurationTypeFK = " + type);
			System.out.println("sqlDurationRemove: duration updated in table");
		} else {
			System.out.println("sqlDurationRemove: duration updated in table");
		}
		obj.close();
	}
	public void sqlDurationRemove(Integer id) {
		Sql obj = new Sql("plantavail");
		SqlDurationType eventFileSqlDuration = sqlDurationTypeGet(obj, id);
		if (eventFileSqlDuration != null) {
			SqlFileEvent event = sqlGet(eventFileSqlDuration.eventFileSqlFK);
			event.getDurations().remove(eventFileSqlDuration.eventFileSqlDurationTypeFK);
			obj.mod("delete from J_EventFileSqlDuration where id = " + id);
			System.out.println("sqlDurationRemove: duration removed from table");
		} else {
			System.out.println("sqlDurationRemove: duration removed from table");
		}
		obj.close();
	}
	private SqlDurationType sqlDurationTypeGet(Sql sql, Integer id) {
		SqlDurationType out = null;
		boolean found = false;
		sql.query("select top 1 EventFileSqlFK, EventFileSqlDurationTypeFK from J_EventFileSqlDuration where id = " + id);
		try {
			sql.getResult().next();
			out = new SqlDurationType(sql.getResult().getInt("EventFileSqlFK"), sql.getResult().getInt("EventFileSqlDurationTypeFK"));
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (found) {
			System.out.println("sqlDurationGet: Duration found");
		} else {
			System.out.println("sqlDurationGet: Duration not found");
		}
		return out;
	}
}
