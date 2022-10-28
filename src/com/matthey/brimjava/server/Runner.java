package com.matthey.brimjava.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jinterop.dcom.common.JISystem;

import com.matthey.brimjava.io.ServerIo;
import com.matthey.brimjava.loader.type.LoadIo;
import com.matthey.brimjava.loader.type.LoadSched;
import com.matthey.brimjava.sched.SchedExecutorService;

@WebListener
public class Runner implements ServletContextListener {
	ConcurrentHashMap<Integer, ServerIo> io = null;
	ScheduledExecutorService sched = null;
	@Override
	public void contextInitialized(ServletContextEvent arg) {
		// TODO: WAL: possible global issues with concurrency, multithreading
		// TODO: AL: Some queries need turning into transactions
		// TODO: AL: Web Servcies authentication
		ServerState.setState(ServerStates.STARTING);
		ClassLoader.load("JDBC Driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			JISystem.setInBuiltLogHandler(false);
		} catch (Exception e) {
			System.out.println("cannot read/write to j-interop log file.");
		}
		LoadIo.loadAll();
		LoadSched.loadAll();
		/*System.out.println(WebServiceInterface.readIo(13, "D20000"));
		WebServiceInterface.writeIo(13, "D20000", 750);
		System.out.println(WebServiceInterface.readIo(13, "D20000"));*/
		/*System.out.println("test:" + WebServiceInterface.readIo(13, "D4006@FLOAT"));
		System.out.println("test:" + WebServiceInterface.readIo(13, "D4006@FLOAT"));
		System.out.println("test:" + WebServiceInterface.readIo(13, "D4006@FLOAT"));*/
		//LoadIo.remove("smelting", "ts3");
		//HashMap<Integer, Integer> durations = new HashMap<Integer, Integer>();
		//durations.put(5, 2);
		//durations.put(10, 4);
		//LoadList.getFromIo(2, 53, 13, "50", ">").sqlAdd("plantavail", "select * from J_Device", "C:\\mods\\", durations);
		//LoadList.getFromIo(2, 53, 10, "50", ">").sqlDurationUpdate(1, 5, 6);
		//LoadList.getFromIo(2, 53, 13, "50", ">").sqlRemove(9);
		//LoadList.getFromIo(2, 53, 10, "50", ">").smsRemove(3);
		//LoadList.getFromIo(2, 53, 10, "50", ">").smsUpdate(3, 3, "this is a test aswell");
		//LoadList.getFromIo(2, 53, 13, "50", ">").smsAdd("this is a test", 4);
		//LoadList.getFromIo(2, 53, 10, "50", ">").emailRemove(2);
		//LoadList.getFromIo(2, 53, 10, "50", ">").emailUpdate(1, 3, "last test", "<h1>this is the last test</h1>");
		//AddressGroupData.addGroup(4, 5);
		//EventList test = LoadList.getFromIo(2, 53, 10, "50", ">");
		//LoadList.getFromIo(2, 53, 12, "50", ">").emailAdd("this is a test", "<h1>this is a test</h1>", 3);
		//LoadData.LoadStructure.add(13, "50", ">");
		//Date date = new Date();
		//LoadSched.add("report 0", date.getTime(), 1000L * 60);
		//LoadList.getFromSched("report 0").emailAdd("this is a scheduled test", "<h1>this is a scheduled test</h1>", 3);
		//LoadData.add(2, 53, "D4006@FLOAT", true);
		//LoadData.add(2, 53, "D4026@FLOAT", 11);
		//LoadData.LoadStructure.remove(10, 2);
		LoadIo.getServer();
		SchedExecutorService.getSched();
		ServerState.setState(ServerStates.READY);
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg) {
		System.out.println("BrimJava shutdown!");
		ServerState.setState(ServerStates.CLOSED);
	}
}
