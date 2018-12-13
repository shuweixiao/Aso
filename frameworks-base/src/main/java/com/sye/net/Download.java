package com.sye.net;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 8/3/2018 14:41
 * <p>
 * <p>
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class Download extends Connection {

    private final String savePath;

    public Download(String url, String savePath) {
        this(url, null, null, savePath, METHOD_GET);
    }

    public Download(String url, Map<String, String> headers, String savePath) {
        this(url, null, headers, savePath, METHOD_GET);
    }

    public Download(String url, String body, String savePath) {
        this(url, body, null, savePath, METHOD_POST);
    }

    public Download(String url, String body, Map<String, String> headers, String savePath) {
        this(url, body, headers, savePath, METHOD_POST);
    }

    public Download(String url, String body, Map<String, String> headers, String savePath, String method) {
        super(url, body, headers, method);
        this.savePath = savePath;
    }


    public void download() throws Exception {
        HttpURLConnection huc = openConnection();

        try {
            while ((this.responseCode = huc.getResponseCode()) == 302) {
                this.url = huc.getHeaderField("location");
                if (this.headers != null)
                    this.headers = null;
                huc = openConnection();
            }
            if (isOk(this.responseCode)) {
                write(huc.getInputStream(), savePath);
            } else {
                readErrorMsg(huc.getErrorStream());
            }
        } finally {
            disconnect(huc);
        }
    }

    /**
     * Created by super.dragon on 2017/3/29 0029 8:57, email:dragon.eros@outlook.com
     *
     * @param stream
     * @param path
     * @return
     */
    protected String write(InputStream stream, String path) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        FileOutputStream fos = new FileOutputStream(path, false);
        try {
            int len;
            byte[] off = new byte[1024];
            while ((len = bis.read(off)) != -1)
                fos.write(off, 0, len);
            fos.flush();
            return path;
        } finally {
            if (fos == null)
                fos.close();
            if (bis == null)
                bis.close();
        }
    }
}
