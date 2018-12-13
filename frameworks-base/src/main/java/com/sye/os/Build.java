package com.sye.os;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/12/2018 16:08
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public final class Build {


    /**
     * 发布方向(根据国家为标准)
     */
    private static final double PRODUCT_FLAVOR = ProductFlavors.CHINA;
    /**
     * SDK功能代号/国家.合作ID
     */
    public static final double VERSION_TYPE = PRODUCT_FLAVOR + ProductFlavors.Cooperation.DEFAULT;
    /**
     * SDK版本号
     */
    public static final int VERSION_CODE = 1;
    /**
     * SDK版本号 用户可见
     * [VersionName][date(Month Day)][Generic|Customized] (serial: UpperCase: Offline; LowerCase: online)
     */
    public static final String VERSION_NAME = "1.0.1.184848A";

    public static final int SUPPORTED = 0;


    /**
     *
     */
    static class ProductFlavors {

        public static final double WORLD = 0;
        public static final double CHINA = 86;

        /**
         * Type
         */
        public static class Cooperation {
            public static final double DEFAULT = 0;
            public static final double ONLINE = 0.2;
            public static final double YIJIA = 0.3;
        }
    }

    public static String format() {
        return String.format("version: %s (code %d) to: %.1f", VERSION_NAME, VERSION_CODE, VERSION_TYPE);
    }
}
