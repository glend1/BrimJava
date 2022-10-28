package com.matthey.brimjava.loader.util;

import java.util.ArrayList;
import java.util.HashMap;

public class InfoDevice {
	private HashMap<AvailLookup, ArrayList<AvailLookup>> channel = null;
	private ArrayList<AvailLookup> device = null;
	private AvailLookup lookupChannel = null;
	private AvailLookup lookupDevice = null;
	public InfoDevice() {
	}
	public HashMap<AvailLookup, ArrayList<AvailLookup>> getChannel() {
		return channel;
	}
	public void setChannel(HashMap<AvailLookup, ArrayList<AvailLookup>> in) {
		channel = in;
	}
	public ArrayList<AvailLookup> getDevice() {
		return device;
	}
	public void setDevice(ArrayList<AvailLookup> in) {
		device = in;
	}
	public void setLookupChannel(AvailLookup in) {
		lookupChannel = in;
	}
	public AvailLookup getLookupChannel() {
		return lookupChannel;
	}
	public AvailLookup getLookupDevice() {
		return lookupDevice;
	}
	public void setLookupDevice(AvailLookup in) {
		lookupDevice = in;
	}
	/*public String formatAddress(String address) {
		String out = null; 
		if (getLookupChannel() != null) {
			if (getLookupChannel().getValue() != null) {
				if (getLookupDevice() != null) {
					if (getLookupDevice().getValue() != null) {
						out = getLookupChannel().getValue() + "." + getLookupDevice().getValue() + "." + address;						
					}
				}
			}
		}
		return out;
	}*/
}
