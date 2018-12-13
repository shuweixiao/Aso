package com.sye.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by oveYue on 2015/4/30.
 */
public final class TimeUtils {

    private TimeUtils() {

    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:12, email dragon.eros@outlook.com
     *
     * @param rt execution time
     * @param mt The max time differences（时间被调回的变更造成的最大差异时间）
     * @return
     */
    public static boolean compare(long rt, long mt) {
        if (rt <= 0)
            return true;
        long differ = System.currentTimeMillis() - rt;
        return differ >= 0 ? true : ((differ + mt) <= 0 ? true : false);
    }

    /**
     * create by super.dragon on 2016/1/15 0015 14:12, email dragon.eros@outlook.com
     *
     * @param current
     * @param start
     * @param end
     * @return
     */
    public static boolean compare(long current, long start, long end) {
        if (end == 0)
            return current >= start;
        return current >= start && current <= end;
    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:14, email dragon.eros@outlook.com
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean compareTime(String start, String end) {
        if (start != null && start.length() > 0 && end != null && end.length() > 0) {
            return (new Date().after(getDate(start))) && (new Date().before(getDate(end)));
        } else if (start == null || start.length() <= 0 && (end != null && end.length() > 0)) {
            return new Date().before(getDate(end));
        } else if (start != null && start.length() > 0 && (end == null || end.length() < 0)) {
            return new Date().after(getDate(start));
        }
        return false;
    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:21, email dragon.eros@outlook.com
     *
     * @param pattern
     * @return
     */
    public static String timeFormat(String pattern) {
        return timeFormat(pattern, locale, System.currentTimeMillis());
    }

    /**
     * create by super.dragon on 2016/1/15 0015 14:22, email dragon.eros@outlook.com
     *
     * @param pattern
     * @param locale
     * @return
     */
    public static String timeFormat(String pattern, Locale locale) {
        return timeFormat(pattern, locale, System.currentTimeMillis());
    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:23, email dragon.eros@outlook.com
     *
     * @param date
     * @param locale
     * @return
     */
    public static String timeFormat(Locale locale, long date) {
        return timeFormat(DEF_PATTERN, locale, date);
    }

    /**
     * Function: Time show Format(transform)
     * create by super.dragon on 2016/1/15 0015 14:21, email dragon.eros@outlook.com
     *
     * @param date
     * @return
     */
    public static String timeFormat(long date) {
        return timeFormat(DEF_PATTERN, locale, date);
    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:19, email dragon.eros@outlook.com
     *
     * @param pattern the pattern describing the date and time format
     * @param locale  the locale whose date format symbols should be used
     * @param date    the milliseconds since January 1, 1970, 00:00:00 GMT.
     * @return
     */
    public static String timeFormat(String pattern, Locale locale, long date) {
        return new SimpleDateFormat(pattern, locale).format(new Date(date));
    }

    /**
     * create by super.dragon on 2016/1/15 0015 14:21, email dragon.eros@outlook.com
     * * <p/>
     *
     * @return
     */
    public static String getCurrentTimeFormat() {
        return new SimpleDateFormat(DEF_PATTERN, locale).format(new Date(System.currentTimeMillis()));
    }

    /**
     * create by super.dragon on 2016/1/15 0015 14:22, email dragon.eros@outlook.com
     * <p/>
     * The formatTime into long
     *
     * @param formatTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long toLong(String formatTime) {
        try {
            if (formatTime == null || formatTime.length() <= 0)
                return 0;
            SimpleDateFormat sdf = new SimpleDateFormat(DEF_PATTERN);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.parse(formatTime).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * create by super.dragon on 2016/1/15 0015 14:27, email dragon.eros@outlook.com
     * <p/>
     * The formatTime into Date
     *
     * @param formatTime
     * @return
     */
    public static Date getDate(String formatTime) {
        try {
            String template = getTemplate(formatTime);
            if (regex(formatTime, "^\\d{1,2}$") || regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}$") ||
                    regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$")) {
                String temp = new SimpleDateFormat("yyyy-MM-dd", locale)
                        .format(new Date()) + " " + formatTime;
                return new SimpleDateFormat(template, locale).parse(temp);
            } else {
                return new SimpleDateFormat(template, locale)
                        .parse(formatTime);
            }
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * create by super.dragon on 2016/1/15 0015 14:23, email dragon.eros@outlook.com
     * get date template from param(formatTime)
     *
     * @param formatTime
     * @return
     */
    public static String getTemplate(String formatTime) {
        if (regex(formatTime,
                "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH:mm:ss";
        else if (regex(formatTime,
                "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH:mm";
        else if (regex(formatTime,
                "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH";
        else if (regex(formatTime, "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd";
        else if (regex(formatTime, "^\\d{4}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM";
        else if (regex(formatTime, "^\\d{4}$") || regex(formatTime, "^\\d{4}\\D*$"))
            return "yyyy";
        else if (regex(formatTime, "^\\d{1,2}$") || regex(formatTime, "^\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH";
        else if (regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}$")
                || regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH:mm";
        else if (regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$")
                || regex(formatTime, "^\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$"))
            return "yyyy-MM-dd HH:mm:ss";
        return "";
    }


    static boolean regex(String time, String regex) {
        return Pattern.compile(regex).matcher(time).matches();
    }

    private static final Locale locale = Locale.getDefault();
    private static final String DEF_PATTERN = "yyyy-MM-dd HH:mm:ss";

}