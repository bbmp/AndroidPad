package com.robam.cabinet.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CabinetAppointmentUtil {

    /**
     * 获取预约分钟数
     * @param time
     * @return
     */
    public static int getAppointmentTime(String time){
        //TODO(需调整)
        return 40;
    }


    public static String  getTimeStr(int remainingTime){
        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        return (aHour <= 9 ? ("0"+aHour) : aHour) + ":" + (aHour_surplus <= 9 ? ("0"+aHour_surplus) : aHour_surplus);
    }

    public static String startTimePoint(int remainingTime){
        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        int addHour = (min + aHour_surplus) / 60;
        int addHour_surplus = (min + aHour_surplus) % 60;

        int totalHour = hour + aHour + addHour;
        int totalMin = addHour_surplus;

        return "将在" + (totalHour <= 9 ? ("0"+totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0"+totalMin) : totalMin) +"启动工作";
    }
}
