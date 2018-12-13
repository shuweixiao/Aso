package com.sye.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * *****************************************************************************************
 * Created by super.dragon on 7/21/2018 09:32
 * <p>
 * Service connection channel
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public interface ISCC {

    public void attach(Context context);

    public void onCreate();

    public void onStart(Intent intent, int startId);

    public int onStartCommand(Intent intent, int flags, int startId);

    public void onDestroy();

    public void onConfigurationChanged(Configuration newConfig);

    public void onLowMemory();

    public void onTrimMemory(int level);

    public boolean onUnbind(Intent intent);

    public void onRebind(Intent intent);

    public void onTaskRemoved(Intent rootIntent);

    public IBinder onBind(Intent intent);
}
