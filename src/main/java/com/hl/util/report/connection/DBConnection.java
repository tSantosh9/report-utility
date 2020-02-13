package com.hl.util.report.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	private Properties properties;
	
	private Connection connection = null;
	
	public DBConnection() {
		init();
	}
	
	private void init() {
		initializePropertyFile();
		initializeConnection();
	}
	
	private void initializeConnection() {
		try {
			Class.forName(properties.getProperty("driver"));
			connection = DriverManager.getConnection(properties.getProperty("url"),
					properties.getProperty("username"), properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void initializePropertyFile() {
		properties 				= new Properties();
		InputStream inputStream = null;
		try {
			inputStream		= getClass().getClassLoader()
										.getResourceAsStream("databaseConfig.properties");
			if(inputStream == null) 
				inputStream	= new FileInputStream("./databaseConfig.properties");
			if(inputStream != null)
				properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to load the property file!!!");
		}
	}
	
	public Connection getConnection() {
		if(connection == null)
			throw new UnsupportedOperationException("Connection not initialized");
		return connection;
	}
	
}
