package com.sye.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Field;

/**
 * Created by oveYue on 2015/11/17 0017.
 */
public class PhoneUtils {

    private static int statusBarHeight = -1;
    private static int tablet = -1;
    private static String mac;

    /**
     * create by super.dragon on 2016/1/21 0021 11:24, email dragon.eros@outlook.com
     * <p/>
     * Gets phone display state bar height
     *
     * @param context
     * @return
     */
    public static int getStateBarHeight(Context context) {
        try {
            if (statusBarHeight <= 0) {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                statusBarHeight = context.getResources().getDimensionPixelSize((Integer) field.get(o));
            }
            return statusBarHeight;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * create by super.dragon on 2016/1/21 0021 11:28, email dragon.eros@outlook.com
     * <p/>
     * Gets Display size
     *
     * @param context
     * @param out
     * @return
     */
    public static int[] getDisplaySize(Context context, final int[] out) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Point point = new Point();
                wm.getDefaultDisplay().getRealSize(point);
                out[0] = point.x;
                out[1] = point.y;
                return out;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point point = new Point();
                wm.getDefaultDisplay().getSize(point);
                out[0] = point.x;
                out[1] = point.y;
                return out;
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                out[0] = dm.widthPixels;
                out[1] = dm.heightPixels;
            }
            return out;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        out[0] = dm.widthPixels;
        out[1] = dm.heightPixels;
        return out;
    }


    /**
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        if (tablet < 0) {
            tablet = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE ? 1 : 0;
        }
        return tablet == 1;
    }

    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸大于6尺寸则为Pad
        return Math.sqrt(x + y) >= 6.0;
    }

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac() {
        if (mac == null) {
            String str = "";
            String macSerial = "";
            try {
                Process pp = Runtime.getRuntime().exec(
                        "cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                for (; null != str; ) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();// 去空格
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (macSerial == null || "".equals(macSerial)) {
                try {
                    return loadFileAsString("/sys/class/net/eth0/address")
                            .toUpperCase().substring(0, 17);
                } catch (Exception e) {
                }
            }
            return mac= macSerial;
        }
        return mac;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

}
