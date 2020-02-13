package com.hl.util.report.generator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author santosh
 *
 */
public interface Generator {
	
	/**
	 * Generates the file in the current working directory.
	 * 
	 * @param resultSet
	 * @param filename
	 * @return <b>filename</b> of a newly created file, iff the file is generated,
	 * <b>null</b> otherwise.
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	String generate(ResultSet resultSet, String filename) 
			throws SQLException, UnsupportedOperationException, IOException;
	
	/**
	 * Generates the file in the given destination path.
	 * 
	 * @param resultSet
	 * @param filename
	 * @param destinationPath
	 * @return <b>filename</b> of a newly created file, iff the file is generated,
	 * <b>null</b> otherwise.
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	String generate(ResultSet resultSet, String filename, String destinationPath)
		throws SQLException, UnsupportedOperationException, IOException;
	
}
