package com.matthey.brimjava.sms.util;

import java.io.IOException;
import java.net.UnknownHostException;

public class InThread implements Runnable {
	public void run() {
		while(In.bWaitToRecv) {
			String responseLine = "";
			int iter = 0;
			int breaker = 1;
			try {
				if (In.is != null) {
					// TODO: AL: why next line?
					while (iter<breaker) {
						responseLine = In.is.readLine();
						if(!responseLine.isEmpty()) {
							System.out.println("responseLine = " + responseLine);
							if (Out.testString == null) {
								synchronized(Out.sync) {
									Out.gotMsg = true;
									Out.sync.notify();
								}
							} else if (responseLine.toLowerCase().contains(Out.testString)) {
								synchronized(Out.sync) {
									Out.gotMsg = true;
									Out.sync.notify();
								}
							} else if (responseLine.toLowerCase().contains("error")) {
								System.out.println("could not send text message!");
								In.bWaitToRecv = false;
							}
							break;
						}
						iter++;
					}
				}
			} catch (UnknownHostException e) {
				System.out.println("Trying to connect to unknown host: " + e.getLocalizedMessage());
			} catch (IOException e) { 
				System.out.println("connection closed while recieved thread still running");
			} catch (Exception e) {
				System.out.println("problem2 " + e.getLocalizedMessage());
			}
		}
	}
}
