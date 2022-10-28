package com.matthey.brimjava.loader.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.openscada.opc.lib.da.browser.Branch;

import com.matthey.brimjava.io.Data;
import com.matthey.brimjava.io.Io;
import com.matthey.brimjava.loader.type.LoadIo;
import com.matthey.brimjava.sql.util.Sql;

public class Availability {
	//channel/device/host
	private static HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> data = new HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>>();
	//private static HashMap<Integer, AliasLookup> aliases = new HashMap<Integer, AliasLookup>();
	//host/channel/device
	private static HashMap<String, HashMap<String, ArrayList<String>>> io = new HashMap<String, HashMap<String,ArrayList<String>>>();
	private static CurrentData cd = new CurrentData();
	private static Sql sql = null;
	private static HashMap<Integer, String> hosts = new HashMap<Integer, String>();
	private static HashMap<Integer, String> devices = new HashMap<Integer, String>();
	private static HashMap<Integer, String> channels = new HashMap<Integer, String>();
	public static HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> getData() {
		return data;
	}
	public static HashMap<Integer, String> getHosts() {
		return hosts;
	}
	public static HashMap<Integer, String> getChannels() {
		return channels;
	}
	public static HashMap<Integer, String> getDevices() {
		return devices;
	}
	public static ArrayList<AvailLookup> getHostCount(Integer device) {
		ArrayList<AvailLookup> out = null;
		ArrayList<AvailLookup> dDevice = null;
		for (AvailLookup dChannel : getData().keySet()) {
			dDevice = getDevice(getData().get(dChannel), device);
			if (dDevice != null) {
				out = dDevice;
			}
		}
		return out;
	}
	public static HashMap<AvailLookup,  ArrayList<AvailLookup>> getChannel(HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> inArray, Integer channel) {
		HashMap<AvailLookup, ArrayList<AvailLookup>> out = null;
		if (inArray != null ) {
			for (AvailLookup lookup : inArray.keySet()) {
				if (lookup.getKey() == channel) {
					out = inArray.get(lookup);
					System.out.println("Channel: " + channel + " found");
					break;
				}
			}
			if (out == null) {
				System.out.println("Channel: " + channel + " not found");
			}
		}
		return out;
	}
	public static HashMap<AvailLookup,  ArrayList<AvailLookup>> getChannel(HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> inArray, String channel) {
		HashMap<AvailLookup, ArrayList<AvailLookup>> out = null;
		if (inArray != null ) {
			for (AvailLookup lookup : inArray.keySet()) {
				if (lookup.getValue() != null) {
					if (lookup.getValue().equalsIgnoreCase(channel)) {
						out = inArray.get(lookup);
						System.out.println("Channel: " + channel + " found");
						break;
					}
				}
			}
			if (out == null) {
				System.out.println("Channel: " + channel + " not found");
			}
		}
		return out;
	}
	private static AvailLookup getChannelLookup(HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> arrayIn, HashMap<AvailLookup, ArrayList<AvailLookup>> findArray) {
		AvailLookup out = null;
		HashMap<AvailLookup, ArrayList<AvailLookup>> search = null;
		for (AvailLookup key : arrayIn.keySet()) {
			search = arrayIn.get(key);
			if (search == findArray) {
				out = key;
			}
		}
		return out;
	}
	protected static HashMap<AvailLookup, ArrayList<AvailLookup>> addChannel(HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> inArray, Integer id, String name) {
		HashMap<AvailLookup, ArrayList<AvailLookup>> out = null;
		if (inArray != null ) {
			out = getChannel(inArray, id);
			if (out == null) {
				out = new HashMap<AvailLookup, ArrayList<AvailLookup>>();
				inArray.put(new AvailLookup(id, name), out);
				addLookup(getChannels(), id, name);
				System.out.println("Channel: added " + id);
			} else {
				System.out.println("Channel: " + id + " already exists");
			}
		} else {
			System.out.println("Channel: bad array");
		}
		return out;
	}
	protected static void removeChannel(HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> inArray, AvailLookup channel) {
		HashMap<AvailLookup,  ArrayList<AvailLookup>> channelLookup = getChannel(inArray, channel.getKey());
		if (channelLookup == null) {
			System.out.println("Channel: " + channel + " cannot be removed");
		} else {
			inArray.remove(channel);
			removeLookup(getChannels(), channel.getKey());
			System.out.println("Channel: " + channel + " removed");
		}
	}
	public static ArrayList<AvailLookup> getDeviceWithoutChannel(Data device) {
		ArrayList<AvailLookup> out = null;
		for (AvailLookup channelLookup : data.keySet()) {
			for (AvailLookup deviceLookup : data.get(channelLookup).keySet()) {
				if (deviceLookup.getKey() == device.getDevice()) {
					//device.setChannel(channelLookup.getKey());
					out = data.get(channelLookup).get(deviceLookup);
					break;
				}
			}
			if (out != null) {
				break;
			}
		}
		return out;
	}
	public static Integer getChannelWithDevice(Integer in) {
		Integer out = null;
		for (AvailLookup channelLookup : data.keySet()) {
			for (AvailLookup deviceLookup : data.get(channelLookup).keySet()) {
				if (deviceLookup.getKey() == in) {
					out = channelLookup.getKey();
					//out = data.get(channelLookup).get(deviceLookup);
					break;
				}
			}
			if (out != null) {
				break;
			}
		}
		return out;
	}
	public static ArrayList<AvailLookup> getDevice(HashMap<AvailLookup, ArrayList<AvailLookup>> channel, Integer device) {
		ArrayList<AvailLookup> out = null;
		for(AvailLookup lookup : channel.keySet()) {
			if (lookup.getKey() == device) {
				out = channel.get(lookup);
				System.out.println("Device: " + device + " found");
				break;
			}
		}
		if (out == null) {
			System.out.println("Device: " + device + " not found");
		}
		return out;
	}
	public static ArrayList<AvailLookup> getDevice(HashMap<AvailLookup, ArrayList<AvailLookup>> channel, String device) {
		ArrayList<AvailLookup> out = null;
		for(AvailLookup lookup : channel.keySet()) {
			if (lookup.getValue() != null) {
				if (lookup.getValue().equalsIgnoreCase(device)) {
					out = channel.get(lookup);
					System.out.println("Device: " + device + " found");
					break;
				}
			}
		}
		if (out == null) {
			System.out.println("Device: " + device + " not found");
		}
		return out;
	}
	private static AvailLookup getDeviceLookup(HashMap<AvailLookup, ArrayList<AvailLookup>> arrayIn, ArrayList<AvailLookup> findArray) {
		AvailLookup out = null;
		ArrayList<AvailLookup> search = null;
		for (AvailLookup key : arrayIn.keySet()) {
			search = arrayIn.get(key);
			if (search == findArray) {
				out = key;
			}
		}
		return out;
	}
	protected static ArrayList<AvailLookup> addDevice(HashMap<AvailLookup, ArrayList<AvailLookup>> channel, Integer id, String value) {
		ArrayList<AvailLookup> out = getDevice(channel, id);
		if (out == null) {
			out = new ArrayList<AvailLookup>();
			channel.put(new AvailLookup(id, value), out);
			addLookup(getDevices(), id, value);
			System.out.println("Device: added " + id);
		} else {
			System.out.println("Device: " + id + " already exists");
		}
		return out;
	}
	protected static void removeDevice(HashMap<AvailLookup, ArrayList<AvailLookup>> inArray, AvailLookup device) {
		ArrayList<AvailLookup> deviceLookup = getDevice(inArray, device.getKey());
		if (deviceLookup == null) {
			System.out.println("Device: " + device + " cannot be removed");
		} else {
			inArray.remove(device);
			removeLookup(getDevices(), device.getKey());
			System.out.println("Device: " + device + " removed");
		}
	}
	public static AvailLookup getHost(ArrayList<AvailLookup> hostList, Integer host) {
		AvailLookup out = null;
		for (AvailLookup hostLookup : hostList) {
			if (hostLookup.getKey() == host) {
				out = hostLookup;
				System.out.println("Host: " + host + " found");
			}
		}
		if (hostList.contains(host)) {
		} else { 
			System.out.println("Host: " + host + " not found");
		}
		return out;
	}
	public static AvailLookup getHost(ArrayList<AvailLookup> hostList, String host) {
		AvailLookup out = null;
		for (AvailLookup hostLookup : hostList) {
			if (hostLookup.getValue() != null) {
				if (hostLookup.getValue().equalsIgnoreCase(host)) {
					out = hostLookup;
					System.out.println("Host: " + host + " found");
				}
			}
		}
		if (hostList.contains(host)) {
		} else { 
			System.out.println("Host: " + host + " not found");
		}
		return out;
	}
	public static AvailLookup addHost(ArrayList<AvailLookup> hostList, Integer id, String value) {
		AvailLookup out = getHost(hostList, id);
		if (out == null) {
			out = new AvailLookup(id, value);
			hostList.add(out);
			System.out.println("Host: added " + id);
		} else {
			System.out.println("Host: " + id + " already exists");
		}
		return out;
	}
	protected static void removeHost(ArrayList<AvailLookup> inArray, AvailLookup host) {
		AvailLookup hostLookup = getHost(inArray, host.getKey());
		if (hostLookup == null) {
			System.out.println("Device: " + host + " cannot be removed");
		} else {
			inArray.remove(host);
			System.out.println("Device: " + host + " removed");
		}
	}
	public static ArrayList<AvailLookup> otherHosts(ArrayList<AvailLookup> hostList, Integer key) {
		ArrayList<AvailLookup> out = null;
		for (AvailLookup host : hostList) {
			if (host.getKey() != key) {
				if (out == null) {
					out = new ArrayList<AvailLookup>();
				}
				out.add(host);
			}
		}
		if (out != null) {
			if (out.size() > 1) {
				System.out.println("Host: generated other hosts");
			}
		}
		return out;
	}
	public static AvailLookup nextHost(ArrayList<AvailLookup> otherHosts) {
		AvailLookup out = null; 
		if (otherHosts != null) {
			out = otherHosts.get(0);
			System.out.println("Host: getting next host");
		} else {
			System.out.println("Host: no other host");
		}
		return out;
	}
	public static Io getIoConn(AvailLookup host) {
		Io out = null; 
		for (Integer i : LoadIo.getServer().keySet()) {
			for (Integer j : LoadIo.getServer().get(i).getServers().keySet()) {
				if (LoadIo.getServer().get(i).getServers().get(j).getHost().equalsIgnoreCase(host.getValue())) {
					out = LoadIo.getServer().get(i).getServers().get(j);
				}
			}
		}
		return out;
	}
	public static void loadAll() {
		System.out.println("Getting data!");
		System.out.println("Getting Top Server Hosts");
		getKnownHosts();
		System.out.println("Converting SQL Tables to an Multi-dimensional array");
		sqlToArray();
		System.out.println("Converting Top Server IO to an Multi-dimensional array");
		ioToArray();
		sql = new Sql("PlantAvail");
		System.out.println("Starting Top Server IO update");
		updateAdd();
		System.out.println("Starting Top Server IO remove");
		updateRemove();
		sql.close();
		cd.clear();
		System.out.println("Data collection finished");
	}
	private static void getKnownHosts() {
		Integer hostId = null;
		String hostName = null;
		Sql obj = new Sql("plantavail");
		obj.query("select id, host"
				+ " from j_io");
		try {
			while(obj.getResult().next()) {	
				hostId = obj.getResult().getInt("id");
				hostName = obj.getResult().getString("host");
				if (notSystem(hostName)) {
					if (!getHosts().containsKey(hostId)) {
						getHosts().put(hostId, hostName);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		obj.close();
	}
	private static void sqlToArray() {
		//channel/device/host
		HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>> tempData = new HashMap<AvailLookup, HashMap<AvailLookup, ArrayList<AvailLookup>>>();
		//HashMap<Integer, AliasLookup> tempAliases = new HashMap<Integer, AliasLookup>();
		CurrentData td = new CurrentData();
		Sql obj = new Sql("plantavail");
		obj.query("select j_dio.id as id, j_channel.id as channelid, j_channel.name as channelname, j_device.id as deviceid, j_device.name as devicename, j_io.id as hostid, j_io.host as hostname, alias"
				+ " from j_io"
				+ " left join j_dio on j_io.id = j_DIO.iofk"
				+ " left join j_device on j_DIO.devicefk = j_device.id"
				+ " left join j_channel on j_channel.id = j_device.channelfk");
		try {
			while(obj.getResult().next()) {
				td.clear();
				td.sChannel = obj.getResult().getString("channelname");
				td.sDevice = obj.getResult().getString("devicename");
				td.sHost = obj.getResult().getString("hostname");
				td.iChannel = obj.getResult().getInt("channelid");
				td.iDevice = obj.getResult().getInt("deviceid");
				td.iHost = obj.getResult().getInt("hostid");
				td.alias = obj.getResult().getString("alias");
				if (!(td.sChannel == null || td.sDevice == null || td.iChannel == null || td.iDevice == null)) {
					// TODO: AL: isolate getAlias method; 
					/*if (!tempAliases.containsKey(td.iDevice)) {
						tempAliases.put(td.iDevice, new AliasLookup(td.iChannel, td.iDevice, td.alias));
					}*/
					if (notSystem(td.sChannel)) {
						td.channelIo = addChannel(tempData, td.iChannel, td.sChannel);
						if (td.channelIo != null) {
							if (notSystem(td.sDevice)) {
								td.deviceIo = addDevice(td.channelIo, td.iDevice, td.sDevice);
								if (td.deviceIo != null) {
									if (notSystem(td.sHost)) {
										addHost(td.deviceIo, td.iHost, td.sHost);
									}
								}
							}
						}
					}
				} else {
					System.out.println("Incomplete line: " + td.sHost + ":" + td.iHost
							+ " " + td.sChannel + ":" + td.iChannel
							+ " " + td.sDevice + ":" + td.iDevice);
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		obj.close();
		//aliases = tempAliases; 
		data = tempData;
	}
	private static void ioToArray() {
		// TODO: AL: increase efficiency
		//host/channel/device
		HashMap<String, HashMap<String, ArrayList<String>>> tempIo = new HashMap<String, HashMap<String,ArrayList<String>>>();
		String hostName = null;
		String deviceName = null;
		String channelName = null;
		try {
			for (Integer intServer : LoadIo.getServer().keySet()) {
				for (Integer iIo : LoadIo.getServer().get(intServer).getServers().keySet()) {
					hostName = LoadIo.getServer().get(intServer).getServers().get(iIo).getHost();
					if (notSystem(hostName)) {
						if (!tempIo.containsKey(hostName)){
							tempIo.put(hostName, new HashMap<String, ArrayList<String>>());
						}
					}
					for (Branch channel : LoadIo.getServer().get(intServer).getServers().get(iIo).getServer().getTreeBrowser().browse().getBranches()) {
						channelName = channel.getName();
						if (notSystem(channelName)) {
							if (!tempIo.get(hostName).containsKey(channelName)) {
								tempIo.get(hostName).put(channelName, new ArrayList<String>());
							}
						}
						for (Branch device : channel.getBranches()) {
							deviceName = device.getName();
							if (notSystem(deviceName)) {
								if (!tempIo.get(hostName).get(channelName).contains(deviceName)) {
									tempIo.get(hostName).get(channelName).add(deviceName);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception thrown in TreeWalk");
			e.printStackTrace();
		}
		io = tempIo;
	}
	private static void updateAdd() {
		for (String host : io.keySet()) {
			cd.clear();
			if (notSystem(host)) {
				for (Integer iHost : getHosts().keySet()) {
					if (getHosts().get(iHost).equalsIgnoreCase(host)) {
						cd.sHost = host;
						cd.iHost = iHost;
						break;
					}
				}
				if (cd.iHost != null) {
			/*if (getHosts().containsValue(host)) {
				cd.sHost = host;
				if (notSystem(cd.sHost)) {
					cd.iHost = getHosts().get(host);*/
					for (String channel : io.get(cd.sHost).keySet()) {
						cd.sChannel = channel;
						if (notSystem(cd.sChannel)) {
							cd.channelIo = getChannel(getData(), cd.sChannel);
							if (cd.channelIo == null) {
								addChannelDb(cd);
							} else {
								cd.iChannel = getChannelLookup(getData(), cd.channelIo).getKey();
							}
							for (String device : io.get(cd.sHost).get(cd.sChannel)) {
								cd.sDevice = device;
								if (notSystem(cd.sDevice)) {
									cd.deviceIo = getDevice(cd.channelIo, cd.sDevice);
									if (cd.deviceIo == null) {
										addDeviceDb(cd);
									}
									if (cd.deviceIo != null) {
										cd.iDevice = getDeviceLookup(cd.channelIo, cd.deviceIo).getKey();
										cd.hostIo = getHost(cd.deviceIo, cd.sHost);
										if (cd.hostIo == null) {
											addHostDb(cd);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private static void addChannelDb(CurrentData temp) {
		sql.mod("insert into j_channel (name) values ('" + temp.sChannel + "')");
		System.out.println("channel " + temp.sChannel + " +db");
		sql.query("select top 1 id from j_channel where name = '" + temp.sChannel + "'");
		try {
			if (sql.getResult().next()) {	
				temp.iChannel = sql.getResult().getInt("id");
				temp.channelIo = addChannel(getData(), temp.iChannel, temp.sChannel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static void addDeviceDb(CurrentData temp) {
		sql.mod("insert into j_device (channelfk, name) values (" + temp.iChannel + ", '" + temp.sDevice + "')");
		sql.query("select top 1 id from j_device where channelfk = " + temp.iChannel + " and name = '" + temp.sDevice + "'");
		try {
			if (sql.getResult().next()) {
				temp.iDevice = sql.getResult().getInt("id");
				temp.deviceIo = addDevice(temp.channelIo, temp.iDevice, temp.sDevice);
				System.out.println("Device " + temp.sHost + "." + temp.sChannel + "." + temp.sDevice + " +db");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static Integer addHostDb(CurrentData temp) {
		Integer out = null;
		sql.query("select top 1 id from j_DIO where iofk = " + temp.iHost + " and devicefk = " + temp.iDevice);
		try {
			if (!sql.getResult().next()) {
				sql.mod("insert into j_DIO (iofk, devicefk) values (" + temp.iHost + "," + temp.iDevice + ")");
				System.out.println("Host " + temp.sHost + "." + temp.sDevice + " +db");
			}
			sql.query("select top 1 id from j_DIO where iofk = " + temp.iHost + " and devicefk = " + temp.iDevice);
			if (sql.getResult().next()) {
				out = sql.getResult().getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
	private static void updateRemove() {
		boolean channelBreak = false;
		boolean deviceBreak = false;
		boolean hostBreak = false;
		Integer chCount = null;;
		for (AvailLookup channel : getData().keySet()) {
			cd.clear();
			cd.iChannel = channel.getKey();
			cd.sChannel = channel.getValue();
			cd.aChannel = channel;
			chCount = channelCount(io, cd.sChannel);
			for(AvailLookup device : getData().get(channel).keySet()) {
				cd.iDevice = device.getKey();
				cd.sDevice = device.getValue();
				cd.aDevice = device;
				for (AvailLookup host : getData().get(channel).get(device)) {
					cd.iHost = host.getKey();
					cd.sHost = host.getValue();
					cd.aHost = host;
					cd.channelData = getChannelData(io.get(cd.sHost), cd.sChannel);
					cd.channelIo = getChannel(getData(), cd.iChannel);
					if (cd.channelData == null && chCount <= 0) {
						removeChannelDb(cd);
						channelBreak = true;
					} else { 
						cd.deviceData = getDeviceData(io.get(cd.sHost).get(cd.sChannel), cd.sDevice);
						cd.deviceIo = getDevice(cd.channelIo, cd.iDevice);
						if (cd.deviceData == null && cd.deviceIo.size() <= 1) {
							removeDeviceDb(cd);
							deviceBreak = true;
						} else {
							cd.hostIo = getHost(cd.deviceIo, cd.iHost);
							if (cd.deviceData == null) {
								removeHostDb(cd);
								hostBreak = true;
							}
						}
					}
					if (hostBreak) {
						hostBreak = false;
						break;
					}
				}
				if (deviceBreak) {
					deviceBreak = false;
					break;
				}
			}
			if (channelBreak) {
				channelBreak = false;
				break;
			}
		}
	}
	private static void removeChannelDb(CurrentData temp) {
		System.out.println("Channel " + temp.iChannel + " -db");
		removeChannel(getData(), temp.aChannel);
		sql.mod("delete j_dio"
				+ " from j_dio"
				+ " left join j_device on j_DIO.devicefk = j_device.id"
				+ " where j_device.channelfk = " + temp.iChannel);
		sql.mod("delete j_device"
				+ " from j_device"
				+ " where j_device.channelfk = " + temp.iChannel);
		sql.mod("delete j_channel"
				+ " from j_channel"
				+ " where j_channel.id = " + temp.iChannel);
	}
	private static void removeDeviceDb(CurrentData temp) {
		System.out.println("Device " + temp.iDevice + " -db");
		removeDevice(temp.channelIo, temp.aDevice);
		sql.mod("delete j_dio"
				+ " from j_device"
				+ " left join j_dio on j_device.id = j_dio.devicefk"
				+ " where j_device.id = " + temp.iDevice);
		sql.mod("delete j_device"
				+ " from j_device"
				+ " where j_device.id = " + temp.iDevice);
	}
	private static void removeHostDb(CurrentData temp) {
		System.out.println("Host " + temp.iHost + " -db");
		removeHost(temp.deviceIo, temp.aHost);
		sql.mod("delete j_dio"
				+ " from j_dio"
				+ " where iofk = " + cd.iHost + " and devicefk = " + cd.iDevice);
	}
	private static ArrayList<String> getChannelData(HashMap<String, ArrayList<String>> arrayList, String key) {
		ArrayList<String> out = null; 
		if (arrayList != null ) {
			for (String aKey : arrayList.keySet()) {
				if (aKey != null) {
					if (aKey.equalsIgnoreCase(key)) {
						out = arrayList.get(aKey);
						System.out.println("Channel Data: " + key + " found");
						break;
					}
				}
			}
			if (out == null) {
				System.out.println("Channel Data: " + key + " not found");
			}
		}
		return out;
	}
	private static String getDeviceData(ArrayList<String> arrayList, String key) {
		String out = null; 
		if (arrayList != null ) {
			for (String aKey : arrayList) {
				if (aKey != null) {
					if (aKey.equalsIgnoreCase(key)) {
						out = key;
						System.out.println("Device Data: " + key + " found");
						break;
					}
				}
			}
			if (out == null) {
				System.out.println("Device Data: " + key + " not found");
			}
		}
		return out;
	}
	private static boolean notSystem(String in) {
		boolean result = true;
		if (in != null) {
			if (in.charAt(0) == '_') {
				result = false;
			}
		}
		return result;
	}
	private static Integer channelCount(HashMap<String, HashMap<String, ArrayList<String>>> inArray, String channel) {
		Integer out = 0;
		for (String channels : inArray.keySet()) {
			if (inArray.get(channels).get(channel) != null) {
				out++;
			}
		}
		return out;
	}
	private static void addLookup(HashMap<Integer, String> array, Integer i, String s) {
		if (array.get(i) == null) {
			array.put(i, s);
		}
	}
	private static void removeLookup(HashMap<Integer, String> array, Integer i) {
		if (array.get(i) != null) {
			array.remove(i);
		}
	}
	/*public static AliasLookup getAlias(String inAlias) {
		AliasLookup alias = null;
		for (Integer iAlias : aliases.keySet()) {
			alias = aliases.get(iAlias);
			if (alias.alias.equalsIgnoreCase(inAlias)) {
				System.out.println("Alias: alias " + inAlias + " found");
				break;
			}
		}
		return alias;
	}
	public static AliasLookup getChannelAlias(Integer channel) {
		AliasLookup alias = null;
		for (Integer iAlias : aliases.keySet()) {
			alias = aliases.get(iAlias);
			if (alias.channel == channel) {
				System.out.println("Alias: channel " + channel + " found");
				break;
			}
		}
		return alias;
	}
	public static AliasLookup getDeviceAlias(Integer device) {
		AliasLookup alias = null;
		for (Integer iAlias : aliases.keySet()) {
			alias = aliases.get(iAlias);
			if (alias.device == device) {
				System.out.println("Alias: Device " + device + " found");
				break;
			}
		}
		return alias;
	}*/
}