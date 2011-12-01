/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.mysql.edit;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.jkiss.dbeaver.ext.IDatabasePersistAction;
import org.jkiss.dbeaver.ext.mysql.MySQLMessages;
import org.jkiss.dbeaver.ext.mysql.model.MySQLDataSource;
import org.jkiss.dbeaver.ext.mysql.model.MySQLUser;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBECommandFilter;
import org.jkiss.dbeaver.model.edit.DBECommandQueue;
import org.jkiss.dbeaver.model.edit.DBEObjectMaker;
import org.jkiss.dbeaver.model.edit.prop.DBECommandComposite;
import org.jkiss.dbeaver.model.impl.edit.AbstractDatabasePersistAction;
import org.jkiss.dbeaver.model.impl.edit.DBECommandAbstract;
import org.jkiss.dbeaver.model.impl.edit.DatabaseObjectScriptCommand;
import org.jkiss.dbeaver.model.impl.jdbc.edit.JDBCObjectManager;

import java.util.Map;

/**
 * MySQLUserManager
 */
public class MySQLUserManager extends JDBCObjectManager<MySQLUser> implements DBEObjectMaker<MySQLUser, MySQLDataSource>, DBECommandFilter<MySQLUser> {

    public long getMakerOptions()
    {
        return FEATURE_EDITOR_ON_CREATE;
    }

    public MySQLUser createNewObject(IWorkbenchWindow workbenchWindow, IEditorPart activeEditor, DBECommandContext commandContext, MySQLDataSource parent, Object copyFrom)
    {
        MySQLUser newUser = new MySQLUser(parent, null);
        if (copyFrom instanceof MySQLUser) {
            MySQLUser tplUser = (MySQLUser)copyFrom;
            newUser.setUserName(tplUser.getUserName());
            newUser.setHost(tplUser.getHost());
            newUser.setMaxQuestions(tplUser.getMaxQuestions());
            newUser.setMaxUpdates(tplUser.getMaxUpdates());
            newUser.setMaxConnections(tplUser.getMaxConnections());
            newUser.setMaxUserConnections(tplUser.getMaxUserConnections());
        }
        commandContext.addCommand(new CommandCreateUser(newUser), new CreateObjectReflector(), true);

        return newUser;
    }

    public void deleteObject(DBECommandContext commandContext, MySQLUser user, Map<String, Object> options)
    {
        commandContext.addCommand(new CommandDropUser(user), new DeleteObjectReflector(), true);
    }

    public void filterCommands(DBECommandQueue<MySQLUser> queue)
    {
        if (!queue.isEmpty()) {
            // Add privileges flush to the tail
            queue.add(
                new DatabaseObjectScriptCommand<MySQLUser>(
                    queue.getObject(),
                    MySQLMessages.edit_user_manager_command_flush_privileges,
                    "FLUSH PRIVILEGES")); //$NON-NLS-1$
        }
    }

    private static class CommandCreateUser extends DBECommandAbstract<MySQLUser> {
        protected CommandCreateUser(MySQLUser user)
        {
            super(user, MySQLMessages.edit_user_manager_command_create_user);
        }
    }


    private static class CommandDropUser extends DBECommandComposite<MySQLUser, UserPropertyHandler> {
        protected CommandDropUser(MySQLUser user)
        {
            super(user, MySQLMessages.edit_user_manager_command_drop_user);
        }
        public IDatabasePersistAction[] getPersistActions()
        {
            return new IDatabasePersistAction[] {
                new AbstractDatabasePersistAction("Drop user", "DROP USER " + getObject().getFullName()) { //$NON-NLS-2$
                    @Override
                    public void handleExecute(Throwable error)
                    {
                        if (error == null) {
                            getObject().setPersisted(false);
                        }
                    }
                }};
        }
    }

}

