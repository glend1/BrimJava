package com.matthey.brimjava.sched;

import java.sql.Timestamp;

import com.matthey.brimjava.util.EventGeneric;

public class SchedEvent extends EventGeneric {
	private String name = null;
	private Timestamp scheduledRun = null;
	private Long Interval = null;
	private Integer eventType = 1;
	public SchedEvent(Integer id, String name, Timestamp run, Long ival) {
		setId(id);
		setName(name);
		setScheduledRun(run);
		setInterval(ival);
	}
	public SchedEvent(String name, Timestamp run, Long ival) {
		setName(name);
		setScheduledRun(run);
		setInterval(ival);
	}
	@Override
	public void trigger() { 
		getEvents().trigger();
	}
	@Override
	public Integer getEventType() {
		return eventType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getScheduledRun() {
		return scheduledRun;
	}
	public void setScheduledRun(Timestamp scheduledRun) {
		this.scheduledRun = scheduledRun;
	}
	public void setScheduledRun(Long time) {
		setScheduledRun(new Timestamp(time));
	}
	public Long getInterval() {
		return Interval;
	}
	public void setInterval(Long interval) {
		Interval = interval;
	}
	public Long getAdjustScheduledRunAsLong() {
		return getScheduledRun().getTime() + getInterval();
	}
	public Timestamp getAdjustScheduledRunAsTimestamp() {
		return new Timestamp(getAdjustScheduledRunAsLong());
	}
}
