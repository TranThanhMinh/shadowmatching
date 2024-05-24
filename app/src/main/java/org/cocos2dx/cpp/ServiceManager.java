package org.cocos2dx.cpp;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceManager {
    static final String TAG = ServiceManager.class.getSimpleName();
    static Class sClass = null;
    static Method sMethod_addService = null;
    static Method sMethod_getService = null;
    static Method sMethod_checkService = null;
    static Method sMethod_listServices = null;

    static
    {
        try {
            sClass = Class.forName("android.os.ServiceManager");
            sMethod_addService = sClass.getMethod("addService", String.class, IBinder.class);
            sMethod_getService = sClass.getMethod("getService", String.class);
            sMethod_checkService = sClass.getMethod("checkService", String.class);
            sMethod_listServices = sClass.getMethod("listServices");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Cannot find class for android.os.ServiceManager", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Cannot find all methods for android.os.ServiceManager", e);
        }
    }

    /**
     * Returns a reference to a service with the given name.
     *
     * @param name the name of the service to get
     * @return a reference to the service, or <code>null</code> if the service doesn't exist
     */
    public static IBinder getService(String name) {
        try {
            return (IBinder) sMethod_getService.invoke(null, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Place a new @a service called @a name into the service
     * manager.
     *
     * @param name the name of the new service
     * @param service the service object
     */
    public static void addService(String name, IBinder service) {
        try {
            sMethod_addService.invoke(null, name, service);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve an existing service called @a name from the
     * service manager.  Non-blocking.
     */
    public static IBinder checkService(String name) {
        try {
            return (IBinder) sMethod_checkService.invoke(null, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return a list of all currently running services.
     */
    public static String[] listServices() throws RemoteException {
        try {
            return (String[]) sMethod_listServices.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}