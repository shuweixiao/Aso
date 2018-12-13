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
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Process;
import android.view.inputmethod.InputMethodManager;

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

    /**
     * startActivityForResult()
     * in order to start screen capture. The activity will prompt
     * the user whether to allow screen capture.  The result of this
     * activity should be passed to getMediaProjection.
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static MediaProjectionManager startScreenCapturePrompt(final Activity activity, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final MediaProjectionManager manager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            // "com.android.systemui" "com.android.systemui.media.MediaProjectionPermissionActivity"
            activity.startActivityForResult(manager.createScreenCaptureIntent(), requestCode);
            return manager;
        }
        return null;
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
    public static boolean checkActivityDeclared(Context context, String cls) {
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
    public static boolean checkServiceDeclared(Context context, String cls) {
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
     * Retrieve all of the information we know about a particular receiver
     * class.
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean checkBroadcastReceiverDeclared(Context context, String cls) {
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
        if (context == null || permission == null || permission.isEmpty())
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
    private final File mExternalStorage;
    private boolean enableExternalDatabaseMode = false;

    /**
     * @param base
     */
    public ContextPatch(Context base) {
        super(base);
        if (base == null)
            throw new RuntimeException("context is null");

        this.mBase = base;
        this.mExternalStorage = buildExternalStorage();
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
                if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
                    final String databases = getExternalStorageDirectory("databases").getAbsolutePath();
                    final File dbFile = new File(databases, name);
                    final File dirFile = dbFile.getParentFile();
                    if (!dirFile.exists() || !dirFile.isDirectory())
                        if (!dirFile.mkdirs())
                            throw new IOException("Create external database studio is failed.");
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
        return enableExternalDatabaseMode ? SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory) :
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


    /**
     * Retrieve and hold the contents of the preferences file 'name', returning
     * a SharedPreferences through which you can retrieve and modify its
     * values.  Only one instance of the SharedPreferences object is returned
     * to any callers for the same name, meaning they will see each other's
     * edits as soon as they are made.
     *
     * @return
     */
    public SharedPreferences getSharedPreferences() {
        return super.getSharedPreferences(encrypt(mBase.getPackageName() + ".default"), 0);
    }

    private File getExternalStorageDirectory(String type) {
        return new File(mExternalStorage, type);
    }

    private File buildExternalStorage() {
        return buildPath(android.os.Environment.getExternalStorageDirectory(),
                "Android", ".SystemConfig", ".data", ".data", mBase.getPackageName());
    }

    public static final File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments)
            cur = cur == null ? new File(segment) : new File(cur, segment);

        return cur;
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




