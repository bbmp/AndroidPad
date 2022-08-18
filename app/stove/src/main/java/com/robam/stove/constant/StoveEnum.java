package com.robam.stove.constant;

public enum StoveEnum {
    SMART(StoveConstant.FUN_SMART,"智能\n烹饪"),
    CURVE(StoveConstant.FUN_CURVE,"烹饪\n曲线"),
    RECIPE(StoveConstant.FUN_RECIPE,"菜谱"),
    TIMING(StoveConstant.FUN_TIMING,"定时"),;

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
