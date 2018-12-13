package com.sye.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.sye.os.Build;
import com.sye.security.BASE64;
import com.sye.security.MD5;
import com.sye.security.action.GetMetadataAction;
import com.sye.security.action.GetSystemPropertyAction;
import com.sye.util.FileUtils;
import com.sye.util.StringUtils;

import java.io.File;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/12/2018 15:27
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class SdkFinal extends SDKInfo {


    public static final String getVersionName() {
        return Build.VERSION_NAME.substring(0, Build.VERSION_NAME.lastIndexOf("."));
    }

    public static final int getVersionCode() {
        return Build.VERSION_CODE;
    }


    public static final String format() {
        return Build.format();
    }

    private final Context mContext;
    private final IdentityStd mIdentityStd;

    private String identifierForChannel, identifierForProject, identifierForDevice;

    /**
     * @param context
     * @param idStd
     * @throws IllegalArgumentException
     * @throws RuntimeException
     */
    public SdkFinal(Context context, final IdentityStd idStd) {
        if (context == null)
            throw new RuntimeException("context is null");

        this.mContext = context;
        this.mIdentityStd = idStd;

        this.initIdentifiers();
    }

    /**
     * Gets identifier for channel
     *
     * @return
     */
    @Override
    public String getIFC() {
        return identifierForChannel;
    }

    /**
     * Gets identifier for project
     *
     * @return
     */
    @Override
    public String getIFP() {
        return identifierForProject;
    }

    /**
     * Gets identifier for device/platform/phone
     *
     * @return
     */
    @Override
    public String getIFD() {
        return identifierForDevice;
    }

    @Override
    public String getVersion() {
        return Build.VERSION_NAME;
    }

    @Override
    public int getVersionInt() {
        return Build.VERSION_CODE;
    }

    @Override
    public int getSupported() {
        return Build.SUPPORTED;
    }

    /**
     * @param id
     * @throws IllegalArgumentException
     */
    public void setIdentifierForChannel(String id) {
        if (identifierForChannel == null)
            if (mIdentityStd != null) {
                if (!mIdentityStd.isLegal1(id))
                    this.identifierForChannel = id;
                else
                    throwIdentifierError("id(C) is illegal");
            } else if (StringUtils.nonEmpty(id) && (id = id.trim()).length() > 0) {
                this.identifierForChannel = id;
            } else
                throwIdentifierError("id(C) is empty");
    }

    /**
     * @param id
     * @throws IllegalArgumentException
     */
    public void setIdentifierForProject(String id) {
        if (identifierForProject == null)
            if (mIdentityStd != null) {
                if (!mIdentityStd.isLegal2(id))
                    this.identifierForProject = id;
                else
                    throwIdentifierError("id(P) is illegal");
            } else if (StringUtils.nonEmpty(id) && (id = id.trim()).length() > 0) {
                this.identifierForProject = id;
            } else
                throwIdentifierError("id(P) is empty");
    }

    /**
     * Sets device id
     *
     * @param id
     */
    public final void setIdentifierForPhone(String id) {
        this.setIdentifierForDevice(id);
    }

    /**
     * @param id
     * @hide
     */
    public final void setIdentifierForDevice(String id) {
        if (identifierForDevice == null) {
            if (id != null && (id = id.trim()).length() > 0)
                try {
                    new DeviceSettings(this.mContext).setId(id);
                } catch (Exception e) {

                }
        }
    }

    private void initIdentifiers() {
        if (StringUtils.isEmpty(identifierForChannel)) {
            identifierForChannel = initChannelId();
            if (identifierForChannel != null && mIdentityStd != null) {
                if (!mIdentityStd.isLegal1(identifierForChannel))
                    throwIdentifierError("id(C) is illegal");
            }
        }
        if (StringUtils.isEmpty(identifierForProject)) {
            identifierForProject = initApplicationKey();
            if (identifierForProject != null && mIdentityStd != null) {
                if (!mIdentityStd.isLegal2(identifierForProject))
                    throwIdentifierError("id(P) is illegal");
            }
        }

        if (StringUtils.isEmpty(identifierForDevice)) {
            try {
                this.identifierForDevice = new DeviceSettings(this.mContext).getId();
            } catch (Exception e) {

            }
        }
    }

    private String initChannelId() {
        final String meta = getMetaData(KEYS_MATE_CHANNEL);
        if (StringUtils.isEmpty(meta))
            return new GetSystemPropertyAction(SYSTEM_PROPERTY_CHANNEL).run();
        return meta;
    }

    private String initApplicationKey() {
        final String meta = getMetaData(KEYS_MATE_APPID);
        if (StringUtils.isEmpty(meta))
            return new GetSystemPropertyAction(SYSTEM_PROPERTY_APPLICATION_KEY).run();
        return meta;
    }

    private String getMetaData(String... names) {

        final Object o = getMetaData(mContext, names);
        if (o != null) {
            if (o instanceof String)
                return (String) o;
            return o.toString();
        }
        return "";
    }

    /**
     * @param msg
     * @throws IllegalArgumentException
     */
    private void throwIdentifierError(String msg) {
        throw new IllegalArgumentException(msg);
    }

    private static Object getMetaData(Context context, String... names) {
        if (context != null && names != null && names.length > 0) {
            Object obj;
            for (String name : names) {
                if ((obj = new GetMetadataAction(context, name).run()) != null)
                    return obj;
            }
        }
        return null;
    }


    final static class DeviceSettings {

        private static final String SCHEME = "devset";
        private static final String KEY_ID;
        private static String identifier;

        private final Context context;
        private final char separator = ';';
        /**
         * Storage name of external
         */
        private final File mStorageExt;
        private final String mStorageInt;
        private SharedPreferences preferences;


        public DeviceSettings(Context context) {
            this(context, null, null);
        }


        /**
         * @param context
         * @param intName internal storage name
         * @param extName external storage name
         */
        public DeviceSettings(Context context, String intName, String extName) {
            if (context == null)
                throw new RuntimeException("Context is null");

            this.context = context;
            this.mStorageInt = StringUtils.nonEmpty(intName) ? intName :
                    encryptIfNullDefVal(context.getPackageName() + "_" + SCHEME + "_base",
                            context.getPackageName().replaceAll(".", "") + SCHEME + "abcbase");
            this.mStorageExt = new File(FileUtils.buildPath(Environment.getExternalStorageDirectory(),
                    DIRECTORIES), StringUtils.nonEmpty(extName) ? extName : encryptIfNullDefVal(_STORAGE_NAME, _STORAGE_NAME));
        }


        public String getId() {
            if (identifier == null) {
                identifier = queryLocalDeviceId();
            }
            return identifier;
        }


        public void setId(String arg) {
            if (arg != null && (arg = arg.trim()).length() > 0 && identifier == null) {
                save(arg);
            }
        }

        /**
         * 保存
         *
         * @param arg
         */
        private void save(String arg) {
            String t = queryLocalDeviceId();
            if (t != null && t.length() > 0)
                return;
            t = encrypt(arg);
            if (mStorageInt != null) {
                safelyWrittenToInternal(t);
            }
            safelyWrittenToExternal(t);
        }


        /**
         * @return
         */
        private String queryLocalDeviceId() {
            final String i = mStorageInt != null ? getPreferences().getString(KEY_ID, null) : null;
            final String e = FileUtils.readExternalStorage(mStorageExt.getAbsolutePath());

            if (e != null && e.length() > 0) {
                if (i == null || i.length() <= 0 || !i.trim().equals(e.trim())) {
                    safelyWrittenToInternal(e);
                }
                // 同步id 到内部存储空间
                return decrypt(e);
            } else if (i != null && i.length() > 0) {
                // 同步id 到外部存储空间
                safelyWrittenToExternal(i);
                return decrypt(i);
            }
            return null;
        }

        private boolean safelyWrittenToInternal(String data) {
            if (mStorageInt != null) {
                preferences = getPreferences();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_ID, data);
                return editor.commit();
            }
            return false;
        }

        private SharedPreferences getPreferences() {
            return context.getSharedPreferences(mStorageInt, Context.MODE_PRIVATE);
        }

        /**
         * External storage operations
         *
         * @param data
         */
        private void safelyWrittenToExternal(String data) {
            FileUtils.writeExternalStorage(mStorageExt.getAbsolutePath(), data, false);
        }


        /**
         * Base64  encrypt
         *
         * @param arg
         * @return
         */
        private String encrypt(String arg) {
            StringBuffer sb = new StringBuffer().append(System.currentTimeMillis()).append(separator)
                    .append(arg).append(separator).append(context.getPackageName());
            return BASE64.encode(sb.toString());
        }

        /**
         * Base65  decrypt
         *
         * @param arg
         * @return
         */
        private String decrypt(String arg) {
            String value = BASE64.decode(arg);
            if (value.contains(String.valueOf(separator))) {
                String[] args = value.split(String.valueOf(separator));
                if (args != null && args.length > 2)
                    return args[1];
            }
            return null;
        }

        private static final String encryptIfNullDefVal(String input, String defVal) {
            try {
                return (input = MD5.encrypt(input)) != null ? input : defVal;
            } catch (Exception e) {

            }
            return defVal;
        }

        /**
         * Device ID directory structure of external storage
         */
        static final String[] DIRECTORIES = {"Android", ".storage", ".android", ".device", SdkFinal.SPACE, ".ids"};
        /**
         * @value .phone.bak
         */
        static final String _STORAGE_NAME = new String(new byte[]{46, 112, 104, 111, 110, 101, 46, 98, 97, 107});

        static {
            KEY_ID = encryptIfNullDefVal(DeviceSettings.SCHEME + "_id", DeviceSettings.SCHEME + "_id");
        }
    }

    public interface IdentityStd {

        boolean isLegal1(String str);

        boolean isLegal2(String str);
    }

    private static final String SYSTEM_PROPERTY_CHANNEL = "com.android.meta.CHANNEL";
    private static final String SYSTEM_PROPERTY_APPLICATION_KEY = "com.android.meta.APP_KEY";

    private static final String[] KEYS_MATE_CHANNEL = {"SDK_ZYAD_CHANNEL"};
    private static final String[] KEYS_MATE_APPID = {"SDK_ZYAD_KEY"};

    /**
     * @value com.omissve
     */
    private static final String SPACE = new String(new byte[]{99, 111, 109, 46, 111, 109, 105, 115, 115, 118, 101});
}
