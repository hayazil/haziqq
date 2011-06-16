/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.oracle.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.oracle.OracleConstants;
import org.jkiss.dbeaver.model.DBPNamedObject2;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTable;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import java.sql.ResultSet;
import java.util.List;

/**
 * OracleTable base
 */
public abstract class OracleTableBase extends JDBCTable<OracleDataSource, OracleSchema> implements DBPNamedObject2
{
    static final Log log = LogFactory.getLog(OracleTableBase.class);

    private String comment;
    private List<OracleTableColumn> columns;

    protected OracleTableBase(OracleSchema schema, boolean persisted)
    {
        super(schema, persisted);
    }

    protected OracleTableBase(OracleSchema oracleSchema, ResultSet dbResult)
    {
        super(oracleSchema, true);
        setName(JDBCUtils.safeGetString(dbResult, OracleConstants.COL_TABLE_NAME));
        this.comment = JDBCUtils.safeGetString(dbResult, OracleConstants.COL_COMMENTS);
    }

    @Property(name = "Comments", viewable = true, editable = true, order = 100)
    public String getDescription()
    {
        return comment;
    }

    public String getFullQualifiedName()
    {
        return DBUtils.getFullQualifiedName(getDataSource(),
            getContainer(),
            this);
    }

    @Override
    public String getTableType()
    {
        return super.getTableType();
    }

    public List<OracleTableColumn> getColumns(DBRProgressMonitor monitor)
        throws DBException
    {
        if (columns == null) {
            getContainer().getTableCache().getChildren(monitor, getDataSource(), this);
        }
        return columns;
    }

    public OracleTableColumn getColumn(DBRProgressMonitor monitor, String columnName)
        throws DBException
    {
        return DBUtils.findObject(getColumns(monitor), columnName);
    }

    @Override
    public boolean refreshEntity(DBRProgressMonitor monitor) throws DBException
    {
        columns = null;
        return true;
    }

    public boolean isColumnsCached()
    {
        return columns != null;
    }

    public void setColumns(List<OracleTableColumn> columns)
    {
        this.columns = columns;
    }


    public String getDDL(DBRProgressMonitor monitor)
        throws DBException
    {
        return "";
    }

}
