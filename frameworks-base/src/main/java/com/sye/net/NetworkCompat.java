package com.sye.net;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;


/**
 * Created by man on 2015/3/17.
 */
public class NetworkCompat {

    /**
     * Unknown network class.
     * {@hide}
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    /**
     * Class of broadly defined "2G" networks.
     * {@hide}
     */
    public static final int NETWORK_TYPE_2_G = 2;
    /**
     * Class of broadly defined "3G" networks.
     * {@hide}
     */
    public static final int NETWORK_TYPE_3_G = 3;
    /**
     * Class of broadly defined "4G" networks.
     * {@hide}
     */
    public static final int NETWORK_TYPE_4_G = 4;

    public static final int NETWORK_TYPE_5_G = 5;

    private static boolean isDual = false;


    /**
     * Indicates whether network connectivity exists and it is possible to establish
     * connections and pass data.
     * <p>
     * Always call this before attempting to perform data transactions.
     *
     * @param context
     * @return {@code true} if network connectivity exists, {@code false} otherwise
     */
    public synchronized static boolean isConnected(Context context) {
        final NetworkInfo info = getActiveNetworkInfo(context);
        return info != null ? info.isConnected() : false;
    }

    /**
     * Reports the type of network
     *
     * @param context
     * @return It may be one of the constants associated with the
     * {@link #NETWORK_TYPE_2_G}, {@link #NETWORK_TYPE_3_G},
     * {@link #NETWORK_TYPE_4_G},{@link #NETWORK_TYPE_WIFI} or
     * {@link #NETWORK_TYPE_UNKNOWN}
     */
    public static int getNetworkType(Context context) {
        final NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo != null && (networkInfo.isAvailable() || networkInfo.isConnected())) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NETWORK_TYPE_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_LTE:/* 4G 100mbps */
                            return NETWORK_TYPE_4_G;
                        case TelephonyManager.NETWORK_TYPE_EHRPD:/* 3G 1-2 mbps */
                        case TelephonyManager.NETWORK_TYPE_HSPA:/* 3G 700-1700kbps */
                        case TelephonyManager.NETWORK_TYPE_HSPAP:/* 3G 10-20mbps */
                        case TelephonyManager.NETWORK_TYPE_HSUPA:/* 3.5G 1-23mbps */
                        case TelephonyManager.NETWORK_TYPE_HSDPA:/* 联通 3.5G 2-14mbps */
                        case TelephonyManager.NETWORK_TYPE_UMTS:/* 联通 3G 400-7000kbps */
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:/* 电信 3G 400-1000kbps */
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:/* 电信 3G 600-1400kbps */
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:/* 电信 3G 5mbps */
                            return NETWORK_TYPE_3_G;
                        case TelephonyManager.NETWORK_TYPE_IDEN:/* 2G 25kbps */
                        case TelephonyManager.NETWORK_TYPE_1xRTT: /* 2G 50-100kbps */
                        case TelephonyManager.NETWORK_TYPE_EDGE:/* 移动 2.75G 50-100kbps */
                        case TelephonyManager.NETWORK_TYPE_GPRS:/* 联通 2.5G 100kbps */
                        case TelephonyManager.NETWORK_TYPE_CDMA:/* 电信 2G 14-64kbps */
                            return NETWORK_TYPE_2_G;
                        default:
                            return NETWORK_TYPE_UNKNOWN;
                    }
                case ConnectivityManager.TYPE_MOBILE_DUN:
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                default:
                    return NETWORK_TYPE_UNKNOWN;
            }
        }
        return NETWORK_TYPE_UNKNOWN;
    }

    /**
     * Reports the type of network
     *
     * @param context
     * @return
     */
    public synchronized static String getNetworkTypeString(Context context) {
        final NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && (info.isAvailable() || info.isConnected())) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return "WIFI";
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    return "DUN";
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    return "SUPL";
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_1xRTT: /* 2G 50-100kbps */
                            return "1xRTT";
                        case TelephonyManager.NETWORK_TYPE_CDMA:/* 电信2G 14-64kbps */
                            return "CDMA";
                        case TelephonyManager.NETWORK_TYPE_EDGE:/* 移动2G 50-100kbps */
                            return "EDGE";
                        case TelephonyManager.NETWORK_TYPE_EHRPD:/* 3G 1-2 mbps */
                            return "EHRPD";
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:/* 电信3G 400-1000kbps */
                            return "EVDO_0";
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:/* 电信3G 600-1400kbps */
                            return "EVDO_A";
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:/* 电信3G 5mbps */
                            return "EVDO_B";
                        case TelephonyManager.NETWORK_TYPE_GPRS:/* 联通2G 100kbps */
                            return "GPRS";
                        case TelephonyManager.NETWORK_TYPE_HSDPA:/* 联通3G 2-14mbps */
                            return "HSDPA";
                        case TelephonyManager.NETWORK_TYPE_HSPA:/* 3G 700-1700kbps */
                            return "HSPA";
                        case TelephonyManager.NETWORK_TYPE_HSUPA:/* 3G 1-23mbps */
                            return "HSUPA";
                        case TelephonyManager.NETWORK_TYPE_HSPAP:/* 3G 10-20mbps */
                            return "HSPAP";
                        case TelephonyManager.NETWORK_TYPE_IDEN:/* 2G 25kbps */
                            return "IDEN";
                        case TelephonyManager.NETWORK_TYPE_LTE:/* 4G 100mbps */
                            return "LTE";
                        case TelephonyManager.NETWORK_TYPE_UMTS:/* 联通3G 400-7000kbps */
                            return "UMTS";
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return "UNKNOWN";
                    }
            }
        }
        return null;
    }


    public static String getMediaAccessControl() {
        try {
            String macSerial = "";
            try {
                Process pp = Runtime.getRuntime().exec(
                        "cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                for (String str = ""; null != str; ) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();// 去空格
                        break;
                    }
                }
            } catch (Exception ex) {

            }

            if (macSerial != null && macSerial.length() > 0)
                return macSerial;

            return readAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (Exception e) {

        }
        return null;
    }

    private static String readAsString(String file) throws Exception {
        final FileReader reader = new FileReader(file);
        final StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int len;
        while ((len = reader.read(buffer)) >= 0) {
            builder.append(buffer, 0, len);
        }
        reader.close();
        return builder.toString();
    }

    @SuppressLint("MissingPermission")
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED)
                return null;
        } else if (context.getPackageManager().checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName())
                == PackageManager.PERMISSION_DENIED)
            return null;

        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

}
