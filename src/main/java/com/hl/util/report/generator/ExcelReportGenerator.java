package com.hl.util.report.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 
 * @author santosh
 *
 */
public class ExcelReportGenerator extends ReportGenerator {
	
	// Updated on: 29-05-19
	protected final int SHEET_ROW_SIZE = 200000; // Threshold
	
	private static Logger logger = Logger.getLogger(ExcelReportGenerator.class);
	protected Workbook workbook;
	protected String toIncludeHeader = null;
	
	private Map<String, CellStyle> cellStyles;
	
	public ExcelReportGenerator(String ... args) {
		super(args);
		toIncludeHeader = args != null && args.length > 0 ? args[0] : null;
		this.cellStyles	= new HashMap<String, CellStyle>();
	}
	
	protected String getDataType(@SuppressWarnings("rawtypes") Class className) {
		if(className == Integer.class || className == BigDecimal.class)
			return "integer";
		if(className == Double.class)
			return "double";
		if(className == Timestamp.class)
			return "timestamp";
		if(className == Date.class)
			return "date";
		if(className == Float.class)
			return "float";
		if(className == Boolean.class)
			return "boolean";
		if(className == String.class)
			return "string";
		return null;
	}
	
	protected CellStyle getHeaderCellStyle() {		
		CellStyle cellStyle = this.workbook.createCellStyle();
		Font bold			= this.workbook.createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyle.setFont(bold);
		DataFormat dFormat 	= this.workbook.createDataFormat();
		cellStyle.setDataFormat(dFormat.getFormat("@"));
		
		return cellStyle;
	}
	
	private CellStyle getCellStyle(String dataType) {
		DataFormat dFormat	= workbook.createDataFormat();
		CellStyle cellStyle = workbook.createCellStyle();
		
		String format		= "@";
		if("integer".equalsIgnoreCase(dataType))
			// format	= "General";
			format	= "0.00";
		else if("double".equalsIgnoreCase(dataType))
			format	= "#,##0.00";
		else if("timestamp".equalsIgnoreCase(dataType))
			format	= "DD/MM/YYYY HH:MM:SS";
		else if("date".equalsIgnoreCase(dataType))
			format	= "DD/MM/YY";
		else if("float".equalsIgnoreCase(dataType))
			format	= "#,##0.00";
		else if("boolean".equalsIgnoreCase(dataType))
			format	= "BOOLEAN";
		
		cellStyle.setDataFormat(dFormat.getFormat(format));
		return cellStyle;
	}
	
	protected void initializeCellStyle(String dataType) {
		if(!cellStyles.containsKey(dataType))
			cellStyles.put(dataType, getCellStyle(dataType));
	}
	
	protected void writeToCell(Row row, int columnIndex, Object data, CellStyle cellStyle, String dataformat) {
		if(data == null) return;
		Cell cell	= row.createCell(columnIndex);		
		if("integer".equalsIgnoreCase(dataformat))
			cell.setCellValue(new BigDecimal(data.toString()).doubleValue());
		else if("double".equalsIgnoreCase(dataformat))
			cell.setCellValue((Double) data);
		else if("timestamp".equalsIgnoreCase(dataformat))
			cell.setCellValue((Timestamp) data);
		else if("date".equalsIgnoreCase(dataformat))
			cell.setCellValue((Date) data);
		// Commented on: 20-09-19
		/*else if("timestamp".equalsIgnoreCase(dataformat) || "date".equalsIgnoreCase(dataformat))
			cell.setCellValue((Date) data);*/
		else if("float".equalsIgnoreCase(dataformat))
			cell.setCellValue((String) data);
		else if("boolean".equalsIgnoreCase(dataformat))
			cell.setCellValue((Boolean) data);
		else
			cell.setCellValue(data.toString());
		
		cell.setCellStyle(cellStyle != null ? cellStyle : cellStyles.get(dataformat));
	}
	
	@Override
	protected String generateReport(ResultSet resultSet, String filename, String destinationPath)
			throws SQLException, UnsupportedOperationException, IOException {
		
		int rowIndex			= 0;
		int columnCount			= resultSet.getMetaData().getColumnCount();
		Sheet sheet				= this.workbook.createSheet("Sheet1");
		Row row					= null;
		String[] dataformats	= new String[columnCount];
		String datatype;
		
		// StringBuffer columnLabels = new StringBuffer("Column labels: \n");
		
		CellStyle headerCell	= getHeaderCellStyle();
		row = sheet.createRow(rowIndex);
		for(int i = 0; i < columnCount; i++) {			
			// columnLabels.append(resultSet.getMetaData().getColumnLabel(i + 1) + "\n");
			System.out.println("Column label: " + resultSet.getMetaData().getColumnLabel(i + 1));
			try {
				datatype		= getDataType(Class.forName(resultSet.getMetaData().getColumnClassName(i + 1)));
				dataformats[i]	= datatype;
				initializeCellStyle(datatype);				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				logger.info("Got an exception while generating a report: " + e.getMessage());
			}
			if(toIncludeHeader == null || !toIncludeHeader.equalsIgnoreCase("no")) {
				System.out.println("Index: " + i);				
				writeToCell(row, i, resultSet.getMetaData().getColumnLabel(i + 1), headerCell, "string");
			} 
		}
		/*
		 *  Updated on: 29-05-19
		 *  
		 *  If the row size of sheet exceeds the threshold, then create a new sheet.
		 *  
		 */
		int count = 0, sheet_count = 1;
		Object object;
		while(resultSet.next()) {
			row	= sheet.createRow(++rowIndex);
			for(int i = 0; i < columnCount; i++) {
				object = resultSet.getObject(i + 1);
				writeToCell(row, i, object, null, dataformats[i]);
			}
			// Updated on: 29-05-19
			count++;
			if(count % SHEET_ROW_SIZE == 0) {
				System.out.println("---------Changed sheet!-----------");
				sheet_count++;
				rowIndex 	= 0;
				sheet 		= this.workbook.createSheet("Sheet" + sheet_count);
			}
		}
		
		
		StringBuffer newFilename = new StringBuffer();		
		newFilename.append(filename == null || filename.isEmpty() ? "Report" : filename);
		System.out.println("Date String: ");
		System.out.println(getDateString());
		System.out.println("Fileextension: " + getFileExtension());
		newFilename.append("_").append(getDateString()).append(getFileExtension());
		
		logger.info("Filename: " + newFilename);
				
		FileOutputStream outputStream = new FileOutputStream(destinationPath.concat(newFilename.toString()));
		workbook.write(outputStream);
		outputStream.close();
		
		// System.out.println("Write over!!");
		// workbook.dispose();
		//workbook.close();
		logger.info(newFilename + ", got generated successfully!");
		
		return newFilename.toString();
	}
	
	protected String getFileExtension() {
		// TODO: Need to make a better way of getting of file extension
		// return workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";
		
		return ".".concat(fileFormat.toString().toLowerCase());
	}
	

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

}
