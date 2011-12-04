/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.jkiss.dbeaver.DBeaverConstants;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.ui.DBIcon;
import org.jkiss.dbeaver.ui.IActionConstants;
import org.jkiss.dbeaver.ui.editors.entity.ObjectPropertiesEditor;
import org.jkiss.utils.CommonUtils;

/**
 * EntityEditorDescriptor
 */
public class EntityEditorDescriptor extends AbstractContextDescriptor
{
    public static final String EXTENSION_ID = "org.jkiss.dbeaver.databaseEditor"; //NON-NLS-1 //$NON-NLS-1$

    public static final String DEFAULT_OBJECT_EDITOR_ID = "default.object.editor"; //NON-NLS-1 //$NON-NLS-1$

    public static final String POSITION_PROPS = IActionConstants.MB_ADDITIONS_PROPS;
    public static final String POSITION_START = IActionConstants.MB_ADDITIONS_START;
    public static final String POSITION_MIDDLE = IActionConstants.MB_ADDITIONS_MIDDLE;
    public static final String POSITION_END = IActionConstants.MB_ADDITIONS_END;

    public static enum Type {
        editor,
        section
    }

    private String id;
    private String className;
    private String contributorClassName;
    private boolean main;
    private String name;
    private String description;
    private String position;
    private Image icon;
    private Type type;

    //private List<Class<?>> objectClasses;
    private Class<? extends IEditorPart> editorClass;
    private Class<? extends IEditorActionBarContributor> contributorClass;

    EntityEditorDescriptor()
    {
        super(new IContributor() {
            public String getName() {
                return DBeaverConstants.PLUGIN_ID;
            }
        }, null);
        this.id = DEFAULT_OBJECT_EDITOR_ID;
        this.className = ObjectPropertiesEditor.class.getName();
        this.contributorClassName = null;
        this.main = true;
        this.name = CoreMessages.registry_entity_editor_descriptor_name;
        this.description = CoreMessages.registry_entity_editor_descriptor_description;
        this.position = null;
        this.icon = DBIcon.TREE_DATABASE.getImage();
        this.type = Type.editor;
    }

    public EntityEditorDescriptor(IConfigurationElement config)
    {
        super(config.getContributor(), config);

        this.id = config.getAttribute(RegistryConstants.ATTR_ID);
        this.className = config.getAttribute(RegistryConstants.ATTR_CLASS);
        this.contributorClassName = config.getAttribute(RegistryConstants.ATTR_CONTRIBUTOR);
        this.main = CommonUtils.getBoolean(config.getAttribute(RegistryConstants.ATTR_MAIN));
        this.name = config.getAttribute(RegistryConstants.ATTR_LABEL);
        this.description = config.getAttribute(RegistryConstants.ATTR_DESCRIPTION);
        this.position = config.getAttribute(RegistryConstants.ATTR_POSITION);
        this.icon = iconToImage(config.getAttribute(RegistryConstants.ATTR_ICON));
        String typeName = config.getAttribute(RegistryConstants.ATTR_TYPE);
        if (!CommonUtils.isEmpty(typeName)) {
            this.type = Type.valueOf(typeName);
        }
    }

    public String getId()
    {
        return id;
    }

/*
    public String getClassName()
    {
        return className;
    }

    public String getObjectType()
    {
        return objectType;
    }
*/

    public boolean isMain()
    {
        return main;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPosition()
    {
        return position;
    }

    public Image getIcon()
    {
        return icon;
    }

    public Type getType()
    {
        return type;
    }

    private Class<? extends IEditorPart> getEditorClass()
    {
        if (editorClass == null) {
            editorClass = getObjectClass(className, IEditorPart.class);
        }
        return editorClass;
    }

    public Class<? extends IEditorActionBarContributor> getContributorClass()
    {
        if (contributorClass == null) {
            if (contributorClassName == null) {
                return null;
            }
            contributorClass = getObjectClass(contributorClassName, IEditorActionBarContributor.class);
        }
        return contributorClass;
    }

    public IEditorPart createEditor()
    {
        Class<? extends IEditorPart> clazz = getEditorClass();
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            log.error("Error instantiating entity editor '" + className + "'", ex); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }

}
