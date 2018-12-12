package com.sye.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by drago on 2016/12/14 0014.
 */
public class MD5 {


    public static boolean compare(File file, String checkSum)
            throws IOException {
        return compare(new FileInputStream(file), checkSum);
    }

    public static boolean compare(FileInputStream file, String checkSum)
            throws IOException {
        final String code;
        return (code = encrypt(file)) != null && code.trim().length() == 32 ?
                code.equalsIgnoreCase(checkSum) : false;
    }

    public static boolean compare(String arg, String checkSum) {
        final String code;
        return (code = encrypt(arg)) != null && code.length() == 32 ?
                code.equalsIgnoreCase(checkSum) : false;
    }

    /**
     * @param arg
     * @return
     */
    public static String encrypt(String arg) {
        return encrypt(arg, "UTF-8");
    }

    /**
     * @param arg
     * @param charsetName
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String arg, String charsetName) {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        byte[] bytes;
        if (charsetName != null && charsetName.trim().length() > 0)
            try {
                bytes = arg.getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                bytes = arg.getBytes();
            }
        else
            bytes = arg.getBytes();

        messageDigest.update(bytes);

        return bufferToHex(messageDigest.digest());
    }


    /**
     * @param file
     * @return
     */
    public static String encrypt(File file) throws IOException, NoSuchAlgorithmException {
        return encrypt(new FileInputStream(file));
    }

    /**
     * @param stream
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String encrypt(FileInputStream stream) throws IOException {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        byte[] buffer = new byte[1024];
        int numRead;
        while ((numRead = stream.read(buffer)) > 0) {
            messageDigest.update(buffer, 0, numRead);
        }
        try {
            stream.close();
        } catch (IOException e) {

        }
        return bufferToHex(messageDigest.digest());
    }

    /**
     * @param arg
     * @return
     */
    public static boolean check(final String arg) {
        return arg == null ? false :
                Pattern.compile("[0-9a-zA-Z]{32}").matcher(arg.trim()).matches();
    }


    private static String bufferToHex(byte[] bytes) {
        final int L = bytes.length;
        final StringBuffer buffer = new StringBuffer(2 * L);
        int p;
        for (int i = 0; i < L; i++) {
            p = bytes[i];
            if ((p = p < 0 ? (p + 256) : p) < 16)
                buffer.append("0");
            buffer.append(Integer.toHexString(p));
        }
        return buffer.toString();
    }


}
