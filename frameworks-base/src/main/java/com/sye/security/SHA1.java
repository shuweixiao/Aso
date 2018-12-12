package com.sye.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by super.dragon on 2016/5/3 0003 11:04
 * <p>
 * version 1.0
 */
public final class SHA1 {


    public static String encrypt(String arg) {
        return encrypt(arg, "UTF-8");
    }


    /**
     * @param file
     * @return
     */
    public static String encrypt(final File file) throws IOException {
        return encrypt(new FileInputStream(file));
    }

    /**
     * @param file
     * @param checkSum
     * @return
     */
    public static boolean compare(final File file, String checkSum) throws IOException {
        return compare(new FileInputStream(file), checkSum);
    }

    /**
     * @param fis
     * @param checkSum
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static boolean compare(final FileInputStream fis, String checkSum)
            throws IOException {
        final String code;
        return (code = encrypt(fis)) != null && code.length() == 40 ?
                code.equalsIgnoreCase(checkSum) : false;
    }

    public static boolean compare(String arg, String checkSum) {
        final String code;
        return (code = encrypt(arg)) != null && arg.length() == 40 ?
                code.equalsIgnoreCase(checkSum) : false;
    }


    /**
     * @param arg
     * @param charsetName
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String arg, String charsetName) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        if (charsetName != null && charsetName.length() > 0) {
            try {
                digest.update(arg.getBytes(charsetName));
            } catch (Exception e) {
                digest.update(arg.getBytes());
            }
        } else
            digest.update(arg.getBytes());

        return bufferToHex(digest.digest());
    }

    /**
     * @param stream
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String encrypt(FileInputStream stream) throws IOException {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        byte[] bt = new byte[1024];
        int n;
        while ((n = stream.read(bt)) > 0)
            digest.update(bt, 0, n);
        try {
            stream.close();
        } catch (Exception e) {

        }
        return bufferToHex(digest.digest());
    }

    /**
     * @param arg
     * @return
     */
    public static boolean check(final String arg) {
        return arg == null ? false :
                Pattern.compile("[0-9a-zA-Z]{40}").matcher(arg.trim()).matches();
    }


    private static String bufferToHex(byte[] bs) {
        StringBuffer sb = new StringBuffer();

        for (byte b : bs) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
