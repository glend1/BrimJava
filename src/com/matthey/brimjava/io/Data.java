package com.matthey.brimjava.io;

import java.util.ArrayList;
import java.util.Calendar;

import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.da.ItemState;

import com.matthey.brimjava.io.util.IoExecutorService;
import com.matthey.brimjava.loader.util.Availability;
import com.matthey.brimjava.util.JIHandle;

public class Data {
	private JIVariant value = null; // JIHandle.Value(value);
	private int maxTimeout = 60;
	private int timeout = 0;
	private String host = null;
	private Integer channel = null;
	private Integer device = null;
	private String address = null;
	private Integer id = null;
	private StructureServer parent = null;
	private ArrayList<StructureEvent> events = new ArrayList<StructureEvent>();
	private Calendar timestamp = null;
	private Short quality = null;
	private int errorcode = 0;
	/*public Data(StructureServer parent){
		setParent(parent);
	}*/
	public Data(Integer device, String address, StructureServer parent){
		setParent(parent);
		setDevice(device);
		setAddress(address);
		setChannelWithDevice(device);
	}
	public Data(Integer device, String address, int timout, StructureServer parent){
		setParent(parent);
		setDevice(device);
		setAddress(address);
		setTimeout(maxTimeout);
		setChannelWithDevice(device);
	}
	public Data(Integer device, String address, StructureServer parent, Integer id){
		setParent(parent);
		setDevice(device);
		setAddress(address);
		setId(id);
		setChannelWithDevice(device);
	}
	public Data(Integer device, String address, int timout, StructureServer parent, Integer id){
		setParent(parent);
		setDevice(device);
		setAddress(address);
		setTimeout(maxTimeout);
		setId(id);
		setChannelWithDevice(device);
	}
	public Data(Integer device, String address) {
		setDevice(device);
		setAddress(address);
		setChannelWithDevice(device);
	}
	protected Data(Integer device, String address, int timout) {
		setDevice(device);
		setAddress(address);
		setTimeout(maxTimeout);
		setChannelWithDevice(device);
	}
	private void setChannelWithDevice(Integer in) {
		setChannel(Availability.getChannelWithDevice(in));
	}
	public String getAddress() {
		return address;
	}
	public String getAddressPath() {
		String out = null;
		if (Availability.getChannels().get(getChannel()) != null) {
			if (Availability.getDevices().get(getDevice()) != null) {
				out = Availability.getChannels().get(getChannel()) + "." + Availability.getDevices().get(getDevice()) + "." + address;
			}
		}
		return out;
	}
	public void setAddress(String addressIn) {
		address = addressIn;
	}
	public void setData(ItemState data) {
		value = data.getValue();
		timestamp = data.getTimestamp();
		quality = data.getQuality();
		errorcode = data.getErrorCode();
	}
	public JIVariant getValue() {
		Integer attempts = 0;
		while (value == null) {
			try {
				if (attempts >= 10) {
					System.out.println(attempts + " Failed attempts. Unable to obtain IO");
					break;
				}
				attempts += 1;
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println(attempts + " Failed attempts. Unable to obtain IO");
			}
		}
		return value;
	}
	public Calendar getTimestamp() {
		return timestamp;
	}
	public Short getQuality() {
		return quality;
	}
	public int getErrorcode() {
		return errorcode;
	}
	public int getTimeout() {
		return maxTimeout;
	}
	public void setTimeout(int timeoutIn) {
		maxTimeout = timeoutIn;
	}
	public void increment() {
		timeout++;
	}
	public void reset() {
		timeout = 0;
	}
	public int getCurrentTimeout() {
		return timeout;
	}
	public StructureServer getParent() {
		return parent;
	}
	protected void setParent(StructureServer parent) {
		this.parent = parent;
	}
	public void addEvent(StructureEvent event) {
		events.add(event);
	}
	public void removeEvent(Integer id) {
		events.remove(id);
	}
	public ArrayList<StructureEvent> getEvents() {
		return events;
	}
	public Integer getEvent(String condition, String operator) {
		Integer out = null; 
		for (StructureEvent aEvents : getEvents()) {
			if (condition.equalsIgnoreCase(aEvents.getCondition()) && operator.equalsIgnoreCase(aEvents.getOperator())) {
				out = aEvents.getId();
				break;
			}
		}
		return out;
	}
	public StructureEvent getEvent(Integer id) {
		StructureEvent out = null; 
		for (StructureEvent aEvents : getEvents()) {
			if (aEvents.getId() == id) {
				out = aEvents;
				break;
			}
		}
		return out;
	}
	protected void callThem() {
		//System.out.println(id + ": " + JIHandle.asObject(getValue()));
		if (!events.isEmpty()) {
			for (StructureEvent event: events) {
				event.setValue(JIHandle.asObject(getValue()));
				IoExecutorService.call(event);
			}
		}
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public Integer getDevice() {
		return device;
	}
	public void setDevice(Integer device) {
		this.device = device; 
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
