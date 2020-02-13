package com.hl.util.report.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hl.util.report.CMSReport;
import com.hl.util.report.Report;

public class Main {

	public static void main(String[] args) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream		= Main.class.getClassLoader()
										.getResourceAsStream("reportTypeConfig.properties");
			if(inputStream == null) 
				inputStream	= new FileInputStream("./reportTypeConfig.properties");
			if(inputStream != null) {
				properties.load(inputStream);
				String types		 = properties.getProperty("report.types");
				if(types != null) {
					String[] reportTypes = types.split(",");
					String className = null;
					Report report 	 = null;
					for(String reportType : reportTypes) {
						className	 = properties.getProperty(reportType + ".reportgenerator");
						if(className == null || className.isEmpty())
							report	 = new CMSReport();
						else
							report	 = (Report) Class.forName(className).newInstance();
						
						if(report != null)
							report.generateReport(reportType);
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
