/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.impl.jdbc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.model.dbc.DBCSavepoint;
import org.jkiss.dbeaver.model.jdbc.JDBCExecutionContext;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Savepoint
 */
public class SavepointManagable implements DBCSavepoint {

    static Log log = LogFactory.getLog(SavepointManagable.class);

    private ConnectionManagable connection;
    private Savepoint original;

    public SavepointManagable(ConnectionManagable connection, Savepoint savepoint)
    {
        this.connection = connection;
        this.original = savepoint;
    }

    public int getId()
    {
        try {
            return original.getSavepointId();
        }
        catch (SQLException e) {
            log.error(e);
            return 0;
        }
    }

    public String getName()
    {
        try {
            return original.getSavepointName();
        }
        catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public JDBCExecutionContext getContext()
    {
        return connection;
    }
}
