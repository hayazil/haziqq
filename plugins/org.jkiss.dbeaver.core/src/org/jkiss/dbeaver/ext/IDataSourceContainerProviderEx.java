/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext;

import org.jkiss.dbeaver.model.struct.DBSDataSourceContainer;

/**
 * DataSource provider editor.
 * May be editor, view or selection element
 */
public interface IDataSourceContainerProviderEx extends IDataSourceContainerProvider {

    /**
     * Change underlying datasource container
     * @return data source object.
     */
    void setDataSourceContainer(DBSDataSourceContainer dataSourceContainer);

}