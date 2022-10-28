package com.matthey.brimjava.sms.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.matthey.brimjava.sms.Sms;

public class Out {
	protected static DataOutputStream os = null;
	protected static Object sync = new Object();
	protected static boolean gotMsg = false;
	protected static String testString = null;
	protected static MessageStructure messageStructure = null;
	public static void open(Socket socket) {
		try {
			os = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("could not open Output Stream");
		}
	}
	protected static void send (ArrayList<String> to, String msg) {
		try {
			msg("ate0\r\n", "ok", 1);
			msg("at+cmgf=1\r\n", "ok", 1);
			msg("at+cnmi=2,2,0,0,0\r\n", "ok", 1);
			for (String number: to) {
				msg("at+cmgs=\"" + number + "\"\r", null, 2);
				// TODO: AL: why next line?
				Thread.sleep(100);
				msg(msg, "ok", 3);
			}
		} catch (InterruptedException e) {
			System.out.println("thread interupted");
		} catch(Exception e) {
			System.out.println("Modem Error ");
		} 
	}
	public static void startQueue() {
		while ((messageStructure = Sms.queue.poll()) != null) {
			send(messageStructure.numbers, messageStructure.message);
		} 
		Sms.close();
	}
	protected static void msg(String strMsg, String testCon, int type) {
		if (Sms.socket != null && os != null && In.is != null) {
			try {
				if (testCon == null) {
					testString = null;
				} else {
					testString = testCon.toLowerCase();
				}
				switch (type) {
					case 1:
						os.writeBytes(strMsg);
						System.out.println("sentLine = " + strMsg);
						synchronized(sync) {
							while (!gotMsg) {
								sync.wait();
							}
							gotMsg = false;
						}
						break;
					case 2:
						os.writeBytes(strMsg);
						System.out.println("sentLine = " + strMsg);
						break;
					case 3:
						os.writeBytes(strMsg);
						os.writeByte(0x1A);
						System.out.println("sentLine = " + strMsg);
						synchronized(sync) {
							while (!gotMsg) {
								sync.wait();
							}
							gotMsg = false;
						}
						break;
				}
			} catch (UnknownHostException e) {
				System.out.println("Trying to connect to unknown host: " + e.getLocalizedMessage());
			} catch (IOException e) { 
				System.out.println("IOException: " + e.getLocalizedMessage());
			} catch (InterruptedException e) {
				System.out.println("InterruptedException: " + e.getLocalizedMessage());
			}
		}
	}
	public static void close() {
		try {
			os.close();
		} catch (IOException e) {
			System.out.println("could not close Output Stream");
		}
	}
}
