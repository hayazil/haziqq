/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.dialogs.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.jkiss.dbeaver.model.data.DBDValueController;

/**
 * TextViewDialog
 */
public class TextViewDialog extends ValueViewDialog {

    private Text textEdit;

    public TextViewDialog(DBDValueController valueController) {
        super(valueController);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Object value = getValueController().getValue();

        Composite dialogGroup = (Composite)super.createDialogArea(parent);

        Label label = new Label(dialogGroup, SWT.NONE);
        label.setText("Value: ");

        int style = SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP;
        if (getValueController().isReadOnly()) {
            style |= SWT.READ_ONLY;
        }
        textEdit = new Text(dialogGroup, style);

        textEdit.setText(value == null ? "" : value.toString());
        int maxSize = getValueController().getColumnMetaData().getDisplaySize();
        if (maxSize > 0) {
            textEdit.setTextLimit(maxSize);
        }
        textEdit.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        gd.widthHint = 300;
        gd.grabExcessVerticalSpace = true;
        textEdit.setLayoutData(gd);
        textEdit.setFocus();
        return dialogGroup;
    }

    protected void createInfoControls(Composite infoGroup)
    {
        Label label = new Label(infoGroup, SWT.NONE);
        label.setText("Length: ");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = 50;
        Text text = new Text(infoGroup, SWT.BORDER | SWT.READ_ONLY);
        text.setText(String.valueOf(getValueController().getColumnMetaData().getDisplaySize()));
        text.setLayoutData(gd);
    }


    @Override
    protected void applyChanges()
    {
        getValueController().updateValue(textEdit.getText());
    }

}
