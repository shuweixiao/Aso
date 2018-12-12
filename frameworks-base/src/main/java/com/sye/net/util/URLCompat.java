package com.xmois.net.util;

import java.util.regex.Pattern;

/**
 * Created by drago on 2016/12/21 0021.
 */

public class URLCompat {

    /**
     * create by super.dragon on 2016/1/15 0015 13:52, email dragon.eros@outlook.com
     *
     * @param arg
     * @return
     */
    public static boolean isHost(String arg) {
        return matchCharacter(REGEX_DOMAIN, arg);
    }

    /**
     * create by super.dragon on 2016/1/15 0015 13:52, email dragon.eros@outlook.com
     *
     * @param arg
     * @return
     */
    public static boolean isURL(String arg) {
        return matchCharacter(REGEX_URL, arg);
    }


    /**
     * Created by super.dragon on 2017/3/25 0025 10:22, email:dragon.eros@outlook.com
     *
     * @param url
     * @return
     */
    public static String getName(final String url) {
        if (url != null) {
            String s = url;
            // ?
            if (s.length() > 0 && s.contains("?"))
                s = s.substring(0, s.indexOf("?"));
            // #
            if (s.length() > 0 && s.contains("#"))
                s = s.substring(0, s.indexOf("#"));
            if (s.length() > 0 && !s.startsWith("/")) {
                // http://
                if (s.length() >= 7 && s.substring(0, 7).toLowerCase().contains("http://"))
                    s = s.substring(7);
                // https://
                if (s.length() >= 8 && s.substring(0, 8).toLowerCase().contains("https://"))
                    s = s.substring(8);
            }
            // /
            if (s.length() > 0 && s.contains("/"))
                s = s.substring(s.lastIndexOf("/") + 1);
            return s;
        }
        return url;
    }

    /**
     * create by super.dragon on 2016/1/15 0015 13:52, email dragon.eros@outlook.com
     *
     * @param regex
     * @param params
     * @return
     */
    public static boolean matchCharacter(String regex, String params) {
        if (params == null || params.length() <= 0)
            throw new IllegalArgumentException("Params isn't null");
        return Pattern.compile(regex).matcher(params.trim()).matches();
    }

    /**
     * 根据相关数据生成一个完成的url地址
     *
     * @param protocol
     * @param host
     * @param port
     * @param path
     * @param query
     * @return
     */
    public static String build(String protocol, String host, String port, String path,
                               String query) throws IllegalArgumentException {
        StringBuffer s = new StringBuffer();
        if (protocol != null && protocol.length() > 0)
            s.append(protocol).append("://");

        if (host == null || host.length() <= 0)
            throw new IllegalArgumentException("Parameter 2 can't is null...");
        s.append(host);
        if (port != null && port.length() > 0)
            s.append(":").append(port);
        if (path != null & path.length() > 0)
            if (path.substring(0, 1).equals("/"))
                s.append(path);
            else
                s.append("/").append(path);
        if (query != null && query.length() > 0)
            s.append("?").append(query);
        return s.toString();
    }

    /* 协议*/
    private static final String PROTOCOL = "^((https|http|ftp|rtsp|mms)?://)?" +
            "(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?";
    /* IP形式:URL-192.168.1.1 */
    private static final String FORM_IP = "(([0-9]{1,3}\\.){3}[0-9]{1,3}|";
    private static final String HOST_RULES = "com|net|org|int|com.cn|net.cn|org.cn|" +
            "gov.cn|com.hk|edu|gov|mil|arpa|Asia|biz|info|name|pro|coop" +
            "|aero|museum|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|" +
            "be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cf|cg|ch|ci|ck|cl|" +
            "cm|cn|co|cq|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|es|et|ev|fi|" +
            "fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gr|gt|gu|gw|gy|hk|hm|hn|" +
            "hr|ht|hu|id|ie|il|in|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|" +
            "ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|ml|mm|mn|mo|mp|" +
            "mq|mr|ms|mt|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|pa|" +
            "pe|pf|pg|ph|pk|pl|pm|pn|pr|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|" +
            "si|sj|sk|sl|sm|sn|so|sr|st|su|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|" +
//            "公司|中国|网络|"+
            "tt|tv|tw|tz|ua|ug|uk|us|uy|va|vc|ve|vg|vn|vu|wf|ws|ye|yu|za|zm|zr|zw";
    /* 域名形式:URL-xxx.com */
    private static final String HOST = FORM_IP + "(([0-9a-z][0-9a-z-]{0,61})?" +
            "[0-9a-z]\\.)+(" + HOST_RULES + "))";
    /* 端口 */
    private static final String PORT = "(:[0-9]{1,4})?((/?)|";
    /* 路径/path */
    private static final String VIRTUAL_DIRECTORIES = "(/[0-9a-zA-Z_!~*'().;" +
            "?:@&=+$,%#-]+)+/?)$";

    /* Host正则 */
    private static final String REGEX_DOMAIN = PROTOCOL + HOST;
    /* 完整的URL正则 */
    private static final String REGEX_URL = PROTOCOL + HOST + PORT + VIRTUAL_DIRECTORIES;


}
