/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.editors.entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.jkiss.dbeaver.ext.ui.IRefreshablePart;
import org.jkiss.dbeaver.model.navigator.DBNDatabaseFolder;
import org.jkiss.dbeaver.model.navigator.DBNDatabaseNode;
import org.jkiss.dbeaver.model.navigator.DBNNode;
import org.jkiss.dbeaver.model.struct.DBSDataSourceContainer;
import org.jkiss.dbeaver.registry.DriverDescriptor;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.actions.navigator.NavigatorHandlerObjectOpen;
import org.jkiss.dbeaver.ui.dialogs.driver.DriverEditDialog;
import org.jkiss.dbeaver.ui.views.properties.ProxyPageSite;
import org.jkiss.dbeaver.ui.views.properties.PropertyPageTabbed;

import java.util.ArrayList;
import java.util.List;

/**
 * DefaultObjectEditor
 */
public class DefaultObjectEditor extends EditorPart implements IRefreshablePart
{
    static final Log log = LogFactory.getLog(DefaultObjectEditor.class);

    private PropertyPageTabbed properties;

    public DefaultObjectEditor()
    {
    }

    public void createPartControl(Composite parent)
    {
        EntityEditorInput entityInput = (EntityEditorInput) getEditorInput();
        DBNNode node = entityInput.getTreeNode();

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(1, true);
        container.setLayout(gl);

        {
            Group infoGroup = new Group(container, SWT.NONE);
            infoGroup.setText("Information");
            gl = new GridLayout(3, false);
            infoGroup.setLayout(gl);

            if (node instanceof DBNDatabaseNode) {
                DBNDatabaseNode dbNode = (DBNDatabaseNode)node;
                if (dbNode.getObject() != null && dbNode.getObject().getDataSource() != null) {
                    final DBSDataSourceContainer dsContainer = dbNode.getObject().getDataSource().getContainer();
                    Label objectIcon = new Label(infoGroup, SWT.NONE);
                    objectIcon.setImage(dsContainer.getDriver().getIcon());

                    Label objectLabel = new Label(infoGroup, SWT.NONE);
                    objectLabel.setText("Driver:");

                    Link objectLink = new Link(infoGroup, SWT.NONE);
                    objectLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                    objectLink.setText("<A>" + dsContainer.getDriver().getName() + "</A>");
                    objectLink.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent e)
                        {
                            DriverEditDialog dialog = new DriverEditDialog(getSite().getShell(), (DriverDescriptor) dsContainer.getDriver());
                            dialog.open();
                        }
                    });
                }
            }
            List<DBNDatabaseNode> nodeList = new ArrayList<DBNDatabaseNode>();
            for (DBNNode n = node; n != null; n = n.getParentNode()) {
                if (n instanceof DBNDatabaseNode && !(n instanceof DBNDatabaseFolder)) {
                    nodeList.add(0, (DBNDatabaseNode)n);
                }
            }
            for (final DBNDatabaseNode databaseNode : nodeList) {
                Label objectIcon = new Label(infoGroup, SWT.NONE);
                objectIcon.setImage(databaseNode.getNodeIconDefault());

                Label objectLabel = new Label(infoGroup, SWT.NONE);
                objectLabel.setText(databaseNode.getMeta().getItemLabel() + ":");

                Link objectLink = new Link(infoGroup, SWT.NONE);
                objectLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

                if (databaseNode == node) {
                    objectLink.setText(databaseNode.getNodeName());
                } else {
                    objectLink.setText("<A>" + databaseNode.getNodeName() + "</A>");
                    objectLink.addSelectionListener(new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            NavigatorHandlerObjectOpen.openEntityEditor(databaseNode, null, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
                        }
                    });
                    objectLink.setToolTipText("Open '" + databaseNode.getNodeName() + "' viewer");
                }
            }
        }

        {
            Composite propsGroup = container;

            Composite propsPlaceholder = new Composite(propsGroup, SWT.BORDER);
            propsPlaceholder.setLayoutData(new GridData(GridData.FILL_BOTH));
            propsPlaceholder.setLayout(new FormLayout());

            DBNNode itemObject = entityInput.getTreeNode();
            //final PropertyCollector propertyCollector = new PropertyCollector(itemObject);
            //List<PropertyAnnoDescriptor> annoProps = PropertyAnnoDescriptor.extractAnnotations(itemObject);

            properties = new PropertyPageTabbed();
            properties.init(new ProxyPageSite(getSite()));
            properties.createControl(propsPlaceholder);
            if (itemObject != null) {
                properties.selectionChanged(this, new StructuredSelection(itemObject));
            }
        }
    }

    @Override
    public void dispose()
    {
        if (properties != null) {
            properties.dispose();
        }
        super.dispose();
    }

    public void setFocus()
    {
    }

    public void doSave(IProgressMonitor monitor)
    {

    }

    public void doSaveAs()
    {
    }

    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException
    {
        setSite(site);
        setInput(input);
    }

    public boolean isDirty()
    {
        return false;
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }

    public void refreshPart(Object source) {
        if (properties != null) {
            properties.refresh();
        }
    }
}