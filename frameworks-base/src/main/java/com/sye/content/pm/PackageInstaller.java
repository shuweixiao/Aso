package com.sye.content.pm;

import android.content.Context;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 14:58
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class PackageInstaller implements IPackageInstaller {

    private final Context context;


    public PackageInstaller(Context context) {
        if (context == null)
            throw new RuntimeException("context is null");

        this.context = context;
    }

    @Override
    public final void start(Task task) {


    }
}
