package com.sye.app;

import com.sye.settings.SdkFinal;

import java.util.Arrays;
import java.util.Collection;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 18:01
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
final class SdkIdentityStd implements SdkFinal.IdentityStd {


    private static final Collection<String> INNERS = Arrays.asList("test");
    private static final boolean ENABLE_INNER_DATA = INNERS != null && INNERS.size() > 0;

    @Override
    public boolean isLegal1(String str) {
        if (str != null && str.length() > 0) {
            if (ENABLE_INNER_DATA && INNERS.contains(str))
                return true;
            else if (str.length() == 16) {
                final char[] array = str.toCharArray();
                for (char c : array) {
                    if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90))
                        continue;
                    return false;
                }
                return true;
            }
//            return Pattern.compile("^(\\d|[a-z]){16}$").matcher(value).matches();
        }
        return false;
    }

    @Override
    public boolean isLegal2(String str) {
        if (str != null && str.length() > 0) {
            if (ENABLE_INNER_DATA && INNERS.contains(str))
                return true;
            else if (str.length() == 32) {
                final char[] array = str.toCharArray();
                for (char c : array) {
                    if ((c >= 48 && c <= 57) || (c >= 97 && c <= 122))
                        continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
