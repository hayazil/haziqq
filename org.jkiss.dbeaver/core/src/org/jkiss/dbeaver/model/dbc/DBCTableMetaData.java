/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.dbc;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.struct.DBSTable;

/**
 * Result set table metadata
 */
public interface DBCTableMetaData {

    /**
     * Table reference
     * @return table table reference. never returns null
     */
    DBSTable getTable()
        throws DBException;

    /**
     * Table name
     * @return table name
     */
    String getTableName();

    /**
     * Table alias
     * @return table alias in query
     */
    String getTableAlias();

    /**
     * Checks table is identitied.
     * Table is identitied if resultset contains at least one set of this table columns which will unique
     * identify table row
     * @return true if this table has at least one unique identitier in the whole resultset.
     */
    boolean isIdentitied()
        throws DBException;

    /**
     * Gets best table identifier.
     * Best identifier is a primary key. If no such one then any unique key fits.
     * @return list of identifier columns which identifies this table row the best way
     * or null if no identifiers found.
     */
    DBCTableIdentifier getBestIdentifier()
        throws DBException;

}
