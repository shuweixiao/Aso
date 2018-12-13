package com.sye.content;

import android.content.Context;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 13:44
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
class PluginWrapper {

    public interface PluginLifecycleCallbacks {

        void onPluginCreated(Context context);

        void onPluginStarted(Context context);

        void onPluginResumed(Context context);

        void onPluginPaused(Context context);

        void onPluginStopped(Context context);

        void onPluginDestroyed(Context context);

    }
}
