package com.hl.util.report.generator;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.hl.util.report.Report.Format;

/**
 * 
 * @author santosh
 *
 */
public abstract class ReportGenerator implements Generator {
	
	private static Logger logger = Logger.getLogger(ReportGenerator.class);
	private String dateFormatStr;
	
	protected String[] args;
	protected Format fileFormat;
	
	public ReportGenerator(String ... args) {
		this.args	= args;
	}	
	
	@Override
	public String generate(ResultSet resultSet, String filename)
			throws SQLException, UnsupportedOperationException, IOException {
		return generate(resultSet, filename, "");
	}

	@Override
	public String generate(ResultSet resultSet, String filename, String destinationPath)
			throws SQLException, UnsupportedOperationException, IOException {
		
		System.out.println("Generate the report");
		
		if(resultSet == null || !resultSet.isBeforeFirst()) {
			logger.info("Result set is empty!");
			throw new UnsupportedOperationException("Result set is empty!");
		}
		
		if(destinationPath == null) destinationPath = "";
		
		File destinationDir = new File(destinationPath);		
		if(!destinationDir.exists()) {
			logger.info(destinationPath + ", doesn't exist. Creating the directory.");
			destinationDir.mkdirs();
		}
		
		if(!destinationPath.isEmpty())
			if(destinationPath.charAt(destinationPath.length() - 1) != File.separatorChar)
				destinationPath = destinationPath.concat("" + File.separatorChar);
		
		return generateReport(resultSet, filename, destinationPath);
	}
	
	/**
	 * 
	 * 
	 * @param resultSet
	 * @param filename
	 * @param destinationPath
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	protected abstract String generateReport(ResultSet resultSet, String filename, String destinationPath)
			throws SQLException, UnsupportedOperationException, IOException;

	/**
	 * 
	 * @return
	 */
	public String getDateString() {
		SimpleDateFormat dateFormat	= new SimpleDateFormat(getDateFormatStr());
		Date currentDate			= new Date();
		return dateFormat.format(currentDate);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDateFormatStr() {
		return dateFormatStr;
	}

	/**
	 * 
	 * @param dateFormat
	 */
	public void setDateFormatStr(String dateFormatStr) {
		this.dateFormatStr = dateFormatStr;
	}	

	public Format getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(Format fileFormat) {
		this.fileFormat = fileFormat;
	}
	
}
