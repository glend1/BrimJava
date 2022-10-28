package com.matthey.brimjava.loader.events.util;

import java.sql.SQLException;
import java.util.ArrayList;

import com.matthey.brimjava.mail.util.AddressStructure;
import com.matthey.brimjava.sql.util.Sql;

public class AddressGroup {
	private Integer group = null;
	public AddressGroup(Integer i) {
		setGroup(i);
	}
	//TODO: AL: recipienttype support
	private ArrayList<String> get(String col) {
		ArrayList<String> out = new ArrayList<String>(); 
		Sql sql = new Sql("plantavail");
		sql.query("select distinct " + col
		+ " from (select " + col
		+ " from j_addressesgroup"
		+ " join users on users.id = j_addressesgroup.userfk"
		+ " union"
		+ " select " + col
		+ " from j_addressesgroup"
		+ " left join groupjunction on j_addressesgroup.groupfk = groupjunction.groupid"
		+ " join users on users.id = groupjunction.userid"
		+ " where addressgroup = " + getGroup() + ") as t1");
		try {
			while(sql.getResult().next()) {
				String colData = sql.getResult().getString(col);
				if (colData != null) {
					if (!colData.equalsIgnoreCase("")) {
						out.add(colData);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		sql.close();
		return out;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer i) {
		group = i;
	}
	public ArrayList<String> getMobile() {
		return get("mobile");
	}
	public AddressStructure getEmail() {
		ArrayList<String> emails = get("email");
		AddressStructure addresses = new AddressStructure();
		for (String email : emails) {
			addresses.add("to", email);
		}
		return addresses;
	}

}
