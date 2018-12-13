package com.sye.content;

import android.content.Context;

import com.sye.server.IService;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 19:48
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class SdkServiceManager extends IService {

    public SdkServiceManager(Context context) {
        super(context);
    }

    /**
     * Publish the service so it is only accessible to the system process.
     */
    public final <T> void pushService(Class<T> type, T service) {
        super.publishLocalService(type, service);
    }

    /**
     * Get a local service by interface.
     */
    public final <T> T getService(Class<T> type) {
        return super.getLocalService(type);
    }

}
