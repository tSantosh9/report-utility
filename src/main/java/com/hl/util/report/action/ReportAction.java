package com.hl.util.report.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hl.util.report.CMSReport;
import com.hl.util.report.connection.DBConnection;


public class ReportAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String reportType = request.getParameter("reportType");
		System.out.println("Report type: " + reportType);
		DBConnection dbConnection = new DBConnection();
		CMSReport cmsReport = new CMSReport(dbConnection.getConnection());
		boolean result = cmsReport.generateReport(reportType);
		System.out.println("Result: " + result);
		dbConnection.getConnection().close();
		return mapping.findForward(result ? "success" : "failure");
	}
	
	
	
	
}
