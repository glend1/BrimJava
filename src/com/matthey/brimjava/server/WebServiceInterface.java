package com.matthey.brimjava.server;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.matthey.brimjava.event.util.TimeManipulation;
import com.matthey.brimjava.mail.util.AddressStructure;

@WebService
public interface WebServiceInterface {
		@WebMethod abstract void loadIoAdd(String serverName, String hostName);
		//@WebMethod void loadIoUpdate(String oldServerName, String newServerName, String oldHostName, String newHostName);
		@WebMethod void loadIoRemove(String serverName, String hostName);
		@WebMethod void loadDataAdd(Integer serverId, Integer deviceId, String address, boolean database);
		//@WebMethod void loadDataAdd(Integer serverId, Integer deviceId, String address, Integer id);
		//@WebMethod public static void loadDataUpdate(Integer oldServerId, Integer oldDeviceId, String oldAddress, Integer newServerId, Integer newDeviceId, String newAddress);
		@WebMethod void loadDataRemove(Integer serverId, Integer deviceId, String address);
		//@WebMethod public static void loadStructureAdd(Integer dataId, String condition, String operator);
		@WebMethod void loadStructureAdd(Integer serverId, Integer deviceId, String address, String condition, String operator);
		@WebMethod void loadStructureRemove(Integer dataId, Integer id);
		//@WebMethod void loadStructureUpdate(Integer dataId, Integer id, String condition, String operator);
		@WebMethod void loadSchedAdd(String name, Long run, Long interval);
		@WebMethod void loadSchedRemove(String name);
		@WebMethod void loadSchedUpdate(String oldName, String newName, Long interval, Long timestamp);
		@WebMethod void ioEmailAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String subject, String html, Integer group);
		@WebMethod void ioEmailRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id);
		@WebMethod void ioEmailUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer addressGroup, String subject, String html);
		@WebMethod void ioSmsAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String message, Integer group);
		@WebMethod void ioSmsRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id);
		@WebMethod void ioSmsUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer addressGroup, String message);
		@WebMethod void ioSqlAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, String tableCol, String query, String path, HashMap<Integer, Integer> duration);
		@WebMethod void ioSqlRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id);
		@WebMethod void ioSqlUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, String tableCol, String query, String path);
		@WebMethod void ioSqlDurationAdd(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, HashMap<Integer, Integer> duration);
		@WebMethod void ioSqlDurationUpdate(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id, Integer type, Integer value);
		@WebMethod void ioSqlDurationRemove(Integer serverId, Integer deviceId, Integer itemId, String condition, String operator, Integer id);
		@WebMethod void schedEmailAdd(String name, String subject, String html, Integer group);
		@WebMethod void schedEmailRemove(String name, Integer id);
		@WebMethod void schedEmailUpdate(String name, Integer id, Integer addressGroup, String subject, String html);
		@WebMethod void schedSmsAdd(String name, String message, Integer group);
		@WebMethod void schedSmsRemove(String name, Integer id);
		@WebMethod void schedSmsUpdate(String name, Integer id, Integer addressGroup, String message);
		@WebMethod void schedSqlAdd(String name, String tableCol, String query, String path, HashMap<Integer, Integer> duration);
		@WebMethod void schedSqlRemove(String name, Integer id);
		@WebMethod void schedSqlUpdate(String name, Integer id, String tableCol, String query, String path);
		@WebMethod void schedSqlDurationAdd(String name, Integer id, HashMap<Integer, Integer> duration);
		@WebMethod void schedSqlDurationUpdate(String name, Integer id, Integer type, Integer value);
		@WebMethod void schedSqlDurationRemove(String name, Integer id);
		@WebMethod void addressGroupAddUser(Integer id, Integer user);
		@WebMethod void addressGroupAddGroup(Integer id, Integer group);
		@WebMethod void addressGroupRemove(Integer id);
		@WebMethod void addressGroupUpdateUser(Integer id, Integer addressgroup, Integer user);
		@WebMethod void addressGroupUpdateGroup(Integer id, Integer addressgroup, Integer group);
		@WebMethod void smsSend(ArrayList<String> to, String msg);
		@WebMethod void sqlFileStatic(String table, String query, String path, String filename, TimeManipulation durations);
		@WebMethod void sendMail(AddressStructure addresses, String subject, String html);
		@WebMethod Object readIo(Integer device, String address);
		@WebMethod void writeIo(Integer device, String address, Object value);
}
