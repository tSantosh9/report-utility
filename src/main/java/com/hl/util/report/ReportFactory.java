package com.hl.util.report;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hl.util.report.Report.Format;
import com.hl.util.report.generator.CSVReportGenerator;
import com.hl.util.report.generator.ExcelReportGenerator;
import com.hl.util.report.generator.PDFGenerator;
import com.hl.util.report.generator.ReportGenerator;

public class ReportFactory {
	
	private static Logger logger = Logger.getLogger(ReportFactory.class);
	
	// public static ReportGenerator createReportGenerator(Format format) {
	public static ReportGenerator createReportGenerator(Format format, String ... args) {
		
		ReportGenerator reportGenerator = null;
		if(format.equals(Format.CSV)) {
			logger.info("Instantiated CSV report generator.");
			reportGenerator = new CSVReportGenerator(args);
		} else if(format.equals(Format.XLSX) || format.equals(Format.XLS)) { // Updated on: 07-06-19
			System.out.println("Instantiated XLSX report generator.");
			logger.info("Instantiated XLSX report generator.");
			// Newly added list to capture file extension.
			// List<String> newArgsList = Arrays.asList(args);
			// newArgsList.add(format.toString());
			
			reportGenerator = new ExcelReportGenerator(args);
			// reportGenerator = new ExcelReportGenerator((String[]) newArgsList.toArray());
			(( ExcelReportGenerator )reportGenerator).setWorkbook(
					format.equals(Format.XLS) ? new HSSFWorkbook() : 
						new SXSSFWorkbook(new XSSFWorkbook(), SXSSFWorkbook.DEFAULT_WINDOW_SIZE));
		} else if(format.equals(Format.PDF)) {
			logger.info("Instantiated PDF report generator.");
			reportGenerator = new PDFGenerator(args);
		}
		if(reportGenerator != null)
			reportGenerator.setFileFormat(format);
		return reportGenerator;
	}
		
}
