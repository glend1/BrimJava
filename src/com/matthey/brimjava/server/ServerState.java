package com.matthey.brimjava.server;

public class ServerState {
	private static ServerStates status = null; 
	protected static void setState(ServerStates in) {
		status = in;
		switch (status) {
			case READY:
				System.out.println("BrimJava Ready!");
				break;
			case STARTING:
				System.out.println("BrimJava Starting!");
				break;
			case CLOSED:
				System.out.println("BrimJava Closed!");
				break;
			case FAILED:
				System.out.println("BrimJava Failed!");
				break;
			case RESTARTING:
				System.out.println("BrimJava Re-Starting!");
				break;
		}
		restartRequired();
	}
	protected static ServerStates getState() {
		return status;
	}
	private static void restartRequired() {
		if (status == ServerStates.FAILED) {
			setState(ServerStates.RESTARTING);
			// TODO: AL: restart procedure
			setState(ServerStates.READY);
		}
	}
	public static boolean isReady() {
		boolean out = false;
		if (status == ServerStates.READY) {
			out = true;
		}
		return out;
	}
}
