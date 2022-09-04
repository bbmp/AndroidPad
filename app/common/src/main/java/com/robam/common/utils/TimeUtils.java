package com.robam.common.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static final SimpleDateFormat SDF_DEFAULT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat SDF_DATE = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat(
            "HH:mm:ss", Locale.getDefault());

    public static final SimpleDateFormat SDF_TIME_2 = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #SDF_DEFAULT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, SDF_DEFAULT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #SDF_DEFAULT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 判断是不是今天日期
     */
    public static boolean isToday(long timeInMillis) {
        return DateUtils.isToday(timeInMillis);
    }

    /**
     * 判断是不是昨天日期
     */
    public static boolean isYestoday(long timeInMillis) {

        long mills = timeInMillis + 1000 * 60 * 60 * 24;
        return DateUtils.isToday(mills);

//		Calendar today = Calendar.getInstance(); // 今天
//		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
//		today.set(Calendar.HOUR_OF_DAY, 0);
//		today.set(Calendar.MINUTE, 0);
//		today.set(Calendar.SECOND, 0);
//
//		Calendar yesterday = (Calendar) today.clone();// 昨天
//		yesterday.set(Calendar.DAY_OF_MONTH,
//				today.get(Calendar.DAY_OF_MONTH) - 1);
//
//		Calendar current = Calendar.getInstance();
//		current.setTimeInMillis(timeInMillis);
//
//		return current.before(today) && current.after(yesterday);
    }

    /**
     * 判断是不是前天日期
     */
    public static boolean isBeforeYestoday(long timeInMillis) {

        long mills = timeInMillis + 1000 * 60 * 60 * 24 * 2;
        return DateUtils.isToday(mills);

//		Calendar yesterday = Calendar.getInstance(); // 昨天
//		yesterday.set(Calendar.DAY_OF_MONTH,
//				yesterday.get(Calendar.DAY_OF_MONTH) - 1);
//		yesterday.set(Calendar.HOUR_OF_DAY, 0);
//		yesterday.set(Calendar.MINUTE, 0);
//		yesterday.set(Calendar.SECOND, 0);
//
//		Calendar beforeday = (Calendar) yesterday.clone(); // 前天
//		beforeday.set(Calendar.DAY_OF_MONTH,
//				yesterday.get(Calendar.DAY_OF_MONTH) - 1);
//
//		Calendar current = Calendar.getInstance();
//		current.setTimeInMillis(timeInMillis);
//
//		return current.before(yesterday) && current.after(beforeday);
    }

    static public Calendar getZeroTime(Calendar c) {

        Calendar res = (Calendar) c.clone();
        res.set(Calendar.HOUR_OF_DAY, 0);
        res.set(Calendar.MINUTE, 0);
        res.set(Calendar.SECOND, 0);
        return res;
    }

    /**
     * @param sec 传入秒数
     * @return 返回 “00:00”格式
     */
    public static String sec2clock(long sec) {
        if (sec <= 0) {
            return "00:00";
        }
        StringBuilder sb = new StringBuilder();
        long min = sec / 60;
        long second = sec % 60;
        if (min < 10) {
            sb.append("0");
        }
        sb.append(min);
        sb.append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }

    /**
     * @param seconds 传入秒
     * @return 返回“分钟：00”格式
     */
    public static String secToString(short seconds) {
        short min = (short) (seconds / 60);
        short sec = (short) (seconds % 60);
        if (sec < 10)
            return min + ":0" + sec;
        else
            return min + ":" + sec;
    }

    /**
     * @param seconds 传入秒
     * @return 返回“小时：00：00”格式
     */
    public static String secToHourMinSec(int seconds) {
        short hour = (short) (seconds / 3600);
        short min = (short) ((seconds % 3600) / 60);
        short sec = (short) (seconds % 60);

        if (hour < 10) {
            if (min < 10) {
                if (sec < 10)
                    return "0" + hour + ":0" + min + ":0" + sec;
                else
                    return "0" + hour + ":0" + min + ":" + sec;
            } else {
                if (sec < 10)
                    return "0" + hour + ":" + min + ":0" + sec;
                else
                    return "0" + hour + ":" + min + ":" + sec;
            }
        } else {
            if (min < 10) {
                if (sec < 10)
                    return hour + ":0" + min + ":0" + sec;
                else
                    return hour + ":0" + min + ":" + sec;
            } else {
                if (sec < 10)
                    return hour + ":" + min + ":0" + sec;
                else
                    return hour + ":" + min + ":" + sec;
            }
        }

    }


    /**
     * @param seconds 传入秒
     * @return 返回“00：00”格式
     */
    public static String secToHourMin(int seconds) {
        short hour = (short) (seconds / 3600);
        short min = (short) ((seconds % 3600) / 60);
        short sec = (short) (seconds % 60);

        if (hour < 10) {
            if (min < 10) {
                if (sec < 10)
                    return "0" + min + ":0" + sec;
                else
                    return "0" + min + ":" + sec;
            } else {
                if (sec < 10)
                    return  min + ":0" + sec;
                else
                    return  min + ":" + sec;
            }
        } else {
            if (min < 10) {
                if (sec < 10)
                    return "0" + min + ":0" + sec;
                else
                    return  "0" + min + ":" + sec;
            } else {
                if (sec < 10)
                    return  min + ":0" + sec;
                else
                    return  min + ":" + sec;
            }
        }

    }

    /**
     * @param mi 传入分钟
     * @return 返回“小时：00”格式
     */
    public static String minToHourMin(int mi) {
        short hour = (short) (mi / 60);
        short min = (short) (mi % 60);

        //不足一个小时
        if (hour < 1) {
            if (min < 10) {

                return "00:" + "0" + min;
            } else {
                return "00:" + min;
            }

        } else {

            if (min < 10) {
                return "0" + hour + ":" + "0" + min;
            } else {
                return "0" + hour + ":" + min;
            }

        }
    }
    /**
     * @param seconds 传入秒
     * @return 返回“_h_min”格式
     */
    public static String secToHourMinH(int seconds) {
        short hour = (short) (seconds / 3600);
        short min = (short) ((seconds % 3600) / 60);
        short sec = (short) (seconds % 60);

        if(hour>0){
            if (min > 0)
                return  hour + "h" + min + "min";
            else
                return hour + "h";
        }else{
            if (min > 0)
                return   min + "min";
            else
                return "";
        }

    }
    public static String getlastWeekTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        String weekTime = TimeUtils.getTime(calendar.getTimeInMillis(), TimeUtils.SDF_DATE);
        return weekTime;
    }

}
