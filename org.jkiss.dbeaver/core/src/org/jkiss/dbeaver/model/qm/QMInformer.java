/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.qm;

import org.jkiss.dbeaver.model.dbc.DBCStatement;
import org.jkiss.dbeaver.model.DBPDataSource;

import java.util.List;

/**
 * Query Manager information reporter
 */
public interface QMInformer {

    public List<DBCStatement> getActiveQueries(DBPDataSource dataSource);

    public List<DBCStatement> getTransactionScope(DBPDataSource dataSource);

}
