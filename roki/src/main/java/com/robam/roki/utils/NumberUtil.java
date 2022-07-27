package com.robam.roki.utils;

public class NumberUtil {
    public static String converString(int num) {
        String unit = "0";
        double newNum = 0.0;
        if (num <= 0)
            return unit;

        if (num < 1000) {
            return num + "";
        }

        if (num >= 1000 && num < 10000) {
            unit = "k";
            newNum = num / 1000.0;
        }

        if (num >= 10000) {
            unit = "w";
            newNum = num / 10000.0;
        }

        String numStr = String.format("%." + 2 + "f", newNum);
        return numStr + unit;
    }
}
