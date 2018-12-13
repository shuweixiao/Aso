package com.sye.net;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by super.dragon() on 2017/3/27 0027.
 * <p>
 * <p>
 * <p>
 * version 1.0.1
 */
public class Connection {


    private static final String TAG = "HTTP";

    /**
     * This time could be user-definable.  This is the default.
     */
    public static final int LENGTH_SHORT = 0;
    /**
     * This time could be user-definable.
     */
    public static final int LENGTH_LONG = 1;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";


    //    @IntDef({LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {

    }

    int mDuration;

    protected int connectTimeout = SHORT_DURATION_TIMEOUT;
    protected int readTimeout = SHORT_DURATION_TIMEOUT;

    protected final String method;
    protected String url;
    protected Map<String, String> headers;
    protected final String body;

    protected int responseCode = -1;
    protected String errorMsg;
    private String contentString;
    private InputStream contentStream;
    private byte[] contentBytes;


    public Connection(String url) {
        this(url, null, null, METHOD_GET);
    }

    public Connection(String url, Map<String, String> headers) {
        this(url, null, headers, METHOD_GET);
    }

    public Connection(String url, String body) {
        this(url, body, null, METHOD_POST);
    }

    public Connection(String url, String body, Map<String, String> headers) {
        this(url, body, headers, METHOD_POST);
    }

    public Connection(String url, String body, String method) {
        this(url, body, null, method);
    }

    public Connection(String url, String body, Map<String, String> headers, String method) {
        if (url == null || (url = url.trim()).length() <= 0)
            throw new NullPointerException("Destination url is null");
        if (method == null)
            throw new NullPointerException("Request method is null");

        this.url = method.equalsIgnoreCase(METHOD_GET) ? prepare(url, body) : url;
        this.body = body;
        this.headers = headers != null ? Collections.unmodifiableMap(headers) : null;
        this.method = method.equalsIgnoreCase(METHOD_GET) ? METHOD_GET : METHOD_POST;

    }

    public void setDuration(@Duration int duration) {
        switch (duration) {
            case LENGTH_LONG:
                this.connectTimeout = LONG_DURATION_TIMEOUT;
                this.readTimeout = LONG_DURATION_TIMEOUT;
                break;
            case LENGTH_SHORT:
                this.connectTimeout = SHORT_DURATION_TIMEOUT;
                this.readTimeout = SHORT_DURATION_TIMEOUT;
                break;
            default:
                return;
        }

    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }


    public final String getStringContent() throws Exception {
        final HttpURLConnection huc = openConnection();
        try {
            if (isOk(huc.getResponseCode())) {
                return safelyConsume(huc.getInputStream(), getEncoding(huc));
            } else {
                readErrorMsg(huc.getErrorStream());
                return null;
            }
        } finally {
            disconnect(huc);
        }
    }


    public final InputStream getStreamContent() throws Exception {
        final HttpURLConnection huc = openConnection();
        try {
            if (isOk(huc.getResponseCode())) {
                InputStream is = huc.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                is.close();
                return new ByteArrayInputStream(baos.toByteArray());
            } else {
                readErrorMsg(huc.getErrorStream());
                return null;
            }
        } finally {
            disconnect(huc);
        }
    }

    protected final void readErrorMsg(InputStream stream) {
        if (stream != null)
            try {
                this.errorMsg = safelyConsume(stream, null);
            } catch (Exception e) {

            }
    }

    protected final void disconnect(HttpURLConnection huc) {
        if (huc != null)
            try {
                huc.disconnect();
            } catch (Exception e) {
            }
    }


    protected final void setError(int errorCode, String errorMsg, Object obj) {
        this.responseCode = -2;
        this.errorMsg = errorMsg;
    }

    /**
     * @param url
     * @param parameters
     * @return
     * @throws NullPointerException
     * @throws
     */
    protected final String prepare(String url, String parameters) {
        if (url == null || url.trim().length() <= 0)
            throw new NullPointerException("Destination url is null");

        String str = new String(url.trim());
        if (parameters != null && parameters.trim().length() > 0)
            str = str.endsWith("?") ? str + parameters.trim() : str + "?" + parameters;

        return str;
    }

    protected final HttpURLConnection openConnection()
            throws Exception {

        reset();

        final HttpURLConnection huc;

        if ("https".equalsIgnoreCase(url.substring(0, 5))) {
            TrustManager[] trustManagers = {new MX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpsURLConnection hsuc = (HttpsURLConnection) new URL(url).openConnection();
            hsuc.setHostnameVerifier(new MHostnameVerifier());
            hsuc.setSSLSocketFactory(sslSocketFactory);
            huc = hsuc;

        } else if ("http".equalsIgnoreCase(url.substring(0, 4))) {
            huc = (HttpURLConnection) new URL(url).openConnection();

        } else
            throw new IOException("Unsupported protocol");

        huc.setRequestMethod(method.toUpperCase());
        huc.setReadTimeout(readTimeout);
        huc.setConnectTimeout(connectTimeout);
        huc.setUseCaches(false);
        huc.setDoInput(true);
        // Sets request properties
        setRequestProperty(huc, headers);

        if (method.equals(METHOD_POST)) {
            huc.setDoOutput(true);
            safelyWrite(huc.getOutputStream(), body, DEFAULT_CHARSET);
        } else {
            huc.connect();
        }
        return huc;
    }

    protected final void reset() {
        this.responseCode = 0;
        this.errorMsg = null;
    }


    /**
     * Created by super.dragon on 2017/3/28 0028 16:32, email:dragon.eros@outlook.com
     *
     * @param os
     * @param body
     * @return
     */
    protected void safelyWrite(OutputStream os, String body, String charsetName) throws
            IOException {
        if (os != null && body != null) {
            os.write(getBytes(body, charsetName));
            os.flush();
            os.close();
        }
    }

    /**
     * Created by super.dragon on 2017/3/29 0029 8:59, email:dragon.eros@outlook.com
     *
     * @param arg
     * @return
     */
    protected final byte[] getBytes(String arg, String charsetName) {
        try {
            return arg.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            return arg.getBytes();
        }

    }

    public String postViaFormData(String url, Map<String, Object> parameters) throws Exception {
        final String flags = BOUNDARY;
        this.headers.put("Content-Type", "multipart/form-data; boundary=" + flags);
        HttpURLConnection huc = openConnection();

        try {
            safelyWrite(huc.getOutputStream(), buildFormData(flags, parameters), DEFAULT_CHARSET);
            int responseCode = huc.getResponseCode();
            if (!isOk(responseCode))
                return null;
            return safelyConsume(huc.getInputStream(), getEncoding(huc));
        } finally {
            if (huc != null)
                huc.disconnect();
        }
    }


    private String buildFormData(final String flags, Map<String, Object> parameters) {
        if (parameters != null && parameters.size() > 0) {
            final String boundaryPrefix = "--";
            final String boundaryNewLine = "\r\n";
            final StringBuilder sb = new StringBuilder();
            Map<String, Object> temp = new HashMap<>(parameters);
            for (Map.Entry<String, Object> entry : temp.entrySet()) {
                if (null != entry.getKey() && !"".equals(entry.getKey()) && !"null".equals(entry.getKey())) {
                    sb.append(boundaryPrefix).append(flags).append(boundaryNewLine)
                            .append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"")
                            .append(boundaryNewLine).append(boundaryNewLine)
                            .append(entry.getValue()).append(boundaryNewLine);
                }
            }
            if (sb.length() > 0) {
                sb.append(boundaryPrefix).append(flags).append(boundaryPrefix);
                return sb.toString();
            }
        }
        return null;
    }


    /**
     * Created by super.dragon on 2017/3/28 0028 16:31, email:dragon.eros@outlook.com
     *
     * @param stream
     * @return
     */
    private String safelyConsume(InputStream stream, String charset) throws IOException {
        BufferedReader reader = charset == null ? new BufferedReader(new InputStreamReader(stream)) :
                new BufferedReader(new InputStreamReader(stream, charset));
        StringBuilder sb = new StringBuilder();
        String t;
        while ((t = reader.readLine()) != null)
            sb.append(t);
        stream.close();
        return sb.toString();
    }

    protected final boolean isOk(int responseCode) {
        return (this.responseCode = responseCode) / 200 == 1;
    }

    /**
     * @param conn
     * @return
     */
    protected final String getEncoding(URLConnection conn) {
        String contentTypeHeader = conn.getHeaderField("Content-Type");
        if (contentTypeHeader != null) {
            int charsetStart = contentTypeHeader.indexOf("charset=");
            if (charsetStart >= 0) {
                return contentTypeHeader.substring(charsetStart + "charset=".length());
            }
        }
        return "utf-8";
    }


    private void setRequestProperty(final URLConnection uc,
                                    final Map<String, String> headers) {
        if (uc != null && headers != null && headers.size() > 0) {
            final Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                if ((entry = iterator.next()) != null
                        && entry.getKey() != null && entry.getKey().trim().length() > 0
                        && entry.getValue() != null && entry.getValue().trim().length() > 0)
                    uc.setRequestProperty(entry.getKey().trim(), entry.getValue().trim());
            }
        }
    }


    protected class MHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {

            return true;
        }
    }


    /**
     * 信任所有主机 对于任何证书都不做SSL检测
     * 安全验证机制，而Android采用的是X509验证
     */
    protected class MX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
            return null;
        }
    }


    static final int SHORT_DURATION_TIMEOUT = 8000;
    static final int LONG_DURATION_TIMEOUT = 14000;

    protected static final String DEFAULT_CHARSET = "utf-8";
    private static final String BOUNDARY = "----ApplicationFormBoundaryV29ybGQgQ29ubmVjdA";

}
