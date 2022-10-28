package com.matthey.brimjava.loader.events.util;

import java.sql.SQLException;

import com.matthey.brimjava.sql.util.Sql;

public class AddressGroupData {
	public static void addUser(Integer id, Integer user) {
		Sql sql = new Sql("plantavail");
		boolean found = false;
		sql.query("select top 1 id from J_addressesgroup where addressgroup = " + id + " and userfk = " + user);
		try {
			sql.getResult().next();
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (!found) {
			sql.mod("insert into J_addressesgroup (addressgroup, userfk) values (" + id + ", " + user + ")");
			System.out.println("User added to Address Group");
		} else {
			System.out.println("User already added Address Group");
		}
		sql.close();
	}
	public static void addGroup(Integer id, Integer group) {
		Sql sql = new Sql("plantavail");
		boolean found = false;
		sql.query("select top 1 id from J_addressesgroup where addressgroup = " + id + " and groupfk = " + group);
		try {
			sql.getResult().next();
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (!found) {
			sql.mod("insert into J_addressesgroup (addressgroup, groupfk) values (" + id + ", " + group + ")");
			System.out.println("Group added to Address Group");
		} else {
			System.out.println("Group already added to Address Group");
		}
		sql.close();
	}
	public static void remove(Integer id) {
		Sql sql = new Sql("plantavail");
		boolean found = false;
		sql.query("select top 1 id from J_addressesgroup where id = " + id);
		try {
			sql.getResult().next();
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (found) {
			sql.mod("delete from J_addressesgroup where id = " + id);
			System.out.println("Address Group removed");
		} else {
			System.out.println("Nothing to remove from Address Group");
		}
		sql.close();
	}
	public static void updateUser(Integer id, Integer addressgroup, Integer user) {
		Sql sql = new Sql("plantavail");
		boolean found = false;
		sql.query("select top 1 id from J_addressesgroup where id = " + id);
		try {
			sql.getResult().next();
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (found) {
			sql.mod("update J_addressesgroup set addressgroup = " + addressgroup + ", userfk = " + user + " where id = " + id);
			System.out.println("Updated User Address Group");
		} else {
			System.out.println("Unable to update User Address Group");
		}
		sql.close();
	}
	public static void updateGroup(Integer id, Integer addressgroup, Integer group) {
		Sql sql = new Sql("plantavail");
		boolean found = false;
		sql.query("select top 1 id from J_addressesgroup where id = " + id);
		try {
			sql.getResult().next();
			found = true;
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		if (found) {
			sql.mod("update J_addressesgroup set addressgroup = " + addressgroup + ", groupfk = " + group + " where id = " + id);
			System.out.println("Updated Group Address Group");
		} else {
			System.out.println("Unable to update Group Address Group");
		}
		sql.close();
	}
}
