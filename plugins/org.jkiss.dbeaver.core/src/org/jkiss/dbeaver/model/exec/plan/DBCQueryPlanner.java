/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.exec.plan;

import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;

/**
 * Execution plan builder
 */
public interface DBCQueryPlanner {

    DBCPlan planQueryExecution(DBCExecutionContext context, String query)
        throws DBCException;

}
