package com.matthey.brimjava.sms;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.matthey.brimjava.sms.util.In;
import com.matthey.brimjava.sms.util.MessageStructure;
import com.matthey.brimjava.sms.util.Out;

public class Sms {
	// TODO: AL: randomly no reply from buffer
	public static Socket socket = new Socket();
	public static ConcurrentLinkedQueue<MessageStructure> queue = new ConcurrentLinkedQueue<MessageStructure>();
	public static void add(String to, String msg) {
		ArrayList<String> numbers = new ArrayList<String>();
		numbers.add(to);
		add(numbers, msg);
	}
	public static void add(ArrayList<String> to, String msg) {
		queue.add(new MessageStructure(to, msg));
		if (!socket.isConnected()) {
			connect();
			Out.startQueue();
		}
	}
	protected static void connect() {
		try {
			socket = new Socket( "172.30.244.156", 4001);
			Out.open(socket);
			In.open(socket);
			System.out.println("connected");
		} catch (UnknownHostException e) {
			System.out.println("Don't know about host");
		} catch (IOException e) {
			System.out.println("Couldn't get I/O for the connection to: host");
		}
	}
	public static void close() { 
		try {
			Out.close();
			In.close();
			socket.close();
			System.out.println("Close Sockets");
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}