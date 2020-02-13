package com.hl.util.report;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.hl.util.report.generator.ReportGenerator;

public abstract class Report {

	private static Logger logger = Logger.getLogger(CMSReport.class);
	// private ReportGenerator reportGenerator;
	protected ReportGenerator reportGenerator;
	private String additionalCondition	= null;
	
	private String defaultDateFormatStr = "yyyy_MM_dd_HH_mm_ss";
	
	public Report() { }
	
	public void initGenerator() {
		// Updated on: 07-06-19
		// this.reportGenerator = ReportFactory.createReportGenerator(getFormat());
		this.reportGenerator = ReportFactory.createReportGenerator(getFormat(), getArgs());	
	}
	
	public String getDestinationDirectory() {
		return null;
	}
	
	public void preReportGeneration(String type) {	}
	
	public boolean generateReport(String type) {
		return generateReport(type, "");
	}
	
	public boolean generateReport(String type, String filename) {
		return generateReport(type, filename, "");
	}
	
	public boolean generateReport(String type, String filename, String destinationDirectory) {
		logger.info("On generate report");
		System.out.println("On generate report");
		if(type == null) type = ""; // Default property values
		
		preReportGeneration(type);
		initGenerator();		
		
		// 05-04-19
		String dateFormatStr = getDateFormat();
		this.reportGenerator.setDateFormatStr(dateFormatStr == null || dateFormatStr.isEmpty() ?
				defaultDateFormatStr : dateFormatStr);
		
		if(destinationDirectory == null || destinationDirectory.isEmpty())
			destinationDirectory = getDestinationDirectory();
		
		logger.info("Destination directory: " + destinationDirectory);
		System.out.println("Destination directory: " + destinationDirectory);
		
		if(filename == null || filename.isEmpty())
			filename = getFilename();
		
		
		/**
		 * 	Bug-002
		 * 
		 *  Updated on: 12-04-19
		 *  
		 *  Reason: Post generation query was getting triggered despite
		 *  the report generation failure.
		 *  
		 *  If the old filename and new filename are same, then there's a failure
		 */
		String oldFilename = filename;
		
		try {
			filename = reportGenerator.generate(getResultSet(), filename, destinationDirectory);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			filename = null;
		} catch (SQLException e) {
			e.printStackTrace();
			filename = null;
		} catch (IOException e) {
			e.printStackTrace();
			filename = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			filename = null;
		}
		System.out.println("Got a filename!!!: " + filename);
		return filename == null || filename.equalsIgnoreCase(oldFilename) ? false : postReportGeneration(filename, destinationDirectory);
	}
		
	public abstract ResultSet getResultSet();
	
	public abstract String getFilename();
	
	public boolean postReportGeneration(String filename, String directory) { 
		return true;
	}
	
	public Format getFormat() {
		return Format.CSV;
	}
	
	public enum Format { CSV, XLSX, XLS, PDF }
	
	/**
	 * Updated on: 05-04-19
	 * 
	 * Default date format
	 * 
	 * @return
	 */
	public String getDateFormat() {
		return "";
	}
	
	public String getArgs() {
		return null;
	}
	
	public String getAdditionalCondition() {
		return additionalCondition;
	}

	public void setAdditionalCondition(String additionalCondition) {
		this.additionalCondition = additionalCondition;
	}
	
}
