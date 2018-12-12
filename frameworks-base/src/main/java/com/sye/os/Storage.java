package com.sye.os;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by Win10-2015 on 2015/8/25.
 */
public class Storage {

    private static String iRootDir;
    private static String iCRootDir;
    private static String eRootDir;
    private static String eCRootDir;

    public static final String DIRECTORY_CACHE = "cache";
    public static final String DIRECTORY_DOWNLOAD = "download";
    public static final String DIRECTORY_MOVIES = "movies";
    public static final String DIRECTORY_MUSIC = "music";
    public static final String DIRECTORY_PICTURES = "pictures";

    /**
     * {@link Storage#DIRECTORY_CACHE},
     * {@link Storage#DIRECTORY_DOWNLOAD},
     * {@link Storage#DIRECTORY_MOVIES},
     * {@link Storage#DIRECTORY_MUSIC},
     * {@link Storage#DIRECTORY_PICTURES}
     */
    public static String getInternalFilesDir(Context context, final String name) {
        if (context == null)
            throw new NullPointerException("Context is empty.");

        if (name == null || name.length() <= 0)
            return getDirs(context, null);
        if (name.equalsIgnoreCase(DIRECTORY_CACHE)) {
            return getDirs(context, DIRECTORY_CACHE);
        } else if (name.equalsIgnoreCase(DIRECTORY_MUSIC)) {
            return getDirs(context, DIRECTORY_MUSIC);
        } else if (name.equalsIgnoreCase(DIRECTORY_DOWNLOAD)) {
            return getDirs(context, DIRECTORY_DOWNLOAD);
        } else if (name.equalsIgnoreCase(DIRECTORY_MOVIES)) {
            return getDirs(context, DIRECTORY_MOVIES);
        } else if (name.equalsIgnoreCase(DIRECTORY_PICTURES)) {
            return getDirs(context, DIRECTORY_PICTURES);
        } else {
            return getDirs(context, null);
        }
    }

    /**
     * @param name
     * @return
     */
    public static String getExternalFilesDir(String name) {
        if (name == null || name.length() <= 0)
            return getDirs(null);
        if (name.equalsIgnoreCase(DIRECTORY_CACHE)) {
            return getDirs(DIRECTORY_CACHE);
        } else if (name.equalsIgnoreCase(DIRECTORY_MUSIC)) {
            return getDirs(DIRECTORY_MUSIC);
        } else if (name.equalsIgnoreCase(DIRECTORY_DOWNLOAD)) {
            return getDirs(DIRECTORY_DOWNLOAD);
        } else if (name.equalsIgnoreCase(DIRECTORY_MOVIES)) {
            return getDirs(DIRECTORY_MOVIES);
        } else if (name.equalsIgnoreCase(DIRECTORY_PICTURES)) {
            return getDirs(DIRECTORY_PICTURES);
        } else {
            return getDirs(null);
        }
    }


    /**
     * create by super.dragon on 2016/1/15 0015 15:53, email dragon.eros@outlook.com
     * <p>
     * get root directory from internal memory
     *
     * @param context
     * @return
     */
    public static String getInternalRootDir(Context context) {
        if (iRootDir == null)
            iRootDir = context.getFilesDir().getAbsolutePath();

        return iRootDir;
    }

    /**
     * create by super.dragon on 2016/1/15 0015 15:54, email dragon.eros@outlook.com
     * <p>
     * get root directory or external memory
     *
     * @return
     */
    public static String getExternalRootDir() {
        if (eRootDir == null)
            eRootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return eRootDir;
    }


    /**
     * @param name
     * @return
     */
    private static String getDirs(Context context, String name) {
        if (iCRootDir == null)
            iCRootDir = getInternalRootDir(context) + "/user";
        if (name == null)
            return iCRootDir;

        File dir = new File(iCRootDir, name);
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        return dir.getAbsolutePath();
    }


    /**
     * create by super.dragon on 2016/1/15 0015 15:49, email dragon.eros@outlook.com
     * Gets the default path to the external memory
     *
     * @param name
     * @return
     */
    private static String getDirs(String name) {
        try {
            if (!isAvailableExternalStorage())
                throw new IOException("External storage devices available to the Department.");

            if (eCRootDir == null) {
                File base = new File(getExternalRootDir());
                eCRootDir = buildPath(base, "Android", "data", "android",
                        "user_" + Build.BRAND).getAbsolutePath();
            }
            if (name == null)
                return eCRootDir;
            File dir = new File(eCRootDir, name);
            if (!dir.exists() || !dir.isDirectory())
                dir.mkdirs();
            return dir.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param name
     * @return
     */
    public static String buildRootDir(String name) {
        return (matchCharacter("^/mmt/.*$", name) || matchCharacter("^/storage/.*$",
                name)) ? name : (getExternalFilesDir(null) + "/" + name);
    }


    public static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                cur = new File(segment);
            } else {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isAvailableExternalStorage() {
        return Environment.getExternalStorageState().equals("mounted") &&
                !Environment.getExternalStorageState().equals("mounted_ro");
    }

    /**
     * create by super.dragon on 2016/1/15 0015 16:27, email dragon.eros@outlook.com
     *
     * @param regex
     * @param params
     * @return
     */
    private static boolean matchCharacter(String regex, String params) {
        return Pattern.compile(regex).matcher(params.trim()).matches();
    }
}
