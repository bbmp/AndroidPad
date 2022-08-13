package com.robam.stove.constant;

import android.view.Display;

public enum StoveEnum {
    SMART(ModeConstant.MODE_SMART,"智能\n烹饪"),
    CURVE(ModeConstant.MODE_CURVE,"烹饪\n曲线"),
    RECIPE(ModeConstant.MODE_RECIPE,"菜谱"),
    TIMING(ModeConstant.MODE_TIMING,"定时"),;

    private int code;
    private String value;

    StoveEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String match(int key) {

        String value = null;

        for (StoveEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }
}
