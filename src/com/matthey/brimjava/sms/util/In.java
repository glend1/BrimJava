package com.matthey.brimjava.sms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class In {
	protected static BufferedReader is = null;
	protected static boolean bWaitToRecv = true;
	protected static InThread threadObj = new InThread();
	public static void open(Socket socket) {
		try {
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			(new Thread(threadObj)).start();
		} catch (IOException e) {
			System.out.println("could not open Input Stream");
		}
	}
	public static void close() {
		try {
			bWaitToRecv = false;
			is.close();
		} catch (IOException e) {
			System.out.println("could not close Input Stream");
		}
	}
}
