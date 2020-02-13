package com.hl.util.report.generator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hl.products.cms.fileGenerator.birt.PDFFactory;
import com.hl.products.cms.fileGenerator.exceptions.NoFactoryFoundException;
import com.hl.products.cms.manager.FileFormat;
import com.hl.products.cms.manager.UnsupportedFormatException;
import com.hl.util.report.task.ReportCreateTask;

public class PDFGenerator extends ReportGenerator {
	
	private String designFileLocation;
	private String designFilename;
	private String ftpRequired;
	private String filename;
	private String fileDestDir;
		
	public PDFGenerator(String ... args) {
		super(args);
		if(args.length > 2) {
			this.designFileLocation = args[0];
			this.designFilename 	= args[1];
			this.ftpRequired		= args[2];
			this.filename			= args[3];
			this.fileDestDir		= args[4];
		}
	}
	
	public void init(String destinationPath) {
		PDFFactory.initInstance("", 
				this.designFileLocation, destinationPath, destinationPath + "/logs/");
	}
	
	@Override
	protected String generateReport(ResultSet resultSet, String filename, String destinationPath)
			throws SQLException, UnsupportedOperationException, IOException {
		
		init(destinationPath);
		
		System.out.println("Design file location: " + this.designFileLocation);
		System.out.println("Design file name: "  	+ this.designFilename);
		System.out.println("Ftp required: "         + this.ftpRequired);
		
		// Commented on: 17-06-19
		// String agent_code, cycle_code, newFilename;
		String newFilename;
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		ResultSetMetaData metadata	= resultSet.getMetaData();
		List<String> columnLabels	= new ArrayList<>();
		for(int i = 0; i < metadata.getColumnCount(); i++) {
			System.out.println("Label: " + metadata.getColumnLabel(i + 1));
			columnLabels.add(metadata.getColumnLabel(i + 1));
		}
		
		while(resultSet.next()) {			
			/*
			 * Commented on: 17-06-19
			 * 
			agent_code	= resultSet.getString("AGENT_CODE");
			cycle_code	= resultSet.getString("CYCLE_CODE");
			System.out.println("Agent code: " + agent_code);
			System.out.println("Cycle code: " + cycle_code);
			
			map.put("cycle_code", cycle_code);
			map.put("agent_code", agent_code);*/
			
			// Assigning all the label's data as a report parameter.
			Map<String, String> map = new HashMap<String, String>();
			for(String label : columnLabels) {
				System.out.println("Label is " + label + ":" + resultSet.getString(label));
				map.put(label, resultSet.getString(label));
			}
			
			// Commented on: 17-06-19
			// newFilename = agent_code + "_" + cycle_code + "_statement";
			if(this.filename == null || filename.isEmpty())
				this.filename = "statement";
			
			String regex 			= "[^{\\\\}]+(?=})";
			Pattern pattern 		= Pattern.compile(regex);
			Matcher matchPattern 	= pattern.matcher(this.filename);
			newFilename 			= this.filename;
			while(matchPattern.find()) {
				System.out.println(matchPattern.group());
				newFilename = newFilename.replaceAll("\\{" + matchPattern.group() + "}",
						resultSet.getString(Integer.parseInt(matchPattern.group())));
			}
			System.out.println(newFilename);
			
			newFilename = newFilename.concat(".pdf");
			try {
				ReportCreateTask  reportCreateTask = new ReportCreateTask(FileFormat.PDF,
						this.designFilename, newFilename, map);
				
				// reportCreateTask.setAgent_code(agent_code);
				// reportCreateTask.setCycle_code(cycle_code);
				reportCreateTask.setFtpRequired(this.ftpRequired);
				reportCreateTask.setDestinationDir(destinationPath);
				reportCreateTask.setFileDestDir(this.fileDestDir);
				
				// reportCreateTask.start();
				executorService.execute(reportCreateTask);
				
			} catch (NoFactoryFoundException | UnsupportedFormatException e) {
				e.printStackTrace();
			}			
		}
		executorService.shutdown();
		while(!executorService.isTerminated()); // Waiting for all threads to complete
		
		return destinationPath;
	}

}
