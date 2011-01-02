/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.runtime.load;

import net.sf.jkiss.utils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.runtime.load.jobs.LoadingJob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Loading utils
 */
public class LoadingUtils {

    static final Log log = LogFactory.getLog(LoadingUtils.class);

    public static Object extractPropertyValue(DBRProgressMonitor monitor, Object object, String propertyName)
        throws DBException
    {
        // Read property using reflection
        if (object == null) {
            return null;
        }
        try {
            Method getter = findPropertyReadMethod(object.getClass(), propertyName);
            if (getter == null) {
                log.warn("Could not find property '" + propertyName + "' read method in '" + object.getClass().getName() + "'");
                return null;
            }
            Class<?>[] paramTypes = getter.getParameterTypes();
            if (paramTypes.length == 0) {
                // No params - just read it
                return getter.invoke(object);
            } else if (paramTypes.length == 1 && paramTypes[0] == DBRProgressMonitor.class) {
                // Read with progress monitor
                return getter.invoke(object, monitor);
            } else {
                log.warn("Could not read property '" + propertyName + "' - bad method signature: " + getter.toString());
                return null;
            }
        }
        catch (IllegalAccessException ex) {
            log.warn("Error accessing items " + propertyName, ex);
            return null;
        }
        catch (InvocationTargetException ex) {
            throw new DBException(ex.getTargetException());
        }
    }

    public static Method findPropertyReadMethod(Class<?> clazz, String propertyName)
    {
        String methodName = BeanUtils.propertyNameToMethodName(propertyName);
        String getName = "get" + methodName;
        String isName = "is" + methodName;
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (
                (!Modifier.isPublic(method.getModifiers())) ||
                (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) ||
                (method.getReturnType().equals(void.class)))
            {
                // skip
            } else if (method.getName().equals(getName)) {
                // If it matches the get name, it's the right method
                return method;
            } else if (method.getName().equals(isName) && method.getReturnType().equals(boolean.class)) {
                return method;
            }
        }
        return null;
    }

    public static <RESULT> LoadingJob<RESULT> createService(
        ILoadService<RESULT> loadingService,
        ILoadVisualizer<RESULT> visualizer)
    {
        return new LoadingJob<RESULT>(loadingService, visualizer);
    }
}
