package com.robam.cabinet.util;

import java.util.Calendar;
import java.util.Date;
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
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.SECOND,remainingTime*60);
        int totalHour = calendar.get(Calendar.HOUR_OF_DAY);
        int totalMin = calendar.get(Calendar.MINUTE);
        int totalDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (day != totalDay) {
            return "将在次日" + (totalHour <= 9 ? ("0" + totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0" + totalMin) : totalMin) + "启动工作";
        } else {
            return "将在" + (totalHour <= 9 ? ("0" + totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0" + totalMin) : totalMin) + "启动工作";
        }
    }
}
