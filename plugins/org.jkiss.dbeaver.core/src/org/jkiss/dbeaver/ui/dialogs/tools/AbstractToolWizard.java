/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.dialogs.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.model.DBPClientHome;
import org.jkiss.dbeaver.model.DBPConnectionInfo;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.model.struct.DBSDataSourceContainer;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.runtime.RuntimeUtils;
import org.jkiss.dbeaver.ui.UIUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Abstract wizard
 */
public abstract class AbstractToolWizard<BASE_OBJECT extends DBSObject>
        extends Wizard implements DBRRunnableWithProgress {

    static final Log log = LogFactory.getLog(AbstractToolWizard.class);

    private final BASE_OBJECT databaseObject;
    private DBPClientHome clientHome;
    private DBPConnectionInfo connectionInfo;

    protected String task;
    protected final DatabaseWizardPageLog logPage;
    private boolean finished;

    protected AbstractToolWizard(BASE_OBJECT databaseObject, String task)
    {
        this.databaseObject = databaseObject;
        this.task = task;
        this.logPage = new DatabaseWizardPageLog(task);
    }

    @Override
    public boolean canFinish()
    {
        return !finished && super.canFinish();
    }

    public BASE_OBJECT getDatabaseObject()
    {
        return databaseObject;
    }

    public DBPConnectionInfo getConnectionInfo()
    {
        return connectionInfo;
    }

    public DBPClientHome getClientHome()
    {
        return clientHome;
    }

    public abstract DBPClientHome findServerHome(String clientHomeId);

    @Override
    public void createPageControls(Composite pageContainer)
    {
        super.createPageControls(pageContainer);

        WizardPage currentPage = (WizardPage) getStartingPage();

        DBSDataSourceContainer container = getDatabaseObject().getDataSource().getContainer();
        connectionInfo = container.getConnectionInfo();
        String clientHomeId = connectionInfo.getClientHomeId();
        if (clientHomeId == null) {
            currentPage.setErrorMessage(CoreMessages.tools_wizard_message_no_client_home);
            getContainer().updateMessage();
            return;
        }
        clientHome = findServerHome(clientHomeId);//MySQLDataSourceProvider.getServerHome(clientHomeId);
        if (clientHome == null) {
            currentPage.setErrorMessage(NLS.bind(CoreMessages.tools_wizard_message_client_home_not_found, clientHomeId));
            getContainer().updateMessage();
        }
    }

    @Override
    public boolean performFinish() {
        if (getContainer().getCurrentPage() != logPage) {
            getContainer().showPage(logPage);
        }
        try {
            RuntimeUtils.run(getContainer(), true, true, this);
        }
        catch (InterruptedException ex) {
            UIUtils.showMessageBox(getShell(), task, NLS.bind(CoreMessages.tools_wizard_error_task_canceled, task, getDatabaseObject().getName()), SWT.ICON_ERROR);
            return false;
        }
        catch (InvocationTargetException ex) {
            UIUtils.showErrorDialog(
                getShell(),
                task + CoreMessages.tools_wizard_error_task_error_title,
                CoreMessages.tools_wizard_error_task_error_message + task,
                ex.getTargetException());
            return false;
        }
        finally {
            getContainer().updateButtons();
        }
        onSuccess();
        return false;
    }

    public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        try {
            finished = executeProcess(monitor);
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        } finally {
            //finished = true;
        }
        if (monitor.isCanceled()) {
            throw new InterruptedException();
        }
    }

    public boolean executeProcess(DBRProgressMonitor monitor)
        throws IOException, CoreException, InterruptedException
    {
        try {
            final List<String> commandLine = getCommandLine();
            final File execPath = new File(commandLine.get(0));

            ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
            processBuilder.directory(execPath.getParentFile());
            if (this.isMergeProcessStreams()) {
                processBuilder.redirectErrorStream(true);
            }
            Process process = processBuilder.start();

            startProcessHandler(monitor, processBuilder, process);
            Thread.sleep(100);

            for (;;) {
                Thread.sleep(100);
                if (monitor.isCanceled()) {
                    process.destroy();
                }
                try {
                    final int exitCode = process.exitValue();
                    if (exitCode != 0) {
                        logPage.appendLog(NLS.bind(CoreMessages.tools_wizard_log_exit_code, exitCode));
                        return false;
                    }
                } catch (Exception e) {
                    // Still running
                    continue;
                }
                break;
            }
            //process.waitFor();
        } catch (IOException e) {
            log.error(e);
            logPage.appendLog(NLS.bind(CoreMessages.tools_wizard_log_io_error, e.getMessage()));
            return false;
        }

        return true;
    }

    protected boolean isMergeProcessStreams()
    {
        return false;
    }

    public boolean isVerbose()
    {
        return false;
    }

    protected void onSuccess()
    {

    }

    abstract protected java.util.List<String> getCommandLine() throws IOException;

    public abstract void fillProcessParameters(List<String> cmd) throws IOException;

    protected abstract void startProcessHandler(DBRProgressMonitor monitor, ProcessBuilder processBuilder, Process process);

}
