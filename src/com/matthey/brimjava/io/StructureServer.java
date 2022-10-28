package com.matthey.brimjava.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StructureServer {
	private Map<Integer, Map<String, Data>> plcs = new HashMap<Integer, Map<String, Data>>();
	private Io parent = null;
	public StructureServer(Io parent) {
		setParent(parent);
	}
	public Map<String, Data> searchPlc(Integer device) {
		Map<String, Data> output = null;
		if (plcs.containsKey(device)) {
			output = plcs.get(device);
		}
		return output;
	}
	public Map<String, Data> searchPlcItem(String item) {
		Map<String, Data> output = null;
		Set<Integer> iter = plcs.keySet();
		for (Integer plc : iter) {
			if (plcs.get(plc).containsKey(item)) {
				output = plcs.get(plc);
			}
		}
		return output;
	}
	public Data searchItem(Map<String, Data> plc, String item) {
		Data output = null;
		if (plc != null) {
			if (plc.containsKey(item)) {
				output = plc.get(item);
			}
		}
		return output;
	}
	public Map<Integer, Map<String, Data>> getPlcs() {
		return plcs;
	}
	public Map<String, Data> newPlc(Integer device) {
		Map<String, Data> output = null;
		if (searchPlc(device) == null) {
			output = plcs.put(device, new HashMap<String, Data>());
			System.out.println("ServerStructure.NewPlc " + device + " added");
		} else {
			System.out.println("ServerStructure.NewPlc " + device + " already in use");
		}
		return output;
	}	
	public Data newItem(Integer device, String item, Integer id) {
		Map<String, Data> result = searchPlc(device);
		Data data = null;
		if (result == null) {
			result = newPlc(device);
		}
		if (searchItem(result, item) == null) {				
			data = new Data(device, item, this, id);
			plcs.get(device).put(item, data);
		} else {
			System.out.println("ServerStructure.NewItem " + item + " already in use");
		}
		return data;
	}
	public void addItem(Data data) {
		//TODO: AL: make channel a requirement
		Map<String, Data> result = searchPlc(data.getDevice());
		if (result == null) {
			result = newPlc(data.getDevice());
		}
		if (searchItem(result, data.getAddress()) == null) {
			data.setParent(this);
			plcs.get(data.getDevice()).put(data.getAddress(), data);
		} else {
			System.out.println("ServerStructure.AddItem " + data.getAddress() + " already in use");
		}
	}
	public void removePlc(Integer device) {
		if (searchPlc(device) != null) {
			plcs.remove(device);
			System.out.println("ServerStructure.RemovePlc " + device + " removed");
		} else {
			System.out.println("ServerStructure.RemovePlc " + device + " not in use");
		}
	}
	public void removeItem(String item) {
		Map<String, Data> result = searchPlcItem(item);
		Integer device = result.get(item).getDevice();
		if (searchPlcItem(item) != null) {
			result.remove(item);
			System.out.println("ServerStructure.RemoveItem.1 " + item + " removed");
			if (result.isEmpty()) {
				removePlc(device);
			}
		} else {
			System.out.println("ServerStructure.RemoveItem.1 " + item + " not in use");
		}
	}
	public void removeItem(Integer device, String item) {
		Map<String, Data> result = searchPlc(device);
		if (result != null) {
			removeItem(result, item);
		} else {
			System.out.println("ServerStrucutre.RemoveItem.2 " + device + " not in use");
		}
	}
	public void removeItem(Map<String, Data> plc, String item) {
		Data result = searchItem(plc,item);
		Integer device = result.getDevice();
		if (result != null) {
			plc.remove(result);
			System.out.println("ServerStructure.RemoveItem.3 " + item + " removed");
			if (plc.isEmpty()) {
				removePlc(device);
			}
		}
	}
	public Data findItemById(Integer id) {
		Data out = null;
		for (Integer iPlc : plcs.keySet()) {
			out = findItemById(plcs.get(iPlc), id);
			if (out != null) {
				break;
			}
		}
		return out;
	}
	public Data findItemById(Map<String, Data> map, Integer id) {
		Data out = null;
		Data data = null; 
		if (plcs.containsValue(map)) {
			for (String iAddress : map.keySet()) {
				data = map.get(iAddress);
				if (data.getId() == id) {
					out = data;
					break;
				}
			}
		}
		return out;
	}
	public Io getParent() {
		return parent;
	}
	public void setParent(Io parent) {
		this.parent = parent;
	}
}
