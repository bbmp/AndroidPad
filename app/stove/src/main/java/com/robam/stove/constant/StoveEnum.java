package com.robam.stove.constant;

import com.robam.common.constant.StoveConstant;

public enum StoveEnum {
    //模式
    STEW(StoveConstant.MODE_STEW,"炖煮"),
    STEAM(StoveConstant.MODE_STEAM,"清蒸"),

    //子模式
    HIGH(StoveConstant.SUBMODE_HIGH, "高温"),
    MID(StoveConstant.SUBMODE_MID, "中温"),
    LOW(StoveConstant.SUBMODE_LOW, "低温");

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
