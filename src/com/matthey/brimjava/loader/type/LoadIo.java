package com.matthey.brimjava.loader.type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jinterop.dcom.core.JIVariant;

import com.matthey.brimjava.event.util.GenericEvent;
import com.matthey.brimjava.io.Data;
import com.matthey.brimjava.io.Io;
import com.matthey.brimjava.io.ServerIo;
import com.matthey.brimjava.io.StructureEvent;
import com.matthey.brimjava.loader.util.AddData;
import com.matthey.brimjava.loader.util.AvailLookup;
import com.matthey.brimjava.loader.util.Availability;
import com.matthey.brimjava.loader.util.EventGroups;
import com.matthey.brimjava.loader.util.GetEvents;
import com.matthey.brimjava.loader.util.InfoDevice;
import com.matthey.brimjava.loader.util.InfoItem;
import com.matthey.brimjava.sql.util.QueryBuilder;
import com.matthey.brimjava.sql.util.Sql;

public class LoadIo {
	//TODO: AL: close server gracefully
	private static ConcurrentHashMap<Integer, ServerIo> server = new ConcurrentHashMap<Integer, ServerIo>();
	public static void loadAll() {
		Sql obj = new Sql("plantavail");
		obj.query("select j_serverio.id as serverid, j_serverIo.name as servername, j_io.id as ioid, j_io.host as hostname"
				+ " from j_io"
				+ " join j_serverIo on j_Io.serverIoFk = j_serverio.id");
		try {
			while( obj.getResult().next() ) {
				if	(server.get((obj.getResult().getInt("serverid"))) == null) {
					server.put(obj.getResult().getInt("serverid"), new ServerIo(obj.getResult().getString("servername")));
					System.out.println("added new server group");
				}
				if (server.get((obj.getResult().getInt("serverid"))).findServer(obj.getResult().getString("hostname")) == null) {
					server.get((obj.getResult().getInt("serverid"))).addServer(obj.getResult().getInt("ioid"), obj.getResult().getString("hostname"));
					System.out.println("added new server");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		obj.close();
		Availability.loadAll();
		LoadData.loadAll();
	}
	public static ConcurrentHashMap<Integer, ServerIo> getServer() {
		return server;
	}
	private static ServerIo getDevice(Data data) {
		ServerIo out = null;
		ServerIo servers = null;
		ArrayList<AvailLookup> hosts = Availability.getDeviceWithoutChannel(data);
		for (AvailLookup host : hosts) {
			for (Integer iServerIo : getServer().keySet()) {
				servers = getServer().get(iServerIo);
				for (Integer iServer : servers.getServers().keySet()) {
					if (servers.getServers().get(iServer).getHost().equalsIgnoreCase(host.getValue())) {
						out = servers;
					}
				}
			}
		}
		return out;
	}
	public static JIVariant readIo(Integer device, String address) {
		JIVariant value = null;
		Data data = new Data(device, address);
		ServerIo io = getDevice(data);
		if (io != null) {
			value = io.readIo(data);
		}
		return value;
	}
	public static void writeIo(Integer device, String address, JIVariant value) {
		Data data = new Data(device, address);
		ServerIo io = getDevice(data);
		if (io != null) {
			io.writeIo(device, data.getAddressPath(), value);
		}
	}
	private static Integer getHost(Integer serverId, String hostName) {
		Integer out = null;
		for (Integer i : server.get(serverId).getServers().keySet()) {
			if (server.get(serverId).getServers().get(i).getHost().equalsIgnoreCase(hostName)) {
				out = i;
				break;
			}
		}
		return out;
	}
	private static Integer getServer(String serverName) {
		Integer out = null;
		for (Integer i : server.keySet()) {
			if (server.get(i).getGroup().equalsIgnoreCase(serverName)) {
				out = i;
				break;
			}
		}
		return out;
	}
	public static void add(String serverName, String hostName) {
		Sql obj = new Sql("plantavail");
		Integer serverId = addServer(obj, serverName);
		if (serverId != null) {
			Integer hostIo = getHost(serverId, hostName);
			if (hostIo == null) {
				obj.mod("insert into J_Io (host, serveriofk) values ('" + hostName + "', " + serverId + ")");
				obj.query("select top 1 id from j_io where host = '" + hostName + "'");
				try {
					obj.getResult().next();
					hostIo = obj.getResult().getInt("id");
					server.get(serverId).addServer(hostIo, hostName);
				} catch (SQLException e) {
					System.out.println("SQL Error");
				}
			}
		}
		obj.close();
	}
	private static Integer addServer(Sql obj, String serverName) {
		Integer serverId = getServer(serverName);
		if (serverId == null) {
			obj.mod("insert into J_ServerIo (name) values ('" + serverName + "')");
			obj.query("select top 1 id from J_serverio where name = '" + serverName + "'");
			try {
				obj.getResult().next();
				serverId = obj.getResult().getInt("id");
				server.put(serverId, new ServerIo(serverName));
			} catch (SQLException e) {
				System.out.println("SQL Error");
			}
		}
		return serverId;
	}
	public static void remove(String serverName, String hostName) {
		Sql obj = new Sql("plantavail");
		Integer serverId = getServer(serverName);
		if (serverId != null) {
			Integer hostId = getHost(serverId, hostName);
			if (hostId != null) {
				cleanup(obj, hostId);
				server.get(serverId).delServer(hostId);
			}
			if (server.get(serverId).getServers().size() <= 0) {
				removeServer(obj, serverName);
			}
		}
		obj.close();
	}
	private static void cleanup(Sql sql, Integer hostId) {
		sql.query("select j_data.devicefk, J_data.address "
				+ "from j_data "
				+ "join j_dio on j_data.devicefk = j_dio.devicefk "
				+ "where iofk = " + hostId);
		Sql newSql = new Sql("plantavail");
		try {
			while(sql.getResult().next()) {
				LoadData.cleanup(newSql, sql.getResult().getInt("devicefk"), sql.getResult().getString("address"));
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		newSql.close();
		sql.mod("delete from j_io where id = " + hostId);
	}
	private static void removeServer(Sql obj, String serverName) {
		Integer serverId = getServer(serverName);
		if (serverId != null) {
			//obj.mod("delete from j_serverio where id = " + serverId);
			server.get(serverId).close();
			server.remove(serverId);
		}
	}
	// TODO: AL: test this after Io.rename() works
	public static void update(String oldServerName, String newServerName, String oldHostName, String newHostName) {
		Sql obj = new Sql("plantavail");
		Integer serverId = updateServer(obj, oldServerName, newServerName);
		if (!oldHostName.equalsIgnoreCase(newHostName)) {
			Integer hostId = getHost(serverId, oldHostName);
			if (hostId != null) {
				obj.mod("update j_io set host = '" + newHostName + "' where id = " + hostId);
				server.get(serverId).getServers().get(hostId).rename(newHostName);
			}
		}
		obj.close();
	}
	private static Integer updateServer(Sql obj, String oldServerName, String newServerName) {
		Integer serverId = null;
		if (!oldServerName.equalsIgnoreCase(newServerName)) {
			serverId = getServer(oldServerName);
			if (serverId != null) {
				obj.mod("update j_serverIo set name = '" + newServerName + "' where id = " + serverId);
				server.get(serverId).rename(newServerName);
			}
		}
		return serverId;
	}
	public static class LoadData {
		// just add the data to the correct pool, with an event to stop it from dropping out
		// then remove the event to cause the data to be dropped over time.
		private static String baseQuery = "select distinct j_io.serveriofk as serverio, "
				+ " j_channel.id as channelid, j_device.id as deviceid, j_data.address, j_data.id as dataid "
				+ " from j_data "
				+ " join j_dio on j_data.devicefk = j_dio.devicefk "
				+ " join j_io on j_io.id = j_dio.iofk "
				+ " join j_device on j_device.id = j_data.devicefk "
				+ " join j_channel on j_channel.id = j_device.channelfk "
				+ " join j_serverio on j_io.serveriofk = j_serverIo.id ";
		protected static void loadAll() {
			loadAll(baseQuery);
		}
		protected static void loadAll(Integer devicefk) {
			loadAll(baseQuery + "where devicefk = " + devicefk);
		}
		protected static void loadAll(Integer[] devicefk) {
			QueryBuilder qb = new QueryBuilder(" or ", baseQuery + "where");
			for (Integer i : devicefk) {
				qb.append("devicefk = " + i);
			}
			loadAll(qb.get());
		}
		private static void loadAll(String query) {
			Sql sql = new Sql("plantavail");
			sql.query(query);
			try {
				while(sql.getResult().next()) {
					add(sql.getResult().getInt("serverio"), sql.getResult().getInt("deviceid"), sql.getResult().getString("address"), sql.getResult().getInt("dataId"));
				}
			} catch (SQLException e) {
				System.out.println("SQL Error");
			}
			sql.close();
			LoadStructure.loadAll();
		}
		private static AvailLookup getLookupDevice(InfoDevice info, Integer deviceId) {
			AvailLookup out = null;
			if (info.getChannel() != null) {
				for (AvailLookup aDevice : info.getChannel().keySet()) {
					if (aDevice.getKey() == deviceId) {
						out = aDevice;
						break;
					}
				}
			}
			return out;
		}
		protected static InfoDevice getDevice(Integer deviceId) {
			InfoDevice info = new InfoDevice();
			for (AvailLookup aChannel : Availability.getData().keySet()) {				
				info.setChannel(Availability.getChannel(Availability.getData(), aChannel.getKey()));
				if (info.getChannel() != null) {
					info.setLookupDevice(getLookupDevice(info, deviceId));
					info.setLookupChannel(aChannel);
					info.setDevice(Availability.getDevice(info.getChannel(), deviceId));
					if (info.getDevice() != null) {
						break;
					}
				}
			};
			return info;
		}
		protected static InfoItem get(Integer serverId, Integer deviceId, String address) {
			ServerIo aServer = server.get(serverId);
			InfoItem out = new InfoItem();
			if (aServer != null) {
				out.setInfo(getDevice(deviceId));
				if (out.getInfo().getDevice() != null) {
					out.setHosts(out.getInfo().getDevice());
					for (AvailLookup aHost : out.getInfo().getDevice()) {
						Io aIo = aServer.getServers().get(aHost.getKey());
						if (aIo != null) {
							out.setIo(aIo);
							if (aIo.getStructure().getPlcs().get(deviceId) != null) {
								Map<String, Data> aAddress = aIo.getStructure().searchPlcItem(address);
								if (aAddress != null) {
									out.setAddress(aAddress.get(address));
								}
							}
							break;
						}
					}
				}
			}
			return out;
		}
		public static void add(Integer serverId, Integer deviceId, String address, boolean db) {
			addLogic(new AddData(serverId, deviceId, address, db));
		}
		public static void add(Integer serverId, Integer deviceId, String address, Integer id) {
			addLogic(new AddData(serverId, deviceId, address, id));
		}
		private static void addLogic(AddData add) {
			InfoItem itemInfo = get(add.getServerId(), add.getDeviceId(), add.getAddress());
			if (itemInfo.ioTrue()) {
				if (itemInfo.getIo().getStructure().getPlcs().get(add.getDeviceId()) == null) {
					itemInfo.getIo().getStructure().newPlc(add.getDeviceId());
				};
				Data data = new Data(add.getDeviceId(), add.getAddress(), itemInfo.getIo().getStructure());
				if (add.getDatabase()) {
					if (!itemInfo.addressTrue()) {
						Sql sql = new Sql("plantavail");
						sql.mod("insert into j_data (devicefk, address) values (" + add.getDeviceId() + ", '" + add.getAddress() + "')");
						sql.query("select id from j_data where devicefk =" + add.getDeviceId() + " and address = '" + add.getAddress() + "'");
						try {
							while(sql.getResult().next()) {
								data.setId(sql.getResult().getInt("id"));
							}
						} catch (SQLException e) {
							System.out.println("SQL Error");
						}
						sql.close();
					}
				} else if (add.getId() != null) {
					data.setId(add.getId());
				}
				itemInfo.getIo().add(data);
			}
		}
		public static void remove(Integer serverId, Integer deviceId, String address) {
			InfoItem itemInfo = get(serverId, deviceId, address);
			if (itemInfo.addressTrue()) {
				itemInfo.getAddress().getEvents().removeAll(itemInfo.getAddress().getEvents());
				Sql sql = new Sql("plantavail");
				cleanup(sql, deviceId, address);
				sql.close();
			}
		}
		public static void cleanup(Sql sql, Integer deviceId, String address) {
			sql.query("select id from J_data where devicefk = " + deviceId + " and address = '" + address + "'");
			Sql newSql = new Sql("plantavail");
			try {
				while(sql.getResult().next()) {
					LoadStructure.cleanup(newSql, sql.getResult().getInt("id"));
				}
			} catch (SQLException e) {
				System.out.println("SQL Error");
			}
			newSql.close();
			sql.mod("delete j_data where devicefk = " + deviceId + " and address = '" + address + "'");
		}
		public static void update(Integer oldServerId, Integer oldDeviceId, String oldAddress, Integer newServerId, Integer newDeviceId, String newAddress) {
			// TODO: AL: update method, is this necessary? unable to rename currently.
			InfoItem itemInfo = get(oldServerId, oldDeviceId, oldAddress);
			if (itemInfo.addressTrue()) {
				ArrayList<StructureEvent> newEvents = new ArrayList<StructureEvent>();  
				newEvents.addAll(itemInfo.getAddress().getEvents());
				add(newServerId, newDeviceId, newAddress, itemInfo.getAddress().getId());
				// TODO: AL: move newEvents to adds data
				Sql sql = new Sql("plantavail");
				sql.mod("update j_data set devicefk = " + newDeviceId + ", address = '" + newAddress + "' where devicefk = " + newDeviceId + " and address = '" + newAddress + "'");
				sql.close();
			}
		}
		public static class LoadStructure {
			private static String baseQuery = "select j_structureEvent.id, j_structureEvent.datafk, j_structureEvent.condition, j_structureEvent.operator"
					+ " from j_structureEvent "
					+ " join j_data on j_data.id = j_structureevent.datafk ";
			protected static void loadAll() {
				loadAll(baseQuery);
			}
			protected static void loadAll(Integer devicefk) {
				loadAll(baseQuery + "where devicefk = " + devicefk);
			}
			protected static void loadAll(Integer[] devicefk) {
				QueryBuilder qb = new QueryBuilder(" or ", baseQuery + "where");
				for (Integer i : devicefk) {
					qb.append("devicefk = " + i);
				}
				loadAll(qb.get());
			}
			private static void loadAll(String query) {
				EventGroups events = GetEvents.byType("Io");
				Sql sql = new Sql("plantavail");
				sql.query(query);
				StructureEvent struct = null;
				Integer dataId = null;
				HashMap<Integer, ArrayList<StructureEvent>> dataMap = new HashMap<Integer, ArrayList<StructureEvent>>();
				try {
					while(sql.getResult().next()) {
						dataId = sql.getResult().getInt("datafk");
						struct = new StructureEvent(sql.getResult().getInt("id"), sql.getResult().getString("condition"), sql.getResult().getString("operator"));
						if (dataMap.get(dataId) == null) {
							dataMap.put(dataId, new ArrayList<StructureEvent>());
						}
						dataMap.get(dataId).add(struct);
					}
				} catch (SQLException e) {
					System.out.println("SQL Error");
				}
				sql.close();
				HashMap<Integer, Io> serverIo = null;
				Data foundData = null;
				for (Integer aServer : server.keySet()) {
					serverIo = server.get(aServer).getServers();
					for (Integer aIo : serverIo.keySet()) {
						for (Integer dataKey : dataMap.keySet()) {
							foundData = serverIo.get(aIo).getStructure().findItemById(dataKey);
							if (foundData != null) {
								for (StructureEvent insStruct : dataMap.get(dataKey)) {
									foundData.addEvent(insStruct);
									System.out.println("Structure Event: " + insStruct.getId() + " Added!");
									ArrayList<GenericEvent> eventList = events.getEvent(insStruct.getId());
									if (eventList != null) {
										insStruct.getEvents().addEvent(eventList);
										System.out.println("EventGroups: " + insStruct.getId() + " events added!");
									}
								}
							}
						}
					}
				}
			}
			private static Data get(Integer dataId) {
				HashMap<Integer, Io> io = null;
				Data out = null;
				for (Integer aServer : server.keySet()) {
					io = server.get(aServer).getServers();
					for (Integer aData : io.keySet()) {
						out = io.get(aData).getStructure().findItemById(dataId);
						if (out != null) {
							break;
						}
					}
					if (out != null) {
						break;
					}
				}
				return out;
			}
			private static Data get(Integer serverId, Integer deviceId, String address) {
				InfoItem result = LoadData.get(serverId, deviceId, address);
				Data out = null;
				if (result != null) {
					out = result.getAddress();
				}
				return out;
			}
			public static void add(Integer dataId, String condition, String operator) {
				Data data = get(dataId);
				addLogic(data, condition, operator);
			}
			public static void add(Integer serverId, Integer deviceId, String address, String condition, String operator) {
				Data data = get(serverId, deviceId, address);
				addLogic(data, condition, operator);
			}
			private static void addLogic(Data data, String condition, String operator) {
				if (data != null) {
					Integer eventId = data.getEvent(condition, operator);					
					if (eventId == null) {
						StructureEvent struct = new StructureEvent(condition, operator);
						data.addEvent(struct);
						Sql sql = new Sql("plantavail");
						sql.mod("insert into J_StructureEvent (datafk, operator, condition) values (" + data.getId() + ", '" + operator + "', '" + condition + "')");
						sql.query("select top 1 id from J_StructureEvent where datafk = " + data.getId() + " and operator = '" + operator + "' and condition = '" + condition + "'");
						try {
							sql.getResult().next();
							struct.setId(sql.getResult().getInt("id"));
						} catch (SQLException e) {
							System.out.println("SQL Error");
						}
						sql.close();
						System.out.println("Structure Event: " + struct.getId() + " Added!");
					} else {
						System.out.println("Structure Event: " + eventId + " Found!");
					}
				} else {
					System.out.println("data not found!");
				}
			}
			public static void remove(Integer dataId, Integer id) {
				Data data = get(dataId);
				for (StructureEvent aStruct : data.getEvents()) {
					if (aStruct.getId() == id) {
						Sql sql = new Sql("plantavail");
						cleanup(sql, id, data);
						data.removeEvent(id);
						sql.close();
						break;
					}
				}
			}
			private static void cleanup(Sql sql, Integer id) {
				Data data = get(id);
				cleanup(sql, id, data);
			}
			private static void cleanup(Sql sql, Integer id, Data data) {
				if (data != null) {
					for (StructureEvent struct : data.getEvents()) {
						struct.cleanupSelf(sql);
					}
				}
				sql.mod("delete from J_StructureEvent where datafk = " + id);
			}
			public static void update(Integer dataId, Integer id, String condition, String operator) {
				Data data = get(dataId);
				StructureEvent update = null;
				for (StructureEvent aStruct : data.getEvents()) {
					if (aStruct.getId() == id) {
						update = aStruct;
						break;
					}
				}
				if (update != null) {
					update.setCondition(condition);
					update.setCondition(operator);
					Sql sql = new Sql("plantavail");
					sql.mod("update J_StructureEvent set condition = '" + condition + "', operator = '" + operator + "' where id = " + id);
					sql.close();
				}
			}	
		}
	}
}
