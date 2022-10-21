package com.robam.dishwasher.constant;

public enum DishWasherEnum {
    SMART(DishWasherConstant.MODE_SMART,"智能洗"),
    POWFULL(1,"强力洗"),
    QUICK(4,"快速洗"),
    DAILY(2,"日常洗"),
    SAVING(3,"节能洗"),
    BRIGHT(6,"晶亮洗"),
    BABYCARE(7,"护婴洗"),
    SELFCLEAN(8,"自清洁"),
    FLUSH(9,"护婴净存"),

    AUX_PAN_POWFULL(10,"锅具强洗"),
    AUX_KILLPOWFULL(11,"加强除菌"),
    AUX_FLUSH(12,"长效净存"),
    AUX_DOWN_WASH(13,"下层洗"),;

    private int code;
    private String value;

    DishWasherEnum(int code, String value) {
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

        for (DishWasherEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }
}
