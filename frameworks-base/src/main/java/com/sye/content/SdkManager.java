package com.sye.content;

import android.content.Context;

import com.sye.settings.SdkFinal;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 15:08
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public abstract class SdkManager {

    private static final Object mLock = new Object();
    private static SdkManager instance;

    public static final SdkManager getInstance(Context context) {
        if (instance == null) {
            synchronized (mLock) {
                instance = new LaunchFactory(context);
            }
        }
        return instance;
    }

    public static final String getVersionName() {
        return SdkFinal.getVersionName();
    }

    public static final int getVersionCode() {
        return SdkFinal.getVersionCode();
    }

    public abstract SdkManager init();

    public abstract SdkManager init(String cid, String aid);


}
