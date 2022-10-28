package com.matthey.brimjava.loader.util;

public class AddData {
	private Integer serverId = null;
	private Integer deviceId = null;
	private String address = null;
	private boolean database = false;
	private Integer id = null;
	public AddData (Integer serverId, Integer deviceId, String address) {
		setServerId(serverId);
		setDeviceId(deviceId);
		setAddress(address);
	}
	public AddData (Integer serverId, Integer deviceId, String address, Integer id) {
		setServerId(serverId);
		setDeviceId(deviceId);
		setAddress(address);
		setId(id);
	}
	public AddData (Integer serverId, Integer deviceId, String address, boolean database) {
		setServerId(serverId);
		setDeviceId(deviceId);
		setAddress(address);
		setDatabase(database);
	}
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public boolean getDatabase() {
		return database;
	}
	public void setDatabase(boolean database) {
		this.database = database;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
