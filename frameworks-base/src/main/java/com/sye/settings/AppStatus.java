package com.sye.settings;

import android.content.Context;

/**
 * *****************************************************************************************
 * Created by super.dragon on 7/20/2018 11:21
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class AppStatus {

    private static final Object mLock = new Object();
    private static volatile AppStatus instance;

    public static final AppStatus getInstance(Context context, String id) {
        if (instance == null)
            synchronized (mLock) {
                instance = new AppStatus(context.getApplicationContext(), id);
            }
        return instance;
    }

    private final Context mContext;
    private final String appId;
    private int versionCode = 1;
    private String versionName = "1.0";

    private AppStatus(final Context context, String appId) {
        if (context == null)
            throw new NullPointerException("context is null");

        this.mContext = context.getApplicationContext();
        this.appId = appId;
    }

    public final String getAppId() {
        return this.appId;
    }

    /**
     * Retrieve the current textual label associated with this item.  This
     * will call back on the given PackageManager to load the label from
     * the application.
     *
     * @return
     */
    public final String getAppName() {
        try {
            return mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0)
                    .applicationInfo.loadLabel(this.mContext.getPackageManager())
                    .toString();
        } catch (Exception var2) {
            return "";
        }
    }

    public final String getPackageName() {
        return mContext.getPackageName();
    }

    /**
     * The version name of this package
     *
     * @return
     */
    public final String getVersionName() {
        try {
            return mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception var2) {
            return versionName;
        }
    }

    /**
     * The version number of this package.
     *
     * @return
     */
    public final int getVersionCode() {
        try {
            return mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception var2) {
            return versionCode;
        }
    }
}
