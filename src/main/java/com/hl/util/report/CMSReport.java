package com.hl.util.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hl.util.report.connection.DBConnection;

import hl.util.ftp.FTPClient;
import hl.util.ftp.factory.FTPFactory;
import hl.util.ftp.factory.FTPFactory.FTPClientType;

public class CMSReport extends Report {
	
	private static Logger logger = Logger.getLogger(CMSReport.class);
	
	private String commonPath			= null;
	
	protected String fileFormat			= null;
	protected String destinationDir		= null;
	protected String query				= null;
	protected String ftpRequired		= null;
	protected String filename			= null;
	protected String postGenQuery		= null;
	// private String cronExpression	= null;
	protected boolean toClose			= false; // Responsibility of caller to close the connection
	
	protected String preGenQuery		= null; // 05-04-19
	protected String dateFormat			= null; // 05-04-19
	protected String toIncludeHeader	= null;
	protected String ftpType			= null;
	
	protected Connection connection;
	protected ResultSet resultSet;
	protected Statement statement;
	
	private FTPClient ftpClient			= null;

	protected Properties properties		= null;
	
	public CMSReport() {
		init();
		this.connection = new DBConnection().getConnection();
		this.toClose	= true;
	}
	
	public CMSReport(Connection connection) {
		this.connection = connection;
		init();
	}
	
	public CMSReport(Connection connection, boolean toClose) {
		this(connection);
		this.toClose = toClose;
	}
	
	private void init() {
		logger.info("INIT() Reading the properties file!");
		System.out.println("INIT() Reading the properties file!");
		properties 				= new Properties();
		InputStream inputStream = null;
		try {
			inputStream		= getClass().getClassLoader()
										.getResourceAsStream("reportConfig.properties");
			if(inputStream == null) 
				inputStream		= new FileInputStream("./reportConfig.properties");
			if(inputStream != null)
				properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Got an exception while reading a properties file!");
			System.err.println("Unable to load the property file!!!");
		}
	}
	
	@Override
	public String getDestinationDirectory() {
		if(commonPath == null || commonPath.isEmpty()) {
			return destinationDir.charAt(0) == File.separatorChar ?
				destinationDir : File.separatorChar + destinationDir;
		}
		return commonPath.charAt(commonPath.length() - 1) == File.separatorChar ?
				commonPath.concat(destinationDir) : 
					commonPath.concat(File.separatorChar + "").concat(destinationDir);
	}

	public String getFileDestinationDir() {
		return this.destinationDir;
	}

	@Override
	public Format getFormat() {
		if(fileFormat == null || fileFormat.equalsIgnoreCase("xlsx"))
			return Format.XLSX;
		else if(fileFormat.equalsIgnoreCase("csv"))
			return Format.CSV;
		else if(fileFormat.equalsIgnoreCase("pdf"))
			return Format.PDF;
		else if(fileFormat.equalsIgnoreCase("xls")) // Updated on: 07-06-19
			return Format.XLS;
		return null;
	}
	
	protected void readProperty(String type) throws Exception {
		logger.info("Reading property file started!");
		System.out.println("Reading property file started!");
		if(properties == null || properties.isEmpty()) {
			logger.error("Property file is empty!");
			throw new Exception("Property file is empty");
		}
		this.commonPath			= properties.getProperty("global.dir");
		//this.globalExpressoin	= properties.getProperty("global.expression");
		this.query				= properties.getProperty(type + ".query.reportData");
		this.fileFormat 		= properties.getProperty(type + ".fileformat");
		this.ftpRequired		= properties.getProperty(type + ".ftpRequired", "no");
		this.ftpType			= properties.getProperty(type + ".ftpType", "FTP");
		this.destinationDir		= properties.getProperty(type + ".destination.dir");
		
		// Updated on: 02-04-19
		this.filename			= properties.getProperty(type + ".filename");
		this.postGenQuery		= properties.getProperty(type + ".query.postReportGeneration");
		// this.cronExpression		= properties.getProperty(type + ".cronExpression");
		
		this.preGenQuery		= properties.getProperty(type + ".query.preReportGeneration"); // 05-04-19
		this.dateFormat			= properties.getProperty(type + ".dateformat"); // 05-04-19
		this.toIncludeHeader	= properties.getProperty(type + ".includeHeader"); // 07-06-19
		
		StringBuffer info		= new StringBuffer();
		info.append("Pre-report generation query: " 	+ this.preGenQuery		+ "\n")
			.append("Report query: "					+ this.query			+ "\n")
			.append("Post-report generation query: " 	+ this.postGenQuery		+ "\n")
			.append("Common directory: "				+ this.commonPath		+ "\n")
			.append("File format: "						+ this.fileFormat		+ "\n")
			.append("Destination directory: " 			+ this.destinationDir	+ "\n")
			.append("Is FTP required: "       			+ this.ftpRequired		+ "\n")
			.append("Date format: "						+ this.dateFormat		+ "\n")
			.append("Include header: "					+ this.toIncludeHeader);
		
		logger.info("Type: " + type + "\n" + info.toString());
		System.out.println("Type: " + type + "\n" + info.toString());
	}
	
	/**
	 * Updated on: 05-04-19
	 * 
	 * @param query
	 */
	protected void executeQuery(String query) {
		logger.info("On execute query, query: " + query);
		System.out.println("On execute query, query: " + query);
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Got an exception while executing a query, query: " + query);
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Updated on: 05-04-19
	 * 
	 * 
	 */
	protected void executePreGenQuery() {
		logger.info("On execute pre-report generation query");
		if(preGenQuery == null || preGenQuery.trim().isEmpty()) return;
		executeQuery(preGenQuery);
	}
	
	/**
	 * Updated on: 02-04-19
	 * 
	 * Support for post generation query execution
	 */
	protected void executePostGenQuery() {
		logger.info("On execute post-report generation query");
		if(postGenQuery == null || postGenQuery.trim().isEmpty()) return;
		executeQuery(postGenQuery);		
	}

	@Override
	public boolean postReportGeneration(String filename, String directory) {
		logger.info("On post report generation!");
		boolean result = true;
		if(ftpRequired.equalsIgnoreCase("yes")) {
			List<String> filenames = new ArrayList<String>();
			filenames.add(filename);
			try {
				ftpClient = FTPFactory.getFTPClient(
							this.ftpType.equalsIgnoreCase("SFTP") ?
									FTPClientType.SFTP : FTPClientType.FTP);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("Directory: " + directory);
			System.out.println("Local dir: " + this.destinationDir);
			if(ftpClient != null) {
				try {
					result = ftpClient.upload(directory, this.destinationDir, 
								filenames.toArray(new String[filenames.size()]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(result && postGenQuery != null && !postGenQuery.isEmpty()) 
			executePostGenQuery();
		close();
		return result;
	}

	@Override
	public void preReportGeneration(String type) {
		logger.info("On pre report generation.");
		try {
			readProperty(type);			
			executePreGenQuery(); 
			query = ( getAdditionalCondition() != null && !getAdditionalCondition().isEmpty() ) ?
						query.concat(" ").concat(getAdditionalCondition()) : query;			
			statement = connection.createStatement();
			resultSet	= statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Got an exception in pre report generation!");
		}
	}
	
	protected void close() {
		logger.info("Closing the resources!");
		if(resultSet != null)
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		if(statement != null)
			try {
				statement.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		
		if(toClose)
			try {
				logger.info("Closing the connection");
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	@Override
	public ResultSet getResultSet() {
		return resultSet;
	}

	@Override
	public String getFilename() {
		return this.filename;
	}

	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public String getArgs() {
		return toIncludeHeader;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}	

}
