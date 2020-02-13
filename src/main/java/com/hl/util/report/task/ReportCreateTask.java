package com.hl.util.report.task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hl.products.cms.fileGenerator.exceptions.NoFactoryFoundException;
import com.hl.products.cms.manager.FileCreateTask;
import com.hl.products.cms.manager.FileFormat;
import com.hl.products.cms.manager.UnsupportedFormatException;

import hl.util.ftp.FTPClient;
import hl.util.ftp.factory.FTPFactory;
import hl.util.ftp.factory.FTPFactory.FTPClientType;

public class ReportCreateTask extends FileCreateTask {
	
	private String agent_code;
	private String cycle_code;
	private String filename;
	private String ftpRequired;
	private String ftpPath;
	private String destinationDir;
	private String fileDestDir;

	public ReportCreateTask(FileFormat fileFormat, String sourceFile, String destinationFile,
			Map<String, String> reportParameters) throws NoFactoryFoundException, UnsupportedFormatException {
		super(fileFormat, sourceFile, destinationFile, reportParameters);
		this.filename	= destinationFile;
	}	

	public String getAgent_code() {
		return agent_code;
	}
	
	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	public String getCycle_code() {
		return cycle_code;
	}

	public void setCycle_code(String cycle_code) {
		this.cycle_code = cycle_code;
	}

	public String getFtpRequired() {
		return ftpRequired;
	}

	public void setFtpRequired(String ftpRequired) {
		this.ftpRequired = ftpRequired;
	}	

	public String getDestinationDir() {
		return destinationDir;
	}

	public void setDestinationDir(String destinationDir) {
		this.destinationDir = destinationDir;
	}

	public String getFileDestDir() {
		return fileDestDir;
	}

	public void setFileDestDir(String fileDestDir) {
		this.fileDestDir = fileDestDir;
	}
	
	@Override
	public void run() {
		System.out.println("Agent code: " 	+ agent_code);
		System.out.println("Cycle code: " 	+ cycle_code);
		System.out.println("Filename: " 	+ filename);
		System.out.println("Destination: "  + destinationDir);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");
		String dateDir 				= dateFormat.format(new Date());
		destinationDir 				= destinationDir + "/" + dateDir;
		System.out.println("New destination dir: " + destinationDir);

		super.run();
		
		// TODO: At this point, if the file has been successfully generated
		// then the record can be maintained in database.
		
		boolean result = false;
		
		if(ftpRequired != null && ftpRequired.equalsIgnoreCase("yes")) {
			System.out.println("FTP is required!!!");
			// TODO: Send file via FTP
			FTPClient ftpClient = null;
			try {
				ftpClient = FTPFactory.getFTPClient(FTPClientType.FTP);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			List<String> filenames = new ArrayList<String>();
			filenames.add(filename);
			
			try {
				result = ftpClient.upload(destinationDir, 
						this.ftpPath + File.separatorChar + fileDestDir, 
						filenames.toArray(new String[filenames.size()]));
			
			} catch (Exception e) {
				result = false;
			}
		}
		System.out.println("FTP Result: " + result);
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}
}
