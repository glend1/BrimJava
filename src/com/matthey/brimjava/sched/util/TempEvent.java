package com.matthey.brimjava.sched.util;

import com.matthey.brimjava.sched.SchedEvent;

public class TempEvent {
	public SchedEvent event = null;
	public Long timestamp = null;
	public TempEvent(SchedEvent ev, Long ts) {
		event = ev;
		timestamp = ts;
	}
}
