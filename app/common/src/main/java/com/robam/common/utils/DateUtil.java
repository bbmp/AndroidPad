package com.robam.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 时间字段处理类
 *
 * @author hxw
 * @date: 2017-1-4
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_STANDARD_ = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_NO_BLANK = "yyyyMMddHHmmss";

    public static final String PATTERN_DATE = "yyyy-MM-dd";

    public static final String PATTERN_DATE_NO = "yyyyMMdd";
    public static final String PATTERN_DATE_D = "yyyy-MM-dd-HH-mm-ss";

    public static final String PATTERN = "HH:mm";
    public static final String PATTERN2 = "HHmm";

    public static final String PATTERN_TIME = "HH:mm:ss";
    private static SimpleDateFormat simpleDateFormat;

    private static long lastClickedTime = 0;

    /**
     * 把Date类型转换为yyyy-mm-dd形式的String 类型
     *
     * @param date
     * @return String (yyyy-mm-dd)
     */
    public static String getPreDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        SimpleDateFormat f = new SimpleDateFormat(PATTERN_DATE);
        return f.format(date);
    }

    /**
     * 毫秒转日期
     *
     * @param pattern
     * @param dateTime
     * @return
     */

    public static String getFormatedDateTime(String pattern, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date(dateTime + 0));
    }

    /**
     * 把Timestamp类型的数据转换成指定形式的String类型 默认为 yyyy-MM-dd HH:mm:ss
     *
     * @param timestamp
     * @param pattern
     * @return String
     */
    public static String timestamp2String(Timestamp timestamp, String pattern) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp null illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(timestamp.getTime()));
    }

    /**
     * 把Date类型转换为指定形式的String类型
     *
     * @param date
     * @param pattern
     * @return String
     */
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("timestamp null illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
            ;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当前时间
     *
     * @return Timestamp
     */
    public static Timestamp currentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    /**
     * 获取当前时间
     *
     * @return Timestamp
     */
    public static long currentTimestampLong() {
        return new Date().getTime();
    }


    /**
     * 获取指定形式的当前时间
     *
     * @param pattern
     * @return String
     */
    public static String currentTimestamp2String(String pattern) {
        return timestamp2String(currentTimestamp(), pattern);
    }

    /**
     * String 类型转换为指定形式的Timestamp 类型 默认形式为：yyyy-MM-dd HH:mm:ss
     *
     * @param strDateTime
     * @param pattern
     * @return Timestamp
     */
    public static Timestamp string2Timestamp(String strDateTime, String pattern) {
        if (strDateTime == null || strDateTime.equals("")) {
            throw new IllegalArgumentException("Date Time Null Illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(strDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new Timestamp(date.getTime());
    }

    /**
     * String 类型转换为指定形式的Date类型 默认形式为：yyyy-mm-dd
     *
     * @param strDate
     * @param pattern
     * @return Date date
     */
    public static Date string2Date(String strDate, String pattern) {
        if (strDate == null || strDate.equals("")) {
            throw new RuntimeException("str date null");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = DateUtil.PATTERN_DATE;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;

        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * String 类型转换为指定形式的Date类型 默认形式为：yyyy-mm-dd
     *
     * @param miliSeconds 毫秒
     * @param pattern
     * @return Date date
     */
    public static Date long2Date(long miliSeconds, String pattern) {
        if (miliSeconds == 0) {
            throw new RuntimeException("str date null");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = DateUtil.PATTERN_STANDARD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date();
        date.setTime(miliSeconds);
        return string2Date(sdf.format(date), pattern);
    }

    /**
     * @param strDate
     * @param sourcePattern
     * @param targetPattern
     * @return String
     */
    public static String string2string(String strDate, String sourcePattern, String targetPattern) {
        Date date = string2Date(strDate, sourcePattern);
        SimpleDateFormat sdf = new SimpleDateFormat(targetPattern);
        return sdf.format(date);
    }

    /**
     * 获取指定时间字符串中的年份
     *
     * @param strDest
     * @return String
     */
    public static String stringToYear(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.valueOf(c.get(Calendar.YEAR));
    }

    /**
     * 获取指定时间字符串中的月份
     *
     * @param strDest
     * @return String
     */
    public static String stringToMonth(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // return String.valueOf(c.get(Calendar.MONTH));
        int month = c.get(Calendar.MONTH);
        month = month + 1;
        if (month < 10) {
            return "0" + month;
        }
        return String.valueOf(month);
    }

    /**
     * 获取指定时间字符串中的日期号
     *
     * @param strDest
     * @return String
     */
    public static String stringToDay(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // return String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            return "0" + day;
        }
        return "" + day;
    }

    /**
     * 获取某年某月1号星期几
     *
     * @param c
     * @return Date
     */
    public static Date getFirstDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;
        c.set(year, month, day, 0, 0, 0);
        return c.getTime();
    }

    /**
     * 获取某年某月最后1号星期几
     *
     * @param c
     * @return Date
     */
    public static Date getLastDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = 1;
        if (month > 11) {
            month = 0;
            year = year + 1;
        }
        c.set(year, month, day - 1, 0, 0, 0);
        return c.getTime();
    }

    public static String date2GregorianCalendarString(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is null");
        }
        long tmp = date.getTime();
        GregorianCalendar ca = new GregorianCalendar();
        ca.setTimeInMillis(tmp);
        try {
            XMLGregorianCalendar t_XMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(ca);
            return t_XMLGregorianCalendar.normalize().toString();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Date is null");
        }

    }

    /**
     * 比较两个日期是否相同
     *
     * @param firstDate
     * @param secondDate
     * @return boolean
     */
    public static boolean compareDate(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            throw new RuntimeException();
        }

        String strFirstDate = date2String(firstDate, "yyyy-MM-dd");
        String strSecondDate = date2String(secondDate, "yyyy-MM-dd");
        if (strFirstDate.equals(strSecondDate)) {
            return true;
        }
        return false;
    }

    /**
     * 获取指定日期的StartTime
     *
     * @param currentDate
     * @return Date (yyyy-MM-dd 00:00:00)
     */
    public static Date getStartTimeOfDate(Date currentDate) {
        String strDateTime = date2String(currentDate, "yyyy-MM-dd") + " 00:00:00";
        return string2Date(strDateTime, "yyyy-MM-dd hh:mm:ss");
    }
    public static String getStartTimeOfDateString(Date currentDate) {
        return  date2String(currentDate, "yyyy-MM-dd") + " 00:00:00";
    }
    /**
     * 获取指定日期的EndTime
     *
     * @param currentDate
     * @return Date (yyyy-MM-dd 59:59:59)
     */
    public static Date getEndTimeOfDate(Date currentDate) {
        String strDateTime = date2String(currentDate, "yyyy-MM-dd") + " 23:59:59";
        return string2Date(strDateTime, "yyyy-MM-dd hh:mm:ss");
    }
    public static String getEndTimeOfDateString(Date currentDate) {
        return  date2String(currentDate, "yyyy-MM-dd") + " 23:59:59";
    }
    /**
     * 获取指定年份和月份对应的天数
     *
     * @param year  指定的年份
     * @param month 指定的月份
     * @return int 返回天数
     */
    public static int getDaysInMonth(int year, int month) {
        if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
                || (month == 12)) {
            return 31;
        } else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
            return 30;
        } else {
            if (((year % 4) == 0) && ((year % 100) != 0) || ((year % 400) == 0)) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 根据所给的起止时间来计算间隔的天数
     *
     * @param date  起始时间
     * @param date2 结束时间
     * @return int 返回间隔天数
     */
    public static int getIntervalDays(Date date, Date date2) {
        long startdate = date.getTime();
        long enddate = date2.getTime();
        long interval = enddate - startdate;
        int intervalday = (int) (interval / (1000 * 60 * 60 * 24));
        return intervalday;
    }

    /**
     * 根据所给的起止时间来计算间隔的月数
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return int 返回间隔月数
     */
    @SuppressWarnings("static-access")
    public static int getIntervalMonths(java.sql.Date startDate, java.sql.Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int startDateM = startCal.MONTH;
        int startDateY = startCal.YEAR;
        int enddatem = endCal.MONTH;
        int enddatey = endCal.YEAR;
        int interval = (enddatey * 12 + enddatem) - (startDateY * 12 + startDateM);
        return interval;
    }

    /**
     * 返回当前日期时间字符串 格式:yyyy-mm-dd hh:mm:ss
     *
     * @return String returnStr 返回当前字符串型日期时间
     */
    public static String getCurrentTime() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 按指定形式返回当前日期时间字符串 默认格式:yyyy-mm-dd hh:mm:ss
     *
     * @param pattern
     * @return String returnStr 返回当前字符串型日期时间
     */
    public static String getCurrentTime(String pattern) {
        String returnStr = null;
        if (pattern == null || pattern.equals("")) {
            pattern = DateUtil.PATTERN_STANDARD;
        }
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 返回当前日期字符串 格式:yyyy-mm-dd
     *
     * @return String returnStr 返回当前字符串型日期
     */
    public static String getCurrentDate() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 按指定形式返回当前日期字符串 默认格式:yyyy-mm-dd
     *
     * @param pattern
     * @return String 返回当前字符串型日期
     */
    public static String getCurrentDate(String pattern) {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 日期相加减
     *
     * @param inputDate
     * @param i
     * @return 返回形式yyy-mm-dd
     */
    public static String processDateAddDay(String inputDate, int i) {
        if (inputDate == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(string2Date(inputDate, ""));
            calendar.add(Calendar.DATE, i);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 月份相加减
     *
     * @param inputDate
     * @param i
     * @return 返回形式yyy-mm-dd
     */
    public static String processDateAddMonth(String inputDate, int i) {
        if (inputDate == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(string2Date(inputDate, ""));
            calendar.add(Calendar.MONTH, i);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取给定当前日期下一个月日期
     *
     * @param startDateStr
     * @return Date
     */
    public static Date getAfterMonthDate(String startDateStr) {
        if (TextUtils.isEmpty(startDateStr))
            return null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(string2Date(startDateStr, "yyyy-MM-dd"));
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取时间段之间天数
     *
     * @param begintime
     * @param endtime
     * @return long dayCount
     */
    public static long getDaycount(String begintime, String endtime) {
        long dayCount = 0L;
        if (TextUtils.isEmpty(begintime) || TextUtils.isEmpty(endtime))
            return dayCount;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        ParsePosition pos1 = new ParsePosition(0);
        Date dt1 = formatter.parse(begintime, pos);
        Date dt2 = formatter.parse(endtime, pos1);
        dayCount = dt2.getTime() - dt1.getTime();
        dayCount = dayCount / (1000 * 60 * 60 * 24) + 1;
        return dayCount;
    }

    public static void main(String[] args) {
        String str1 = "2011-01-01";
        String str2 = "1988-09-09";
        Date date1 = DateUtil.string2Date(str1, "yyyy-MM-dd");
        Date date2 = DateUtil.string2Date(str2, "yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        c2.add(Calendar.YEAR, 4);
        if (c2.before(c1)) {
            System.out.println("illegal");
        } else {
            System.out.println("ok");
        }
        System.out.println(getPreDay(new Date()));
    }

    /**
     * 返回当前月份 格式:yyyyMM
     *
     * @return String 返回当前字符串型日期
     */
    public synchronized static String getCurrentMonth() {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMM");
        return f.format(new Date());
    }

    /**
     * 日期时间比较大小
     *
     * @param starttime
     * @param endtime
     * @param pattern
     * @return int 1: 开始时间>结束时间 0：开始时间=结束时间 -1：开始时间<结束时间
     */
    public static int compareTime(String starttime, String endtime, String pattern) {
        if (starttime == null || endtime == null) {
            throw new RuntimeException();
        }
        int result = 0;
        try {
            if (pattern == null || pattern.equals("")) {
                pattern = DateUtil.PATTERN_STANDARD;
            }
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(string2Date(starttime, pattern));
            c2.setTime(string2Date(endtime, pattern));
            result = c1.compareTo(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     */
    public static String formatDate(String date, String pattern) {
        if (date == null || date == "") {
            throw new RuntimeException();
        }
        String result = null;
        try {
            if (pattern == null || pattern.equals("")) {
                pattern = DateUtil.PATTERN_DATE;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            result = format.format(format.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间分钟相加减
     *
     * @param time1
     * @param time2
     */
    public static String timeplus(String time1, String time2) {
        if (time1 == null || time2 == null) {
            throw new RuntimeException();
        }
        String result = null;
        try {
            int t1 = Integer.valueOf(time1);
            int t2 = Integer.valueOf(time2);
            int t3 = t1 + t2;
            String t4 = t3 / 100 + "";
            String t5 = t3 % 100 + "";
            if (t4.length() <= 1) {
                t4 = "0" + t4;
            }
            if (t5.length() <= 1) {
                t5 = "0" + t5;
            }
            result = t4 + ":" + t5 + ":" + "00";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatDate1(String time, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        // try {
        // date = simpleDateFormat.parse(time);
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        String date = simpleDateFormat.format(new Date(Long.parseLong(time)));
        return date;
    }

    /**
     * @param intervalTime 间隔时间
     * @return
     */
    public static boolean isPermitClick(long intervalTime) {
        Date date = new Date();
        if (date.getTime() - lastClickedTime > intervalTime) {
            lastClickedTime = date.getTime();
            return true;
        }
        lastClickedTime = date.getTime();
        return false;
    }

    /**
     * 判断是否同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * 判断是否同一天
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * 判断是否过期
     *
     * @param date 时间（此时间与系统的当前时间比较）
     * @param val  过期时间
     * @param unit 过期时间的单位
     * @return
     */
    public static boolean isExpire(Date date, long val, TimeUnit unit) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (date.getTime() + unit.toMillis(val)) < System.currentTimeMillis();
    }

    public static Date stringTiemOrDate (String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss");
        try {
            Date date = simpleDateFormat.parse(time) ;
            return date ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    /**
     * 秒转换为时分
     * @param time
     * @return
     */
    public static String secForMatTime2(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            if (time % 60 == 0)
                minute = time / 60;
            else
                minute = time / 60 + 1;
            if (minute < 60) {
                timeStr = "00" + ":" + unitFormat(minute);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute);
            }
        }
        return timeStr;
    }
    /**
     * 秒转换为分
     * @param time
     * @return
     */
    public static String secForMatTime3(int time) {
        String timeStr = null;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00";
        else {
            if (time % 60 == 0)
                minute = time / 60;
            else
                minute = time / 60 + 1;

            timeStr = String.valueOf(minute) ;

        }
        return timeStr;
    }

    /**
     * 秒转换为时分秒
     * @param time
     * @return
     */
    public static String secForMatTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + "分钟" + unitFormat(second)+"秒";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分钟" + unitFormat(second)+"秒";
            }
        }
        return timeStr;
    }
    public static String secToTime1(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                if(second!=0){
                    timeStr = unitFormat(minute) + "分钟" + unitFormat(second)+"秒";
                }else{
                    timeStr = unitFormat(minute) + "分钟" ;
                }
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                if(second!=0){
                    timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分钟" + unitFormat(second)+"秒";
                }else{
                    timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分钟" ;
                }
            }
        }
        return timeStr;
    }
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getCurrentDate1Hours() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN);
        String dateStr = format.format(new Date());
        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = format.parse(dateStr);
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        dateStr = format.format(date);
        System.out.println(dateStr);

        return dateStr ;
    }


    /**
     * 获取小时差
     * @param orderDate
     * @return
     */
    public static int getHousGap(String orderDate) {
        String currentTime = DateUtil.getCurrentTime(PATTERN);
        int orderMin = Integer.parseInt(orderDate.substring(orderDate.length() - 2, orderDate.length()));
        int currentMin = Integer.parseInt(currentTime.substring(currentTime.length() - 2, orderDate.length()));
        int orderHours = Integer.parseInt(orderDate.substring(0, 2));
        int currentHours = Integer.parseInt(currentTime.substring(0, 2));
        if ( DateUtil.compareTime(currentTime , orderDate , PATTERN) == 1){

            if (orderMin - currentMin < 0){
                return orderHours + 23 - currentHours ;
            }else {
                return orderHours + 24 - currentHours ;
            }
        }else {
            if (orderMin - currentMin < 0){
                return orderHours  - currentHours - 1 ;
            }else {
                return orderHours  - currentHours ;
            }
        }
    }

    /**
     * 获取分钟差
     * @param orderDate
     * @return
     */
    public static int getMinGap(String orderDate) {
        String currentTime = DateUtil.getCurrentTime(DateUtil.PATTERN);
        int orderMin = Integer.parseInt(orderDate.substring(orderDate.length() - 2, orderDate.length()));
        int currentMin = Integer.parseInt(currentTime.substring(currentTime.length() - 2, orderDate.length()));
        int orderHours = Integer.parseInt(orderDate.substring(0, 2));
        int currentHours = Integer.parseInt(currentTime.substring(0, 2));
        if (orderMin - currentMin < 0){
            return orderMin + 60 - currentMin ;
        }else {
            return orderMin - currentMin ;
        }

    }
    //获取周几 和时间
    public static String getWeek() {
        String curWeek = "周日";
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                curWeek = "周日";
                break;
            case 2:
                curWeek = "周一";
                break;
            case 3:
                curWeek = "周二";
                break;
            case 4:
                curWeek = "周三";
                break;
            case 5:
                curWeek = "周四";
                break;
            case 6:
                curWeek = "周五";
                break;
            case 7:
                curWeek = "周六";
                break;
        }
        return curWeek;
    }
    public static int getWeek(String setTime) {
        int curWeek = 1;
        String week = setTime.substring(0, 2);
        switch (week) {
            case "周日":
                curWeek = 1;
                break;
            case "周一":
                curWeek = 2;
                break;
            case "周二":
                curWeek = 3;
                break;
            case "周三":
                curWeek = 4;
                break;
            case "周四":
                curWeek = 5;
                break;
            case "周五":
                curWeek = 6;
                break;
            case "周六":
                curWeek = 7;
                break;
        }
        return curWeek;
    }
    //比较小时和分钟
    public static boolean isNowTime(String setTime) {
        String curTime = (String) DateFormat.format("HH:mm", System.currentTimeMillis());
        String curHour = curTime.substring(0, 2);
        String curMin = curTime.substring(3, 5);
        String hour = setTime.substring(2, 4);
        String minute = setTime.substring(5, 7);
        if (curHour.equals(hour) && curMin.equals(minute)) { //
            return true;
        }
        return false;
    }
}