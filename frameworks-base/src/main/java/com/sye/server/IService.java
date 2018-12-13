package com.sye.server;

import android.content.Context;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 19:04
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public abstract class IService {

    private final Context mContext;

    public IService(Context context) {
        if (context == null)
            throw new RuntimeException("context is null");

        this.mContext = context;
    }

    public final Context getContext() {
        return this.mContext;
    }

    /**
     * Publish the service so it is only accessible to the system process.
     */
    protected final <T> void publishLocalService(Class<T> type, T service) {
        LocalServices.addService(type, service);
    }

    /**
     * Get a local service by interface.
     */
    protected final <T> T getLocalService(Class<T> type) {
        return LocalServices.getService(type);
    }

}
