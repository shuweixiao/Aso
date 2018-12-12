package com.sye.security.action;

/**
 * *****************************************************************************************
 * Created by super.dragon on 2018/5/16 18:17
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class GetSystemPropertyAction implements PrivilegedAction<String> {

    private String theProp;

    public GetSystemPropertyAction(String theProp) {
        this.theProp = theProp;
    }

    public String run() {
        String b = null;
        try {
            b = System.getProperty(theProp);
        } catch (NullPointerException e) {
        }
        return b;
    }
}
