/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.jdbc;

import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.dbc.DBCExecutionContext;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC connection
 */
public interface JDBCExecutionContext extends DBCExecutionContext, Connection {

    DBPDataSource getDataSource();
    
    DBRProgressMonitor getProgressMonitor();

    JDBCDatabaseMetaData getMetaData()
        throws SQLException;

    JDBCStatement createStatement()
        throws SQLException;

    JDBCStatement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql)
        throws SQLException;

    JDBCCallableStatement prepareCall(String sql)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException;

    JDBCCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException;

    JDBCCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql, int[] columnIndexes)
        throws SQLException;

    JDBCPreparedStatement prepareStatement(String sql, String[] columnNames)
        throws SQLException;

    void close();

}
