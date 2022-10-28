package com.matthey.brimjava.loader.util;

import java.util.ArrayList;

import com.matthey.brimjava.io.Data;
import com.matthey.brimjava.io.Io;

public class InfoItem {
	private ArrayList<AvailLookup> hosts = null;
	private Data address = null;
	private Io io = null;
	private InfoDevice info = null;
	public InfoItem() {
	}
	public void setHosts(ArrayList<AvailLookup> in) {
		hosts = in;
	}
	public ArrayList<AvailLookup> getHosts() {
		return hosts;
	}
	public void setAddress(Data data) {
		address = data;
	}
	public Data getAddress() {
		return address;
	}
	public void setIo(Io in) {
		io = in;
	}
	public Io getIo() {
		return io;
	}
	public void setInfo(InfoDevice in) {
		info = in;
	}
	public InfoDevice getInfo() {
		return info;
	}
	public boolean addressTrue() {
		boolean out = false;
		if (getHosts() != null) {
			if (getIo() != null) {
				if (getAddress() != null) {
					out = true;
					System.out.println("address found");
				}
			}
		}
		if (!out) {
			System.out.println("address not found");
		}
		return out;
	}
	public boolean ioTrue() {
		boolean out = false;
		if (getHosts() != null) {
			if (getIo() != null) {
				if (getAddress() == null) {
					out = true;
					System.out.println("io found,  address not found");
				}
			}
		}
		if (!out) {
			System.out.println("io not found,  address not found");
		}
		return out;
	}
	public boolean hostsTrue() {
		boolean out = false;
		if (getHosts() != null) {
			if (getIo() == null) {
				out = true;
				System.out.println("hosts found, io not found");
			}
		}
		if (!out) {
			System.out.println("hosts not found, io not found");
		}
		return out;
	}
	public boolean nothingTrue() {
		boolean out = false;
		if (getHosts() == null) {
			out = true;
			System.out.println("hosts not found");
		}
		return out;
	}
}
