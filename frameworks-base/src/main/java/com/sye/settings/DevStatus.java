package com.sye.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sye.content.ContextPatch;
import com.sye.net.NetworkCompat;
import com.sye.security.action.GetSystemPropertyAction;
import com.sye.security.action.SetSystemPropertyAction;
import com.sye.util.PhoneUtils;
import com.sye.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * *****************************************************************************************
 * Created by super.dragon on 7/20/2018 11:17
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class DevStatus {

    private static final Object mLock = new Object();
    private static volatile DevStatus instance;

    public static final DevStatus getInstance(Context context) {
        synchronized (mLock) {
            if (instance == null)
                instance = new DevStatus(context.getApplicationContext());
            return instance;
        }
    }


    public static final String getInternetProtocol() {
        String value;
        return StringUtils.nonEmpty(value = new GetSystemPropertyAction(
                "internal.config.internet_protocol").run()) ? value : "";
    }

    public final static void setInternetProtocol(String value) {
        if (value != null && (value = value.trim()).length() > 0) {
            new SetSystemPropertyAction("internal.config.internet_protocol", value).run();
        }
    }

    private final Context mAppContext;

    public final String model;
    public final String brand;
    public final String release;
    public final String manufacturer;

    private final float density;
    private final int densityDpi;
    private final int[] displayPixels = new int[2];

    private String androidId;
    private String deviceId;
    private String country;
    private String language;
    private String mediaAccessControl;
    private String subscriberId;
    private String phoneNumber;
    private String simOperator;
    private String mUserAgent;

    private volatile double locationLatitude;
    private volatile double locationLongitude;
    private volatile float locationAccuracy;

    private DevStatus(Context context) {
        this.mAppContext = context.getApplicationContext();

        this.model = Build.MODEL;
        this.brand = Build.BRAND;
        this.release = Build.VERSION.RELEASE;
        this.manufacturer = Build.MANUFACTURER;

        PhoneUtils.getDisplaySize(context, displayPixels);

        final DisplayMetrics metrics = this.mAppContext.getResources().getDisplayMetrics();
        this.density = metrics.density;
        this.densityDpi = getVersion() > 3 ? metrics.densityDpi : 120;
        this.initLocation();
    }


    /**
     * Gets the WebView's user-agent string.
     *
     * @return
     */
    public String getUserAgent() {
        if (mUserAgent == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return mUserAgent = new WebView(mAppContext).getSettings().getUserAgentString();
            }
            return mUserAgent = WebSettings.getDefaultUserAgent(mAppContext);
        }
        return mUserAgent;
    }

    public int getVersion() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception var1) {
            return 3;
        }
    }

    public float getDensity() {
        return this.density;
    }

    public int getDensityDpi() {
        return this.densityDpi;
    }

    public int getDisplayWidth() {
        return this.displayPixels[0];
    }

    public int getDisplayHeight() {
        return this.displayPixels[1];
    }

    /**
     * @return The value of {@link android.content.res.Configuration#ORIENTATION_PORTRAIT} or
     * {@link android.content.res.Configuration#ORIENTATION_LANDSCAPE}
     */
    public int getScreenOrientation() {
        return mAppContext.getResources().getConfiguration().orientation;
    }

    public double getLocationLatitude() {
        return this.locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public float getLocationAccuracy() {
        return locationAccuracy;
    }

    public String getAndroidId() {
        try {
            return android.provider.Settings.Secure.getString(
                    this.mAppContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     *
     * @return
     */
    public String getDeviceId() {
        return getDeviceId(null);
    }

    /**
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     *
     * @param defVal
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getDeviceId(String defVal) {
        try {
            if (ContextPatch.checkSelfPermissions(mAppContext, Manifest.permission.READ_PHONE_STATE)) {
                return ((TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }
        } catch (Exception e) {

        }
        return defVal;
    }

    /**
     * Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
     * Return null if it is unavailable
     *
     * @return
     */
    public String getSubscriberId() {
        return getSubscriberId(null);
    }

    /**
     * Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
     * Return null if it is unavailable
     *
     * @param defVal
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getSubscriberId(String defVal) {
        try {
            if (ContextPatch.checkSelfPermissions(mAppContext, Manifest.permission.READ_PHONE_STATE))
                return ((TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
        } catch (Exception e) {

        }
        return defVal;
    }

    /**
     * Returns the phone number string for line 1, for example, the MSISDN
     * for a GSM phone. Return null if it is unavailable
     *
     * @return
     */
    public String getPhoneNumber() {
        return getPhoneNumber(null);
    }

    /**
     * Returns the phone number string for line 1, for example, the MSISDN
     * for a GSM phone. Return null if it is unavailable
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getPhoneNumber(String defVal) {
        try {
            if (ContextPatch.checkSelfPermissions(mAppContext, Manifest.permission.READ_SMS) ||
                    ContextPatch.checkSelfPermissions(mAppContext, Manifest.permission.READ_PHONE_STATE)) {
                return ((TelephonyManager) mAppContext
                        .getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
            }
        } catch (Exception e) {

        }
        return defVal;
    }

    /**
     * Returns the MCC+MNC (mobile country code + mobile network code) of the
     * provider of the SIM. 5 or 6 decimal digits.
     *
     * @return
     */
    public String getSimOperator() {
        return getSimOperator(null);
    }

    /**
     * Returns the MCC+MNC (mobile country code + mobile network code) of the
     * provider of the SIM. 5 or 6 decimal digits.
     *
     * @param defVal
     * @return
     */
    public String getSimOperator(String defVal) {
        try {
            return ((TelephonyManager) mAppContext
                    .getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
        } catch (Exception e) {

        }
        return defVal;
    }

    /**
     * Returns the numeric name (MCC+MNC) of current registered operator.
     *
     * @return
     */
    public String getNetworkOperator() {
        return getNetworkOperator(null);
    }

    /**
     * Returns the numeric name (MCC+MNC) of current registered operator.
     *
     * @param defVal
     * @return
     */
    public String getNetworkOperator(String defVal) {
        try {
            return ((TelephonyManager) this.mAppContext
                    .getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
        } catch (Exception var2) {

        }
        return defVal;
    }

    public int getNetworkType() {
        try {
            return NetworkCompat.getNetworkType(mAppContext);
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * Returns the MAC address of the WLAN interface
     *
     * @return
     */
    public String getMediaAccessControl() {
        return StringUtils.isEmpty(mediaAccessControl) ?
                mediaAccessControl = getMediaAccessControl0() : mediaAccessControl;
    }


    /**
     * Returns the ISO country code equivalent for the SIM provider's country code.
     * Or returns the country code for this locale, or {@code ""} if this locale
     * doesn't correspond to a specific country.
     *
     * @return
     */
    public String getCountry() {
        if (StringUtils.isEmpty(country))
            try {
                final TelephonyManager manager = (TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
                if (StringUtils.nonEmpty(country = manager.getSimCountryIso())) {
                    return country = country.toUpperCase();
                } else if (StringUtils.nonEmpty(country = Locale.getDefault().getCountry())) {
                    return country = country.toUpperCase();
                }
            } catch (Exception e) {

            }
        return country;
    }

    /**
     * Returns the language code for this {@code Locale} or the empty string if no language
     * was set.
     *
     * @return
     */
    public String getLanguage() {
        return Locale.getDefault().getLanguage().toLowerCase();
    }

    @SuppressLint("MissingPermission")
    public Map<String, String> getLacAndCeilId() {
        try {
            final TelephonyManager tm = (TelephonyManager)
                    this.mAppContext.getSystemService(Context.TELEPHONY_SERVICE);

            final String operator = tm.getSimOperator();
            if (operator != null && operator.length() >= 5) {
                final HashMap<String, String> hashMap = new HashMap();
                int mcc = Integer.parseInt(operator.substring(0, 3));
                int mnc = Integer.parseInt(operator.substring(3));
                if (mcc == 460) {
                    mcc = 0;
                    int t = 0;
                    if (mnc != 3 && mnc != 5) {
                        GsmCellLocation gsmc;
                        if ((gsmc = (GsmCellLocation) tm.getCellLocation()) != null) {
                            mcc = gsmc.getLac();
                            t = gsmc.getCid();
                        }
                    } else {
                        CdmaCellLocation cdmac;
                        mcc = (cdmac = (CdmaCellLocation) tm.getCellLocation()).getNetworkId();
                        t = cdmac.getBaseStationId();
                    }

                    hashMap.put("lac", "" + mcc);
                    hashMap.put("cellid", "" + t);
                }

                return hashMap;
            }
        } catch (Throwable t) {

        }
        return null;
    }


    @SuppressLint("MissingPermission")
    private void initLocation() {
        try {
            final LocationManager lm;
            if ((lm = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE)) != null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(2);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(false);
                criteria.setPowerRequirement(1);

                try {
                    String provider = lm.getBestProvider(criteria, true);
                    Location location;

                    if ((location = lm.getLastKnownLocation(provider)) != null) {
                        this.locationLatitude = location.getLatitude();
                        this.locationLongitude = location.getLongitude();
                        this.locationAccuracy = location.getAccuracy();
                    } else {
                        lm.requestLocationUpdates(provider, 2000l, 7000f, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                try {
                                    locationLatitude = location.getLatitude();
                                    locationLongitude = location.getLongitude();
                                    locationAccuracy = location.getAccuracy();
                                    lm.removeUpdates(this);
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

    }

    private int count(float density, int pixels) {
        return (mAppContext.getApplicationInfo().flags & 8192) != 0 ? (int) ((float) pixels / density) : pixels;
    }

    @SuppressLint("MissingPermission")
    private String getMediaAccessControl0() {
        // By android system api
        try {
            if (ContextPatch.checkSelfPermissions(mAppContext, Manifest.permission.ACCESS_WIFI_STATE)) {
                return ((WifiManager) mAppContext.getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo().getMacAddress();
            }
        } catch (Exception e) {

        }

        // By java api
        String macSerial;
        try {
            if ((macSerial = getMacByJavaAPI()) != null && macSerial.contains(":"))
                return macSerial;
        } catch (Exception e) {

        }

        // By system config file
        return NetworkCompat.getMediaAccessControl();
    }

    @TargetApi(9)
    private static String getMacByJavaAPI() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();
                if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
                    byte[] addr = netInterface.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        return null;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString().toLowerCase(Locale.getDefault());
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    private static void hookWebView() {
        if (android.os.Process.myUid() == android.os.Process.SYSTEM_UID || android.os.Process.myUid() == 0) {
            try {
                Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
                Field field = factoryClass.getDeclaredField("sProviderInstance");
                field.setAccessible(true);
                Object sProviderInstance = field.get(null);
                if (sProviderInstance != null)
                    return;

                Method methodGetProviderClass;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    methodGetProviderClass = factoryClass.getDeclaredMethod("getProviderClass");
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                    methodGetProviderClass = factoryClass.getDeclaredMethod("getFactoryClass");
                } else {
                    return;
                }

                methodGetProviderClass.setAccessible(true);
                Class<?> providerClass = (Class<?>) methodGetProviderClass.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                    field.set("sProviderInstance", sProviderInstance);
                }
            } catch (Exception e) {

            }
        }
    }

    static {
        hookWebView();
    }
}
