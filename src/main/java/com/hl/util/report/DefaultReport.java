package com.hl.util.report;

import java.sql.ResultSet;

public class DefaultReport extends Report {

	@Override
	public ResultSet getResultSet() {
		return null;
	}

	@Override
	public String getFilename() {
		return "default";
	}

}
