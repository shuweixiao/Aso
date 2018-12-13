package com.sye.server;

import java.util.HashMap;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 19:08
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
final class LocalServices {

    private static final HashMap<Class<?>, Object> sLocalServiceObjects =
            new HashMap<Class<?>, Object>();

    private LocalServices() {
    }

    /**
     * Returns a local service instance that implements the specified interface.
     *
     * @param type The type of service.
     * @return The service object.
     */
    public static <T> T getService(Class<T> type) {
        synchronized (sLocalServiceObjects) {
            return (T) sLocalServiceObjects.get(type);
        }
    }

    /**
     * Adds a service instance of the specified interface to the global registry of local services.
     */
    public static <T> void addService(Class<T> type, T service) {
        synchronized (sLocalServiceObjects) {
            if (sLocalServiceObjects.containsKey(type)) {
                throw new IllegalStateException("Overriding service registration");
            }
            sLocalServiceObjects.put(type, service);
        }
    }
}
