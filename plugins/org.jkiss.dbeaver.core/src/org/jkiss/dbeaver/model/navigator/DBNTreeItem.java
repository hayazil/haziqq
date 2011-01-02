/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.navigator;

import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.registry.tree.DBXTreeItem;
import org.jkiss.dbeaver.ui.ICommandIds;

/**
 * DBNTreeItem
 */
public class DBNTreeItem extends DBNTreeNode
{
    private DBXTreeItem meta;
    private DBSObject object;

    DBNTreeItem(DBNNode parent, DBXTreeItem meta, DBSObject object, boolean reflect)
    {
        super(parent);
        this.meta = meta;
        this.object = object;
        if (this.getModel() != null) {
            this.getModel().addNode(this, reflect);
        }
    }

    protected void dispose(boolean reflect)
    {
        if (this.getModel() != null) {
            // Notify model
            // Reflect changes only if underlying object is not persisted
            this.getModel().removeNode(this, reflect);
        }
        this.object = null;
        super.dispose(reflect);
    }

    public DBXTreeItem getMeta()
    {
        return meta;
    }

    @Override
    protected void reloadObject(DBRProgressMonitor monitor, DBSObject object) {
        getModel().removeNode(this, false);
        this.object = object;
        getModel().addNode(this, false);
    }

    public DBSObject getObject()
    {
        return object;
    }

    public Object getValueObject()
    {
        return object;
    }

    public String getDefaultCommandId()
    {
        return ICommandIds.CMD_OBJECT_OPEN;
    }

    public final boolean isManagable() 
    {
        return true;
    }
}
