/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

/*
 * Created on Jul 23, 2004
 */
package org.jkiss.dbeaver.ext.erd.action;

import org.eclipse.jface.action.Action;
import org.jkiss.dbeaver.ext.erd.Activator;
import org.jkiss.dbeaver.ext.erd.editor.ERDEditor;

/**
 * Action to toggle the layout between manual and automatic
 *
 * @author Serge Rieder
 */
public class DiagramLayoutAction extends Action
{
	private ERDEditor editor;

	public DiagramLayoutAction(ERDEditor editor)
	{
		super("Arrange Diagram", Activator.getImageDescriptor("icons/arrangeall.png"));
		this.editor = editor;
	}

	public void run()
	{
        //editor.get
        editor.getDiagramPart().rearrangeDiagram();
	}

}