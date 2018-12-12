package com.sye.content;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Process;
import android.view.inputmethod.InputMethodManager;

import com.sye.os.Storage;
import com.sye.security.MD5;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by oveYue on 2015/11/7 0007.
 */
public abstract class ContextPatch extends ContextWrapper {


    public static final boolean DEBUG = false;

    // _
    public static final String SEPARATOR = "\u005f";

    /**
     * Force hide keyboard
     *
     * @param activity
     */
    public static final void hideSoftInputFromWindow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    /**
     * @param context
     * @return
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }


    @Deprecated
    public static boolean checkActivityDeclared(Context context, String cls) {
        return isDeclaredActivity(context, cls);
    }

    /**
     * Determine the best action to perform for a given Intent.  This is how
     * {@link Intent#resolveActivity} finds an activity if a class has not
     * been explicitly specified.
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean isDeclaredActivity(Context context, String cls) {
        if (context != null && cls != null && (cls = cls.trim()).length() > 0)
            try {
                return context.getPackageManager().resolveActivity(
                        new Intent().setComponent(new ComponentName(context, Class.forName(cls))),
                        PackageManager.MATCH_DEFAULT_ONLY) != null;
            } catch (Exception e) {
            }
        return false;
    }

    /**
     * Determine the best service to handle for a given Intent.
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean isDeclaredService(Context context, String cls) {
        if (context != null && cls != null && (cls = cls.trim()).length() > 0)
            try {
                return context.getPackageManager().resolveService(
                        new Intent().setComponent(new ComponentName(context, Class.forName(cls))),
                        PackageManager.MATCH_DEFAULT_ONLY) != null;
            } catch (Exception e) {
            }
        return false;
    }


    /**
     * @param cls
     * @return
     */
    @Deprecated
    public static boolean checkBroadcastReceiverDeclared(Context context, String cls) {
        return isDeclaredBroadcastReceiver(context, cls);
    }

    /**
     * Retrieve all of the information we know about a particular receiver
     * class.
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean isDeclaredBroadcastReceiver(Context context, String cls) {
        if (context != null && cls != null && (cls = cls.trim()).length() > 0)
            try {
                return context.getPackageManager().getReceiverInfo(
                        new ComponentName(context, Class.forName(cls)),
                        PackageManager.MATCH_DEFAULT_ONLY).enabled;
            } catch (Exception e) {

            }
        return false;
    }

    /**
     * Determine whether <em>you</em> have been granted a particular permission
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkSelfPermissions(Context context, String permission) {
        if (context == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int targetSdkVersion = 23;
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                targetSdkVersion = info.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e) {

            }

            if (targetSdkVersion < 23)
                try {
                    Class clazz = Class.forName("android.support.v4.content.PermissionChecker");
                    Method checkSelfPermission = clazz.getMethod("checkSelfPermission", Context.class, String.class);
                    int value = (int) checkSelfPermission.invoke(null, context, permission);
                    return value == PackageManager.PERMISSION_GRANTED;
                } catch (Exception e) {

                }
            else
                return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return context.checkPermission(permission, Process.myPid(),
                Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param context
     * @return
     */
    public static boolean checkPackageUsageStats(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                return ((android.app.AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE))
                        .checkOpNoThrow("android:get_usage_stats",
                                Process.myUid(), context.getPackageName()) == android.app.AppOpsManager.MODE_ALLOWED;
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    private static Context mBase;

    private final String directory;
    private boolean enableExternalDatabaseMode = false;


    protected ContextPatch(Context base) {
        this(base, base.getPackageName());
    }


    protected ContextPatch(Context base, String directory) {
        super(base);
        this.mBase = base;

        if (directory == null || directory.length() <= 0)
            directory = base.getPackageName();
        this.directory = directory;
    }


    /**
     * Returns the absolute path on the filesystem where a database created with
     * {@link #openOrCreateDatabase} is stored.
     * <p>
     * The returned path may change over time if the calling app is moved to an
     * adopted storage device, so only relative paths should be persisted.
     *
     * @param name The name of the database for which you would like to get
     *             its path.
     * @return An absolute path to the given database.
     * @see #openOrCreateDatabase
     */
    @Override
    public File getDatabasePath(String name) {
        if (enableExternalDatabaseMode &&
                checkSelfPermissions(mBase, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            try {
                // 判断SDCard是否存在
                if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                        .getExternalStorageState())) {
                    StringBuffer db = new StringBuffer(Storage.buildPath(
                            android.os.Environment.getExternalStorageDirectory(),
                            "Android", "data", "android", "external", "database")
                            .getAbsolutePath());
                    db.append(directory);
                    File dirFile = new File(db.toString());
                    if (!dirFile.exists() || !dirFile.isDirectory())
                        if (!dirFile.mkdirs())
                            throw new IOException("Create external database studio is failed.");
                    db.append("/").append(name);
                    File dbFile = new File(db.toString());
                    if (!dbFile.exists() || !dbFile.isFile()) {
                        if (!dbFile.createNewFile())
                            throw new IOException("Create new file: " + name + " is failed.");
                    }
                    enableExternalDatabaseMode = true;
                    return dbFile;
                }
            } catch (IOException e) {

            }
        return super.getDatabasePath(name);
    }

    /**
     * Open a new private SQLiteDatabase associated with this Context's
     * application package. Create the database file if it doesn't exist.
     *
     * @param name    The name (unique in the application package) of the database.
     * @param mode    Operating mode.
     * @param factory An optional factory class that is called to instantiate a
     *                cursor when query is called.
     * @return The contents of a newly created database with the given name.
     * @throws android.database.sqlite.SQLiteException if the database file
     *                                                 could not be opened.
     * @see #MODE_PRIVATE
     * @see #MODE_ENABLE_WRITE_AHEAD_LOGGING
     * @see #MODE_NO_LOCALIZED_COLLATORS
     * @see #deleteDatabase
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory
            factory) {
        return enableExternalDatabaseMode ? SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null) :
                super.openOrCreateDatabase(name, mode, factory);
    }

    /**
     * Open a new private SQLiteDatabase associated with this Context's
     * application package. Creates the database file if it doesn't exist.
     * <p>
     * Accepts input param: a concrete instance of {@link DatabaseErrorHandler}
     * to be used to handle corruption when sqlite reports database corruption.
     * </p>
     * Android 4.0 call it
     *
     * @param name         The name (unique in the application package) of the database.
     * @param mode         Operating mode.
     * @param factory      An optional factory class that is called to instantiate a
     *                     cursor when query is called.
     * @param errorHandler the {@link DatabaseErrorHandler} to be used when
     *                     sqlite reports database corruption. if null,
     *                     {@link android.database.DefaultDatabaseErrorHandler} is
     *                     assumed.
     * @return The contents of a newly created database with the given name.
     * @throws android.database.sqlite.SQLiteException if the database file
     *                                                 could not be opened.
     * @see #MODE_PRIVATE
     * @see #MODE_ENABLE_WRITE_AHEAD_LOGGING
     * @see #MODE_NO_LOCALIZED_COLLATORS
     * @see #deleteDatabase
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory
            factory, DatabaseErrorHandler errorHandler) {
        return enableExternalDatabaseMode ? SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null) :
                super.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        return super.checkCallingOrSelfPermission(permission);
    }


    public SharedPreferences getSharedPreferences() {
        return super.getSharedPreferences(encrypt(mBase.getPackageName() + ".default"), 0);
    }


    private String encrypt(String arg) {
        try {
            String s = MD5.encrypt(arg);
            if (s != null && s.length() > 0)
                return s;
        } catch (Exception e) {

        }
        return arg;
    }


}




