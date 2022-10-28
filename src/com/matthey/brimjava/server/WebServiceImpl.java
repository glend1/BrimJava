package com.matthey.brimjava.server;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jws.WebService;

import com.matthey.brimjava.event.util.TimeManipulation;
import com.matthey.brimjava.loader.events.util.AddressGroupData;
import com.matthey.brimjava.loader.events.util.LoadList;
import com.matthey.brimjava.loader.type.LoadIo;
import com.matthey.brimjava.loader.type.LoadSched;
import com.matthey.brimjava.mail.SendMail;
import com.matthey.brimjava.mail.util.AddressStructure;
import com.matthey.brimjava.sms.Sms;
import com.matthey.brimjava.sql.SqlFileStatic;
import com.matthey.brimjava.util.JIHandle;

@WebService(endpointInterface = "com.matthey.brimjava.server.WebServiceInterface")
public class WebServiceImpl implements WebServiceInterface {
	@Override
	public void loadIoAdd(String serverName, String hostName) {
		LoadIo.add(serverName, hostName);		
	}
	/*@Override
	public void loadIoUpdate(String oldServerName, String newServerName, String oldHostName, String newHostName) {
		LoadIo.update(oldServerName, newServerName, oldHostName, newHostName);		
	}*/
	@Override
	public void loadIoRemove(String serverName, String hostName) {
		LoadIo.remove(serverName, hostName);
	}
	@Override
	public void loadDataAdd(Integer serverId, Integer deviceId, String address, boolean database) {
		LoadIo.LoadData.add(serverId, deviceId, address, database);		
	}
	/*@Override
	public static void loadDataAdd(Integer serverId, Integer deviceId, String address, Integer id) {
		LoadIo.LoadData.add(serverId, deviceId, address, id);		
	}*/
	/*@Override
	public void loadDataUpdate(Integer oldServerId, Integer oldDeviceId, String oldAddress, Integer newServerId, Integer newDeviceId, String newAddress) {
		LoadIo.LoadData.update(oldServerId, oldDeviceId, oldAddress, newServerId, newDeviceId, newAddress);		
	}*/
	@Override
	public void loadDataRemove(Integer serverId, Integer deviceId, String address) {
		LoadIo.LoadData.remove(serverId, deviceId, address);		
	}
	/*@Override
	public void loadStructureAdd(Integer dataId, String condition, String operator) {
		LoadIo.LoadData.LoadStructure.add(dataId, condition, operator);		
	}*/
	@Override
	public void loadStructureAdd(Integer serverId, Integer deviceId, String address, String condition, String operator) {
		LoadIo.LoadData.LoadStructure.add(serverId, deviceId, address, condition, operator);		
	}
	@Override
	public void loadStructureRemove(Integer dataId, Integer id) {
		LoadIo.LoadData.LoadStructure.remove(dataId, id);		
	}
	/*@Override
	public void loadStructureUpdate(Integer dataId, Integer id, String condition, String operator) {
		LoadIo.LoadData.LoadStructure.update(dataId, id, condition, operator);		
	}*/
	@Override
	public void loadSchedAdd(String name, Long run, Long interval) {
		LoadSched.add(name, run, interval);		
	}
	@Override
	public void loadSchedRemove(String name) {
		LoadSched.remove(name);		
	}
	@Override
	public void loadSchedUpdate(String oldName, String newName, Long interval, Long timestamp) {
		LoadSched.update(oldName, newName, interval, timestamp);		
	}
	@Override 
	public void ioEmailAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String subject, String html, Integer group) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).emailAdd(subject, html, group);		
	}
	@Override 
	public void ioEmailRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).emailRemove(id);		
	}
	@Override 
	public void ioEmailUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer addressGroup, String subject, String html) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).emailUpdate(id, addressGroup, subject, html);		
	}
	@Override 
	public void ioSmsAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String message, Integer group) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).smsAdd(message, group);		
	}
	@Override 
	public void ioSmsRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).smsRemove(id);		
	}
	@Override 
	public void ioSmsUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer addressGroup, String message) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).smsUpdate(id, addressGroup, message);		
	}
	@Override 
	public void ioSqlAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String tableCol, String query, String path, HashMap<Integer, Integer> duration) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlAdd(tableCol, query, path, duration);		
	}
	@Override 
	public void ioSqlRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlRemove(id);		
	}
	@Override 
	public void ioSqlUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, String tableCol, String query, String path) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlUpdate(id, tableCol, query, path);		
	}
	@Override 
	public void ioSqlDurationAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, HashMap<Integer, Integer> duration) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlDurationAdd(id, duration);		
	}
	@Override 
	public void ioSqlDurationUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer type, Integer value) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlDurationUpdate(id, type, value);		
	}
	@Override 
	public void ioSqlDurationRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id) {
		LoadList.getFromIo(serverId, deviceId, itemId, condition, operator).sqlDurationRemove(id);		
	}
	@Override 
	public void schedEmailAdd(String name, String subject, String html, Integer group) {
		LoadList.getFromSched(name).emailAdd(subject, html, group);
	}
	@Override 
	public void schedEmailRemove(String name, Integer id) {
		LoadList.getFromSched(name).emailRemove(id);
	}
	@Override 
	public void schedEmailUpdate(String name, Integer id, Integer addressGroup, String subject, String html) {
		LoadList.getFromSched(name).emailUpdate(id, addressGroup, subject, html);
	}
	@Override 
	public void schedSmsAdd(String name, String message, Integer group) {
		LoadList.getFromSched(name).smsAdd(message, group);
	}
	@Override 
	public void schedSmsRemove(String name, Integer id) {
		LoadList.getFromSched(name).smsRemove(id);
	}
	@Override 
	public void schedSmsUpdate(String name, Integer id, Integer addressGroup, String message) {
		LoadList.getFromSched(name).smsUpdate(id, addressGroup, message);
	}
	@Override 
	public void schedSqlAdd(String name, String tableCol, String query, String path, HashMap<Integer, Integer> duration) {
		LoadList.getFromSched(name).sqlAdd(tableCol, query, path, duration);
	}
	@Override 
	public void schedSqlRemove(String name, Integer id) {
		LoadList.getFromSched(name).sqlRemove(id);
	}
	@Override 
	public void schedSqlUpdate(String name, Integer id, String tableCol, String query, String path) {
		LoadList.getFromSched(name).sqlUpdate(id, tableCol, query, path);
	}
	@Override 
	public void schedSqlDurationAdd(String name, Integer id, HashMap<Integer, Integer> duration) {
		LoadList.getFromSched(name).sqlDurationAdd(id, duration);
	}
	@Override 
	public void schedSqlDurationUpdate(String name, Integer id, Integer type, Integer value) {
		LoadList.getFromSched(name).sqlDurationUpdate(id, type, value);
	}
	@Override 
	public void schedSqlDurationRemove(String name, Integer id) {
		LoadList.getFromSched(name).sqlDurationRemove(id);
	}
	@Override
	public void addressGroupAddUser(Integer id, Integer user) {
		AddressGroupData.addUser(id, user);		
	}
	@Override
	public void addressGroupAddGroup(Integer id, Integer group) {
		AddressGroupData.addGroup(id, group);		
	}
	@Override
	public void addressGroupRemove(Integer id) {
		AddressGroupData.remove(id);		
	}
	@Override
	public void addressGroupUpdateUser(Integer id, Integer addressgroup, Integer user) {
		AddressGroupData.updateUser(id, addressgroup, user);		
	}
	@Override
	public void addressGroupUpdateGroup(Integer id, Integer addressgroup, Integer group) {
		AddressGroupData.updateGroup(id, addressgroup, group);		
	}
	@Override
	public void smsSend(ArrayList<String> to, String msg) {
		Sms.add(to, msg);		
	}
	@Override
	public void sqlFileStatic(String table, String query, String path, String filename, TimeManipulation durations) {
		SqlFileStatic.print(table, query, path, filename, durations);		
	}
	@Override
	public void sendMail(AddressStructure addresses, String subject, String html) {
		SendMail.send(addresses, subject, html);		
	}
	@Override
	public Object readIo(Integer device, String address) {
		return JIHandle.asObject(LoadIo.readIo(device, address));
	}
	@Override
	public void writeIo(Integer device, String address, Object value) {
		LoadIo.writeIo(device, address, JIHandle.asVariant(value));		
	}
}
