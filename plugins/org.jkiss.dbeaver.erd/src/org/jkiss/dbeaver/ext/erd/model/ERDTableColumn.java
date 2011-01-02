/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

/*
 * Created on Jul 13, 2004
 */
package org.jkiss.dbeaver.ext.erd.model;

import org.eclipse.swt.graphics.Image;
import org.jkiss.dbeaver.ext.ui.IObjectImageProvider;
import org.jkiss.dbeaver.model.struct.DBSTableColumn;
import org.jkiss.dbeaver.ui.DBIcon;

/**
 * Column entry in model Table
 * @author Serge Rieder
 */
public class ERDTableColumn extends ERDObject<DBSTableColumn>
{
    private boolean inPrimaryKey;
    private boolean inForeignKey;

    public ERDTableColumn(DBSTableColumn dbsTableColumn, boolean inPrimaryKey) {
        super(dbsTableColumn);
        this.inPrimaryKey = inPrimaryKey;
    }

	public String getLabelText()
	{
		return object.getName();
	}

    public Image getLabelImage()
    {
        if (object instanceof IObjectImageProvider) {
            return ((IObjectImageProvider)object).getObjectImage();
        } else {
            return DBIcon.TYPE_UNKNOWN.getImage();
        }
    }

    public boolean isInPrimaryKey() {
        return inPrimaryKey;
    }

    public boolean isInForeignKey() {
        return inForeignKey;
    }

    public void setInForeignKey(boolean inForeignKey) {
        this.inForeignKey = inForeignKey;
    }
}