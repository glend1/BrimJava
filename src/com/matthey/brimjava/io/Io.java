package com.matthey.brimjava.io;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.da.OPCSERVERSTATE;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.opc.lib.da.browser.Branch;
 
 public class Io {
	private Server server = null;
	private AccessBase access = null;
	private String host = null;
	private Group group = null;
	private ServerIo parent = null;
	private StructureServer plc = new StructureServer(this);
	private Collection<Branch> opcNames = null; 
	private void constructor(String host) {
		final ConnectionInformation ci = new ConnectionInformation();
		ci.setHost(host);
		ci.setDomain("brimcontrols.local");
		ci.setUser("admin");
		ci.setPassword("!Wozniu2");
		ci.setClsid("680DFBF7-C92D-484D-84BE-06DC3DECCD68"); // if ProgId is not working, try it using the Clsid instead
		this.host = host;
		//ci.setProgId("SWToolbox.TOPServer.V5");
		server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
		try {
			connect();
			access = new SyncAccess(server, 1000);
			group = server.addGroup("group");
			access.bind();
		} catch (Exception e) {
			System.out.println("Io.Constructor could not connect to " + host);
			close();
		}		
	}
	public Io(String host) {
		constructor(host);
	}
	protected Io(String host, ServerIo parent) {
		setParent(parent);
		constructor(host);
	}
	public void connect() {
        try {
			server.connect();
			System.out.println("Io.Connect " + host + " connected");
        } catch (AlreadyConnectedException e) {
        	System.out.println("Io.Connect already connected");
		} catch (Exception e) {
			System.out.println("Io.Connect cannot connect");
		}
	}
	public boolean getStatus() {
		if (server.getServerState() != null) {
			if (server.getServerState().getServerState().equals(OPCSERVERSTATE.OPC_STATUS_RUNNING)) {
				return true;
			}
		}
		return false;
	}
	public Server getServer() {
		return server;
	}
	public void rename(String newHostName) {
		// TODO: AL : cannot rename unless completely recreate the object 
		this.host = newHostName;
	}
	public StructureServer getStructure() {
		return plc;
	}
	public AccessBase getSync() {
		return access;
	}
	public Group getGroup() {
		return group;
	}
	public String getHost() {
		return host;
	}
    public void add(Data data) {
    	try {
    		Map<String, Data> result = plc.searchPlcItem(data.getAddress());
    		if (result == null) {
    			group.addItem(data.getAddressPath());
    			plc.addItem(data);
    			access.addItem(data.getAddressPath(), new DataCallback() {
    				@Override
    				public void changed(Item addrRef, ItemState state) {
    					data.setData(state);
    					if (data.getEvents().isEmpty()) {
	    					data.increment();
	    					if (data.getCurrentTimeout() >= data.getTimeout()) {
	    						delete(data);
	    					}
    					}
    					data.callThem();
    				}
    			});
    			System.out.println("Io.Add " + data.getAddress() + " added");
    		} else {
    			System.out.println("Io.Add " + data.getAddress() + " already exists");
    		};
    	} catch (Exception e) {
    		e.printStackTrace();
    	}    	
    }
    public void delete(Data data) {
    	plc.removeItem(data.getAddress());
    	access.removeItem(data.getAddressPath());
    }
    public void write(String address, JIVariant val) {  
		try {
			group.addItem(address).write(val);
			System.out.println(val + " written to " + address);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	public boolean close() {
		if (server.getServerState() == null) {
			System.out.println("Io.Close already closed, or cannot close " + host);
			return false;
		}
		try {
			access.unbind();
			server.disconnect();				
			System.out.println("Io.Close disconnected " + host);
		} catch (JIException e) {
			e.printStackTrace();
		}
		return true;
	}
	public ServerIo getParent() {
		return parent;
	}
	private void setParent(ServerIo parent) {
		this.parent = parent;
	}
	public Collection<Branch> getOpcNames() {
		return opcNames;
	}
	public void setOpcNames(Collection<Branch> opcNames) {
		this.opcNames = opcNames;
	}
}