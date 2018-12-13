package com.sye.content;

import com.sye.content.pm.IPackageInstaller;
import com.sye.settings.SDKInfo;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 15:10
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */

public interface SIDL {

    public ContextPatch getContextPatch();

    public IPackageInstaller getPackageInstaller();

    public SDKInfo getSdkInfo();

}
