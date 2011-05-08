/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.impl.jdbc;

import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceInfo;
import org.jkiss.dbeaver.model.meta.IPropertyValueTransformer;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.ui.preferences.PrefConstants;

/**
 * Object name case transformer
 */
public class JDBCObjectNameCaseTransformer implements IPropertyValueTransformer<DBSObject, String> {

    public String transform(DBSObject object, String value)
    {
        return transformName(object, value);
    }

    public static String transformName(DBSObject object, String value)
    {
        final DBPDataSource dataSource = object.getDataSource();
        final boolean isNameCaseSensitive = dataSource.getContainer().getPreferenceStore().getBoolean(PrefConstants.META_CASE_SENSITIVE);
        if (isNameCaseSensitive) {
            return value;
        }
        final DBPDataSourceInfo info = dataSource.getInfo();
        if (!info.supportsQuotedMixedCase() && !info.supportsUnquotedMixedCase()) {
            // Mixed case not supported - so leave it as is
            return value;
        }
        switch (info.storesUnquotedCase()) {
            case LOWER:
                return value.toLowerCase();
            case UPPER:
                return value.toUpperCase();
            case MIXED:
            default:
                // Stores case as is - leave as is
                return value;
        }
    }
}
