package com.hl.util.report.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.opencsv.CSVWriter;

/**
 * 
 * @author santosh
 *
 */
public class CSVReportGenerator extends ReportGenerator {
	
	private static Logger logger = Logger.getLogger(CSVReportGenerator.class);
	
	// private SimpleDateFormat dateFormat;
	
	public CSVReportGenerator(String ... args) {
		super(args);
	}
	
	/*private String[] writeHeader(ResultSet resultSet, int columnCount) {
		String[] headerFields = new String[columnCount];
		for(int i = 0; i < columnCount; i++)
			try {
				headerFields[i] = resultSet.getMetaData().getColumnLabel(i + 1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return headerFields;
	}
	
	private String[] getData(ResultSet resultSet, int columnCount) {		
		String[] data = new String[columnCount];
		for(int i = 0; i < columnCount; i++) {
			try {
				if(Class.forName(resultSet.getMetaData().getColumnClassName(i + 1))
						.equals(Date.class) || 
						Class.forName(resultSet.getMetaData().getColumnClassName(i + 1))
						.equals(java.sql.Date.class)) {
					
					data[i] = dateFormat.format(resultSet.getDate(i + 1));
					
				} else if(Class.forName(resultSet.getMetaData().getColumnClassName(i + 1))
						.equals(Timestamp.class)) {
					Timestamp timestamp = resultSet.getTimestamp(i + 1);
					if(timestamp != null)
						data[i] = dateFormat.format(new Date(timestamp.getTime()));
					// System.out.println("Date: " + dateFormat.format(new Date(timestamp.getTime())));
				} else {
					data[i] = resultSet.getString(i + 1);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return data;
	}*/
	
	@Override
	protected String generateReport(ResultSet resultSet, String filename, String destinationPath)
			throws SQLException, UnsupportedOperationException, IOException {
		
		// dateFormat = new SimpleDateFormat(getDateFormatStr()); // 05-04-19
		
		StringBuffer newFilename = new StringBuffer();		
		newFilename.append(filename == null || filename.isEmpty() ? "Report" : filename);
		newFilename.append("_").append(getDateString()).append(".csv");
		
		logger.info("Filename: " + newFilename);
		
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriter(destinationPath.concat(newFilename.toString())),
					',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
						
			/*
			 * Commented: For java 6 version
			 * 
			 * int columnCount = resultSet.getMetaData().getColumnCount();
			
			if(resultSet.next())
				csvWriter.writeNext(writeHeader(resultSet, columnCount));
			
			while(resultSet.next())
				csvWriter.writeNext(getData(resultSet, columnCount));*/
			
			// TODO: Need to keep an option whether to include a header or not
			
			csvWriter.writeAll(resultSet, true);
			csvWriter.flush();
			logger.info(newFilename + ", got generated successfully.");
		} catch (IOException e) {
			logger.info("Got an exception in generate report: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.info("Got an exception in generate report: " + e.getMessage());
			e.printStackTrace();
			// Bug-001
			// Status: fixed
		} finally {	
			if(csvWriter != null)
				csvWriter.close();
		}
		return newFilename.toString();
	}

}
