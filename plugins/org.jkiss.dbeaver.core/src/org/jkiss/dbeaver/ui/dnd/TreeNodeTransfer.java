/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.dnd;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.jkiss.dbeaver.model.navigator.DBNNode;

import java.util.Collection;

/**
 * Used to move DBNNode around in a database navigator.
 */
public final class TreeNodeTransfer extends LocalObjectTransfer<Collection<DBNNode>> {

	private static final TreeNodeTransfer INSTANCE = new TreeNodeTransfer();
	private static final String TYPE_NAME = "DBNNode Transfer"//$NON-NLS-1$
			+ System.currentTimeMillis() + ":" + INSTANCE.hashCode();//$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton instance.
	 *
	 * @return The singleton instance
	 */
	public static TreeNodeTransfer getInstance() {
		return INSTANCE;
	}

	private TreeNodeTransfer() {
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

    public static Collection<DBNNode> getFromClipboard()
    {
        Clipboard clipboard = new Clipboard(Display.getDefault());
        try {
            return (Collection<DBNNode>) clipboard.getContents(TreeNodeTransfer.getInstance());
        } finally {
            clipboard.dispose();
        }
    }

}
