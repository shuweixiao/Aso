package com.sye.security.action;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * *****************************************************************************************
 * Created by super.dragon on 5/24/2018 11:10
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class GetMetadataAction implements PrivilegedAction {

    private final Context theContext;
    private final String theName;

    public GetMetadataAction(Context theContext, String theName) {
        this.theContext = theContext;
        this.theName = theName;
    }

    @Override
    public Object run() {
        try {
            final ApplicationInfo ai = theContext.getPackageManager()
                    .getApplicationInfo(theContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (ai != null && ai.metaData != null && ai.metaData.containsKey(theName))
                return ai.metaData.get(theName);
        } catch (Exception e) {

        }
        return null;
    }
}
