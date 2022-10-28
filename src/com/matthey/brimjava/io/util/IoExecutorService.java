package com.matthey.brimjava.io.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.matthey.brimjava.io.StructureEvent;

public class IoExecutorService {
	protected static ExecutorService ioExec = Executors.newSingleThreadExecutor();
	public static void call(StructureEvent event) {
		ioExec.execute(event);
	}
}
