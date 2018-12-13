package com.sye.settings;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 17:15
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public abstract class SDKInfo {

    /**
     * Gets identifier for channel
     *
     * @return
     */
    public abstract String getIFC();

    /**
     * Gets identifier for project
     *
     * @return
     */
    public abstract String getIFP();

    /**
     * Gets identifier for device/platform/phone
     *
     * @return
     */
    public abstract String getIFD();

    /**
     * @return
     */
    public abstract String getVersion();

    public abstract int getVersionInt();

    public abstract int getSupported();

}
