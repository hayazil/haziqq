/*
 * Copyright (C) 2010-2012 Serge Rieder
 * serge@jkiss.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jkiss.dbeaver.ui.editors.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jkiss.dbeaver.core.DBeaverCore;
import org.jkiss.dbeaver.ext.IAutoSaveEditorInput;
import org.jkiss.dbeaver.ext.IDataSourceContainerProvider;
import org.jkiss.dbeaver.ext.IDataSourceProvider;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.struct.DBSDataSourceContainer;
import org.jkiss.dbeaver.ui.editors.ProjectFileEditorInput;

/**
 * SQLEditorInput
 */
public class SQLEditorInput extends ProjectFileEditorInput implements IPersistableElement, IAutoSaveEditorInput, IDataSourceProvider, IDataSourceContainerProvider
{
    public static final QualifiedName PROP_DATA_SOURCE_ID = new QualifiedName("org.jkiss.dbeaver", "sql-editor-data-source-id");

    static final Log log = LogFactory.getLog(SQLEditorInput.class);

    private DBSDataSourceContainer dataSourceContainer;
    private String scriptName;

    public SQLEditorInput(IFile file)
    {
        super(file);
        this.scriptName = file.getFullPath().removeFileExtension().lastSegment();
        this.dataSourceContainer = getScriptDataSource(file);
        if (this.dataSourceContainer == null) {
            setScriptDataSource(getFile(), null);
        }
    }

    @Override
    public DBSDataSourceContainer getDataSourceContainer()
    {
        return dataSourceContainer;
    }

    public void setDataSourceContainer(DBSDataSourceContainer container)
    {
        if (dataSourceContainer == container) {
            return;
        }
        dataSourceContainer = container;
        IFile file = getFile();
        if (file != null) {
            setScriptDataSource(file, dataSourceContainer);
        }
    }

    @Override
    public String getName()
    {
        String dsName = "<None>";
        if (dataSourceContainer != null) {
            dsName = dataSourceContainer.getName();
        }
        return scriptName + " (" + dsName + ")";
    }

    @Override
    public String getToolTipText()
    {
        if (dataSourceContainer == null) {
            return super.getName();
        }
        return
            "Script: " + getFile().getName() + "\n" +
            "Connection: " + dataSourceContainer.getName() + "\n" +
            "Type: " + (dataSourceContainer.getDriver() == null ? "Unknown" : dataSourceContainer.getDriver().getName()) + "\n" +
            "URL: " + dataSourceContainer.getConnectionInfo().getUrl();
    }

    @Override
    public IPersistableElement getPersistable()
    {
        return this;
    }

    @Override
    public String getFactoryId()
    {
        return SQLEditorInputFactory.getFactoryId();
    }

    @Override
    public void saveState(IMemento memento)
    {
        SQLEditorInputFactory.saveState(memento, this);
    }

    @Override
    public boolean isAutoSaveEnabled()
    {
        return true;
    }

    @Override
    public DBPDataSource getDataSource()
    {
        return getDataSourceContainer().getDataSource();
    }

    public static DBSDataSourceContainer getScriptDataSource(IFile file)
    {
        try {
            String dataSourceId = file.getPersistentProperty(PROP_DATA_SOURCE_ID);
            if (dataSourceId != null) {
                return DBeaverCore.getInstance().getProjectRegistry().getDataSourceRegistry(file.getProject()).getDataSource(dataSourceId);
            } else {
                return null;
            }
        } catch (CoreException e) {
            log.error("Internal error while reading file property", e);
            return null;
        }
    }

    public static void setScriptDataSource(IFile file, DBSDataSourceContainer dataSourceContainer)
    {
        try {
            file.setPersistentProperty(PROP_DATA_SOURCE_ID, dataSourceContainer == null ? null : dataSourceContainer.getId());
        } catch (CoreException e) {
            log.error("Internal error while writing file property", e);
        }
    }

}
