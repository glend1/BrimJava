package com.matthey.brimjava.io;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.core.JIVariant;

import com.matthey.brimjava.loader.util.AvailLookup;
import com.matthey.brimjava.loader.util.Availability;

public class ServerIo implements Runnable {
	private HashMap<Integer, Io> servers = new HashMap<Integer, Io>();
	private Thread threadServers = null;
	private boolean connected = false;
	private String group = null;
	private int iServer = 0;
	private int fServer = 0;
	public ServerIo(String group) {
		this.group = group;
		threadServers = new Thread(this,group);
	}
	public String getGroup() {
		return group;
	}
	public void rename(String newServerName) {
		this.group = newServerName;
		threadServers.setName(newServerName);
	}
	public HashMap<Integer, Io> getServers() {
		return servers;
	}
	public Io addServer(Integer id, String host) {
		Io io = new Io(host, this);
		servers.put(id, io);
		iServer++;
		if (threadServers.getState().equals(Thread.State.NEW)) {
			connected = true;
			threadServers.start();
		}
		if (!io.getStatus()) {
			System.out.println("IoServer.AddServer could not connect to " + host);
		}
		return io;
	}
	/*public void delServer(Integer id, String host) {
		Io deleteServer = null;
		for (Integer iServer: servers.keySet()) {
			if (servers.get(iServer).getHost().equalsIgnoreCase(host)) {
				deleteServer = servers.get(iServer);
				break;
			}
		}
		delServer(deleteServer);
	}
	public void delServer(Io io) {
		if (findServer(io.getHost()) != null) {
			if (io.close()) {
				io.close();
				System.out.println(servers);
				System.out.println(io);
				servers.remove(io);
				System.out.println(servers);
				System.out.println("IoServer.DelServer removed " + io.getHost());
				iServer--;			
			} else {
				System.out.println("IoServer.DelServer could not remove " + io.getHost());
			}
		} else {
			System.out.println("IoServer.DelServer " + io.getHost() + " not found");
		}
	}*/
	public void delServer(Integer i) {
		Io io = findServer(i);
		if (findServer(io.getHost()) != null) {
			io.close();
			servers.remove(i);
			if (servers.get(i) == null) {
				System.out.println("IoServer.DelServer removed " + io.getHost());
			} else {
				System.out.println("IoServer.DelServer " + io.getHost() + " not removed");
			}
			iServer--;			
		} else {
			System.out.println("IoServer.DelServer " + io.getHost() + " not found");
		}
	}
	public Io findServer(String host) {
		Io server = null;
		boolean matchFound = false;
		for (Integer iIo: servers.keySet()) {
			if (servers.get(iIo).getHost().equalsIgnoreCase(host)) {
				server = servers.get(iIo);
				matchFound = true;
				System.out.println("IoServer.FindServer match found for " + host);
			}
		}
		if (!matchFound) {
			System.out.println("IoServer.FindServer no match found for " + host);
		}
		return server;
	}
	public Io findServer(Integer id) {
		return servers.get(id);
	}
	protected Data addItem(Data data) {
		boolean added = false;
		if (data.getChannel() != null) {
			HashMap<AvailLookup, ArrayList<AvailLookup>> channel = Availability.getChannel(Availability.getData(), data.getChannel());
			if (channel != null) {
				ArrayList<AvailLookup> device = Availability.getDevice(channel, data.getDevice());
				if (device != null) {
					for (AvailLookup host : device) {
						for (Integer iServer: servers.keySet()) {
							if (host.getValue().equalsIgnoreCase(servers.get(iServer).getHost())) {
								if (servers.get(iServer).getStatus()) {
									data.setHost(host.getValue());
									servers.get(iServer).add(data);
									System.out.println("IoServer.AddItem " + data.getAddress() + " added to " + servers.get(iServer).getHost());
									added = true;
									break;
								}
							}
							if (added) {
								break;
							}
						}
						if (added) {
							break;
						}
					}
				}
			}
		}
		if (!added) {
			System.out.println("IoServer.AddItem cannot add " + data.getAddress() + " to " + getGroup());
		}
		return data;
	}
	public JIVariant readIo(Data data) {
		JIVariant output = null;
		Data io = null;
		Map<String, Data> deviceArray = addDevice(data.getDevice());
		if (deviceArray != null) {
			io = servers.get(iServer).getStructure().searchItem(deviceArray, data.getAddress());
		}
		if (io == null) {
			io = addItem(data);
		}
		if (io == null) {
			System.out.println("IoServer.ReadIo cannot get io"); 
		} else {
			System.out.println("IoServer.ReadIo " + data.getDevice() + " found in " + data.getDevice());
			io.reset();
			output = io.getValue();			
		}
		return output;
	}
	public void writeIo(Integer device, String address, JIVariant value) {
		Io io = null;
		Map<String, Data> deviceArray = addDevice(device);
		if (deviceArray != null) {
			io = servers.get(iServer);
			io.write(address, value);
		}
	}
	public Map<String, Data> addDevice(Integer device) {
		Map<String, Data> foundPlc = null;
		for (Integer iServer: servers.keySet()) {
			StructureServer struct = servers.get(iServer).getStructure();
			foundPlc =	struct.searchPlc(device);
			if (foundPlc != null) {
				System.out.println("Device found in: " + group + " " + servers.get(iServer).getHost() + " ID:" + device);
				break;
			}
		}
		if (foundPlc == null) {
			for (Integer iServer: servers.keySet()) {
				foundPlc = servers.get(iServer).getStructure().newPlc(device);
				break;
			}
		}
		return foundPlc;
	}
	// TODO: AL: test io failover
	private void moveIo() {
		Io io = null;
		boolean moved = false;
		ArrayList<AvailLookup> device = null;
		Io newIo = null;
		for (Integer iServer: servers.keySet()) {
			if (!servers.get(iServer).getStatus()) {
				for (Integer iDevice: servers.get(iServer).getStructure().getPlcs().keySet()) {
					moved = false;
					device = Availability.getHostCount(iDevice);
					if (device.size() >= 1) {
						for (AvailLookup d : device) {
							newIo = findServer(d.getValue());
							if (newIo != null) {
								for (String address : servers.get(iServer).getStructure().getPlcs().get(d.getKey()).keySet()) {
									moveItem(servers.get(iServer), io, servers.get(iServer).getStructure().getPlcs().get(d.getKey()).get(address));
									moved = true;
								}
							}
							if (moved) {
								break;
							}
						}
					}
					if (moved) {
						break;
					} else {
						System.out.println("IoServer.MoveIo no other available hosts available");
					}
				}
			}
		}					
	}
	protected void moveItem (Io oldServer, Io newServer, Data data) {
		oldServer.getStructure().removeItem(data.getAddress());
		newServer.add(data);
	}
	public boolean close() {
		for (Integer iServer: servers.keySet()) {
			servers.get(iServer).close();				
		}
		if (servers.size() == 0) {
			System.out.println("IoServer.Close all servers closed terminating thread");
			connected = false;
			threadServers.interrupt();
			return true;
		} else {
			System.out.println("IoServer.Close failed to close all servers");
		}
		return false;
	}
	@Override
	public void run() {
		while (connected) {
			try {
				fServer = 0;
				for (Integer iServer: servers.keySet()) {
					try {
						if (!servers.get(iServer).getStatus()) {
							fServer++;
							System.out.println("IoServer.run " + servers.get(iServer).getHost() + " IO server problem");
						}
					} catch (NullPointerException e) {
						servers.get(iServer).connect();
					}
				}
				if (iServer == fServer) {
					System.out.println("IoServer.run all " + group + " servers failed");
				} else if (fServer > 0) {
					System.out.println("IoServer.run " + fServer + " " + group + " server(s) failed. Moving Io");
					moveIo();
				}
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					System.out.println("IoServer.run " + group + " stopped");
					break;
				}
			} catch (ConcurrentModificationException e) {
				System.out.println("IoServer.run " + group + " list in use");
			}
		}
	}
}
