package com.matthey.brimjava.loader.events.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.matthey.brimjava.event.EventList;
import com.matthey.brimjava.io.Data;
import com.matthey.brimjava.io.Io;
import com.matthey.brimjava.io.ServerIo;
import com.matthey.brimjava.io.StructureEvent;
import com.matthey.brimjava.io.StructureServer;
import com.matthey.brimjava.loader.type.LoadIo;
import com.matthey.brimjava.sched.SchedEvent;
import com.matthey.brimjava.sched.SchedExecutorService;

public class LoadList {
	public static EventList getFromIo(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator) {
		EventList eventList = null;
		ConcurrentHashMap<Integer, ServerIo> loadIo = LoadIo.getServer();
		if (loadIo != null) {
			ServerIo server = loadIo.get(serverId);
			if (server != null) {
				for (Integer ioId : server.getServers().keySet()) {
					Io io = server.findServer(ioId);
					if (io != null) {
						StructureServer struct = io.getStructure();
						if (struct != null) {
							Map<String, Data> plc = struct.searchPlc(deviceId);
							if (plc != null) {
								Data data = struct.findItemById(itemId);
								if (data != null) {
									Integer eventStructureId = data.getEvent(condition, operator);
									if (eventStructureId != null) {
										StructureEvent eventStructure = data.getEvent(eventStructureId);
										if (eventStructure != null) {
											eventList = eventStructure.getEvents();
										}
									}
								}
							}
						}
					}
					if (eventList != null) {
						break;
					}
				}
			}
		}
		return eventList;
	}
	public static EventList getFromSched(String name) {
		EventList out = null;
		SchedEvent event = SchedExecutorService.getEventFromList(name);
		if (event != null) {
			out = event.getEvents();
		}
		return out;
	}
}
