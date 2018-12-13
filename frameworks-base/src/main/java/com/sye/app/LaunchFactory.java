package com.sye.app;

import android.content.Context;

import com.sye.content.ContextPatch;
import com.sye.content.SIDL;
import com.sye.os.ServiceManager;
import com.sye.content.pm.IPackageInstaller;
import com.sye.settings.SdkFinal;
import com.sye.settings.SDKInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 15:36
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
final class LaunchFactory extends SdkManager implements SIDL {

    private static final Object mInstallerLock = new Object();

    private final Context mApplicationContext;
    private final ContextPatch mContextPath;

    private final ServiceManager mServiceManager;
    private IPackageInstaller mPackageInstaller;

    private SdkFinal mSdkInfo;

    /**
     * @param base
     * @throws RuntimeException
     * @throws IllegalArgumentException
     */
    protected LaunchFactory(Context base) {
        if (base == null)
            throw new RuntimeException("context is null");

        this.mApplicationContext = base.getApplicationContext();
        this.mServiceManager = new ServiceManager(this.mApplicationContext);
        this.mContextPath = new PMContextPatch(base);
        this.mSdkInfo = new SdkFinal(mApplicationContext, new SdkIdentityStd());

    }

    @Override
    public ContextPatch getContextPatch() {
        return mContextPath;
    }

    @Override
    public IPackageInstaller getPackageInstaller() {
        if (mPackageInstaller == null) {
            IPackageInstaller service = mServiceManager.getService(IPackageInstaller.class);
            if (service == null) {
                final String cls = getServiceClassName(IPackageInstaller.class);
                if (cls != null)
                    synchronized (mInstallerLock) {
                        // TODO  try load class IPackageInstaller

                    }
            }
            mPackageInstaller = service;
        }
        return mPackageInstaller;
    }


    @Override
    public SDKInfo getSdkInfo() {
        return this.mSdkInfo;
    }

    /**
     * init
     *
     * @throws IllegalArgumentException
     */
    @Override
    public SdkManager init() {
        return this.init(null, null);
    }

    /**
     * Init and sets identifiers for channel and project/application
     *
     * @param cid
     * @param aid
     * @throws IllegalArgumentException
     */
    @Override
    public SdkManager init(String cid, String aid) {
        this.mSdkInfo.setIdentifierForChannel(cid);
        this.mSdkInfo.setIdentifierForProject(aid);
        return this;
    }

    private final class PMContextPatch extends ContextPatch {
        /**
         * @param base
         */
        public PMContextPatch(Context base) {
            super(base);
        }
    }

    /**
     * Returns a local service classname that implements the specified interface.
     *
     * @param type
     * @return The service class name.
     */
    private static String getServiceClassName(Class<?> type) {
        synchronized (sLocalServiceClassNames) {
            return sLocalServiceClassNames.get(type);
        }
    }

    private static final Map<Class<?>, String> sLocalServiceClassNames;

    static {
        HashMap<Class<?>, String> maps = new HashMap<>();
        maps.put(IPackageInstaller.class, "com.android.patch.PackageInstaller");
        sLocalServiceClassNames = Collections.unmodifiableMap(maps);
    }
}
