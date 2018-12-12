package com.sye.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Win10-2015 on 2015/8/19.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();


    /**
     * @param context
     * @param name
     * @param data
     * @param mode
     */
    public static void writeInternalStorage(final Context context, final String name,
                                            final String data, final int mode) {
        if (context == null || isEmpty(name) || isEmpty(data))
            return;
        try {
            FileOutputStream fos = context.openFileOutput(name, mode);
            fos.write(data.getBytes(defaultCharset));
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return;
        }
    }

    /**
     * @param context
     * @param name
     * @return
     */
    public static String readInternalStorage(final Context context, final String name) {
        try {
            if (context == null || isEmpty(name))
                return null;
            FileInputStream fis = context.openFileInput(name);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            return new String(bytes, defaultCharset);
        } catch (Exception e) {
            return null;
        }
    }

    public static String read(File file) throws IOException {
        if (file != null && file.exists()) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                FileReader fileReader = new FileReader(file);
                reader = new BufferedReader(fileReader);

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            } catch (Exception e) {
                throw e;
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (Exception e) {

                    }
            }
        } else
            return null;
    }

    public static void write(final File file, String data) throws IOException {
        if (file == null)
            throw new IOException("Target file can't be null");

        File parent;
        if (!(parent = file.getParentFile()).exists())
            parent.mkdirs();

        FileWriter writer;
        if ((writer = new FileWriter(file)) != null) {
            writer.write(data);
            writer.flush();
            writer.close();
        }
    }

    /**
     * @param name
     * @param data
     * @param append
     */
    public static void writeExternalStorage(final String name, final String data,
                                            final boolean append) {
        try {
            if (!isAvailableExternalStorage() || isEmpty(name) || isEmpty(data))
                return;
            File file = new File(name);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    if (parent.isFile() && !parent.mkdir())
                        return;
                    else if (!parent.mkdirs()) {
                        return;
                    }
                }
            } else {
                if (file.isDirectory() && !file.createNewFile())
                    return;
            }
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes(defaultCharset));
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return;
        }
    }


    /**
     * @param name
     * @return
     */
    public static String readExternalStorage(final String name) {
        try {
            if (!isAvailableExternalStorage() || isEmpty(name))
                return null;
            File file = new File(name);
            if (!file.exists() || file.isDirectory())
                return null;
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            return new String(bytes, defaultCharset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean renameTo(File oldFile, File newFile) {
        if (oldFile != null && newFile != null && oldFile.exists()) {
            return !oldFile.renameTo(newFile) ? copy(oldFile, newFile, true) : true;
        }
        return false;
    }

    public static boolean copy(File from, File to) {
        return copy(from, to, false);
    }

    public static boolean copy(File from, File to, boolean delete) {
        try {
            FileInputStream is = new FileInputStream(from);
            FileOutputStream os = new FileOutputStream(to);
            byte[] bt = new byte[cacheSize];
            int tm = 0;
            while ((tm = is.read(bt)) > 0) {
                os.write(bt, 0, tm);
            }
            os.flush();
            os.close();
            is.close();
            if (delete) {
                try {
                    from.delete();
                } catch (Exception e) {

                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean copy(InputStream stream, File to) {
        if (mkdirs(to)) {
            try {
                FileOutputStream fos = new FileOutputStream(to);
                byte[] buffer = new byte[4096];
                int count;
                while ((count = stream.read(buffer)) > 0)
                    fos.write(buffer, 0, count);
                try {
                    stream.close();
                } catch (Exception e) {

                }
                try {
                    fos.close();
                } catch (Exception e) {

                }
                return true;
            } catch (IOException e) {

            }
        }
        return false;
    }

    private static boolean mkdirs(File file) {
        if (file != null) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                return false;
            return true;
        }
        return false;
    }

    public static void deletes(File... files) {
        for (File file : files) {
            if (file.isFile() && file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {

                }
            }
        }
    }

    public static boolean isAvailableExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
                (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY));
    }


    private static boolean isEmpty(final String arg) {
        return arg == null || arg.length() <= 0;
    }


    private static final String defaultCharset = "utf-8";
    private static final int cacheSize = 1024;

}
