package com.matthey.brimjava.loader.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentData {
	protected String alias = null;
	protected String sDevice = null;
	protected String sChannel = null;
	protected String sHost = null;
	protected Integer iDevice = null;
	protected Integer iChannel= null;
	protected Integer iHost = null;
	protected HashMap<AvailLookup, ArrayList<AvailLookup>> channelIo = null;
	protected ArrayList<String> channelData = null;
	protected ArrayList<AvailLookup> deviceIo = null;
	protected String deviceData = null;
	protected HashMap<String, ArrayList<String>> hostData = null;
	protected AvailLookup hostIo = null;
	protected AvailLookup aChannel;
	protected AvailLookup aHost;
	protected AvailLookup aDevice;
	protected CurrentData() {
	}
	protected void clear() {
		sHost = null;
		sChannel = null;
		sDevice = null;
		iHost = null;
		iChannel = null;
		iDevice = null;
		channelIo = null;
		channelData = null;
		deviceIo = null;
		deviceData = null;
		hostData = null;
		hostIo = null;
		aChannel = null;
		aHost = null;
		aDevice = null;
	}
}
