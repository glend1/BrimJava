package com.matthey.brimjava.sched.util;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.matthey.brimjava.sched.SchedEvent;
import com.matthey.brimjava.sched.SchedExecutorService;

public class RunningSchedule implements Runnable {
	public RunningSchedule() {
		System.out.println("Schedule Instantiated");
	}
	public void run() {
		// TODO: AL: runtime doesn't have to be correct here 
		Timestamp runtime = SchedExecutorService.getRunTimestamp();
		SchedExecutorService.updateRunTimestamp();
		ArrayList<SchedEvent> events = null;
		Long currentEventLong = null;
		ArrayList<TempEvent> moveArray = new ArrayList<TempEvent>();
		//System.out.println(runtime + " Running");
		/*Set<Timestamp> keys = SchedExecutorService.getEvents().keySet();  // Needn't be in synchronized block
		Timestamp next = null;
		synchronized (SchedExecutorService.getEvents()) {  // Synchronizing on m, not s!
			Iterator<Timestamp> i = keys.iterator(); // Must be in synchronized block
			while (i.hasNext()) {
			next = i.next();*/
		for (Timestamp ts : SchedExecutorService.getEvents().keySet()) {
			if (runtime.compareTo(ts) > 0) {
				events = SchedExecutorService.getRun(ts);
				for (SchedEvent event : events) {
					System.out.println("Old Schedule Event detected, updating");
					currentEventLong = event.getAdjustScheduledRunAsLong();
					while (currentEventLong < runtime.getTime()) {
						currentEventLong += event.getInterval();
					}
					moveArray.add(new TempEvent(event, currentEventLong));
				}
			} else if (runtime.compareTo(ts) < 0) {
				System.out.println("Future Schedule Event detected, breaking");
				break;
			} else {
				System.out.println("Current event detected, triggering");
				for (SchedEvent event : SchedExecutorService.getRun(ts)) {
					moveArray.add(new TempEvent(event, event.getScheduledRun().getTime()));
				}
			}
		}
		for (TempEvent holder : moveArray) {
			SchedExecutorService.eventMoveWithDatabase(holder.event, new Timestamp(holder.timestamp));
			if (holder.timestamp == runtime.getTime()) {
				holder.event.trigger();
				SchedExecutorService.reschedule(holder.event);
			}
		}
	}
}
