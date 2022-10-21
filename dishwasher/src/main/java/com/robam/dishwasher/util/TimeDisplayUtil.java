package com.robam.dishwasher.util;

public class TimeDisplayUtil {
    public static String getHourAndMin(int remainingTime){
        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        //return (aHour <= 9 ? ("0"+aHour) : aHour) + "h" + (aHour_surplus <= 9 ? ("0"+aHour_surplus) : aHour_surplus) + "min";
        return aHour + "h" + (aHour_surplus <= 9 ? ("0"+aHour_surplus) : aHour_surplus) + "min";
    }

}
