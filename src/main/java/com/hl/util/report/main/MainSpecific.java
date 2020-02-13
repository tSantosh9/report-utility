package com.hl.util.report.main;

import com.hl.util.report.CMSReport;

public class MainSpecific {

	public static void main(String[] args) {
		CMSReport testReport = new CMSReport();
		// testReport.setAdditionalCondition("WHERE CATEGORYNAME = 'AgentMaster'");
		testReport.generateReport("Test");
		
	}
	
}
