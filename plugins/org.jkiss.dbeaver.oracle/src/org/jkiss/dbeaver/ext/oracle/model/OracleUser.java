/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.oracle.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPSaveableObject;
import org.jkiss.dbeaver.model.access.DBAUser;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.meta.LazyProperty;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * OracleUser
 */
public class OracleUser extends OracleGlobalObject implements DBAUser, DBPSaveableObject, OracleTablespace.TablespaceReferrer
{
    static final Log log = LogFactory.getLog(OracleUser.class);

    private long id;
    private String name;
    private String externalName;
    private String status;
    private Timestamp createDate;
    private Timestamp lockDate;
    private Timestamp expiryDate;
    private Object defaultTablespace;
    private Object tempTablespace;
    private String profile;
    private String consumerGroup;

    public OracleUser(OracleDataSource dataSource, ResultSet resultSet) {
        super(dataSource, resultSet != null);
        if (resultSet != null) {
            this.id = JDBCUtils.safeGetLong(resultSet, "USER_ID");
            this.name = JDBCUtils.safeGetString(resultSet, "USERNAME");
            this.externalName = JDBCUtils.safeGetString(resultSet, "EXTERNAL_NAME");
            this.status = JDBCUtils.safeGetString(resultSet, "ACCOUNT_STATUS");

            this.createDate = JDBCUtils.safeGetTimestamp(resultSet, "CREATED");
            this.lockDate = JDBCUtils.safeGetTimestamp(resultSet, "LOCK_DATE");
            this.expiryDate = JDBCUtils.safeGetTimestamp(resultSet, "EXPIRY_DATE");
            this.defaultTablespace = JDBCUtils.safeGetString(resultSet, "DEFAULT_TABLESPACE");
            this.tempTablespace = JDBCUtils.safeGetString(resultSet, "TEMPORARY_TABLESPACE");

            this.profile = JDBCUtils.safeGetString(resultSet, "PROFILE");
            this.consumerGroup = JDBCUtils.safeGetString(resultSet, "INITIAL_RSRC_CONSUMER_GROUP");
        }
    }

    @Property(name = "ID", order = 1, description = "ID number of the user")
    public long getId()
    {
        return id;
    }

    @Property(name = "User name", viewable = true, order = 2, description = "Name of the user")
    public String getName() {
        return name;
    }

    @Property(name = "External name", order = 3, description = "User external name")
    public String getExternalName()
    {
        return externalName;
    }

    @Property(name = "Status", viewable = true, order = 4, description = "Account status")
    public String getStatus()
    {
        return status;
    }

    @Property(name = "Create date", viewable = true, order = 5, description = "User creation date")
    public Timestamp getCreateDate()
    {
        return createDate;
    }

    @Property(name = "Lock date", order = 6, description = "Date the account was locked if account status was LOCKED")
    public Timestamp getLockDate()
    {
        return lockDate;
    }

    @Property(name = "Expiry date", order = 7, description = "Date of expiration of the account")
    public Timestamp getExpiryDate()
    {
        return expiryDate;
    }

    @Property(name = "Default tablespace", order = 8, description = "Default tablespace for data")
    @LazyProperty(cacheValidator = OracleTablespace.TablespaceReferenceValidator.class)
    public Object getDefaultTablespace(DBRProgressMonitor monitor) throws DBException
    {
        return OracleTablespace.resolveTablespaceReference(monitor, this, "defaultTablespace");
    }

    @Property(name = "Temporary tablespace", order = 9, description = "Default tablespace for temporary tables or a tablespace group")
    @LazyProperty(cacheValidator = OracleTablespace.TablespaceReferenceValidator.class)
    public Object getTempTablespace(DBRProgressMonitor monitor) throws DBException
    {
        return OracleTablespace.resolveTablespaceReference(monitor, this, "tempTablespace");
    }

    public Object getTablespaceReference(Object propertyId)
    {
        return "defaultTablespace".equals(propertyId) ? defaultTablespace : tempTablespace;
    }

    @Property(name = "Profile", order = 10, description = "User resource profile name")
    public String getProfile()
    {
        return profile;
    }

    @Property(name = "Consumer group", order = 11, description = "Initial resource consumer group for the user")
    public String getConsumerGroup()
    {
        return consumerGroup;
    }

}