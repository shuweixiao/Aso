package com.sye.settings;

import com.sye.os.Build;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/12/2018 15:27
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class SdkStatus {


    public static final int getVersionCode() {
        return Build.VERSION_CODE;
    }


    public static final String getVersionName() {
        return Build.VERSION_NAME;
    }

    public static final String format() {
        return Build.format();
    }

}
