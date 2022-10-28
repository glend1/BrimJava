package com.matthey.brimjava.sql.util;

import com.matthey.brimjava.sql.util.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sql {
	protected Connection conn = null;
	protected Statement stmt = null;
	private ResultSet rs = null;
	public Sql(String database) {
		try {
			conn = DriverManager.getConnection("jdbc:sqlserver://insql2:1433;databaseName=" + database, Secret.NAME, Secret.PASSWORD );
			stmt = conn.createStatement(); 
		} catch (SQLException e) {
			System.out.println("Cannot connect to SQL");
			e.printStackTrace();
		}
	}
	private void echo(String query) {
		System.out.println(query);
	}
	public void query(String query) {
		try {
			echo(query);
			conn.setAutoCommit(false);
			rs = stmt.executeQuery(query);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println("Cannot perform this SQL query: " + query);
			e.printStackTrace();
		}
	}
	public void mod(String query) {
		echo(query);
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Cannot perform this SQL query: " + query);
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Cannot close SQL connection");
			e.printStackTrace();
		}		
	}
	public ResultSet getResult() {
		return rs;
	}
}
