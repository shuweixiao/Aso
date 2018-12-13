package com.sye.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * *****************************************************************************************
 * Created by super.dragon on 7/19/2018 16:28
 * <p>
 * Advertising show connection channel
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public interface IACC {

    public void onAttach(Activity input);

    public void onCreate(Bundle savedInstanceState);

    public void onCreated(Bundle savedInstanceState);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onRestart();

    public void onDestroy();

    public void onConfigurationChanged(Configuration newConfig);

    public void onAttachedToWindow();

    public void onDetachedFromWindow();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onBackPressed();

    public boolean onKeyDown(int keyCode, KeyEvent event);

    public boolean dispatchKeyEvent(KeyEvent event);

    public boolean dispatchTouchEvent(MotionEvent ev);

}
