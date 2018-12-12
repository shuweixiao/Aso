package com.sye.security.action;

/**
 * *****************************************************************************************
 * Created by super.dragon on 2018/5/16 18:17
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class SetSystemPropertyAction implements PrivilegedAction<String> {

    private String theProp;
    private String newVal;

    public SetSystemPropertyAction(String theProp, String newVal) {
        this.theProp = theProp;
        this.newVal = newVal;
    }

    public String run() {
        return System.setProperty(theProp, newVal);
    }
}