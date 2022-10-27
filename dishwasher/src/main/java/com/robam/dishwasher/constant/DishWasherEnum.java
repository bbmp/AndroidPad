package com.robam.dishwasher.constant;

public enum DishWasherEnum {
    //主模式
    SMART(DishWasherConstant.MODE_SMART,"智能洗"),
    POWFULL(DishWasherConstant.MODE_POWFULL,"强力洗"),
    QUICK(DishWasherConstant.MODE_QUICK,"快速洗"),
    DAILY(DishWasherConstant.MODE_DAILY,"日常洗"),
    SAVING(DishWasherConstant.MODE_SAVING,"节能洗"),
    BRIGHT(DishWasherConstant.MODE_BRIGHT,"晶亮洗"),
    BABYCARE(DishWasherConstant.MODE_BABYCARE,"护婴洗"),
    SELFCLEAN(DishWasherConstant.MODE_SELFCLEAN,"自清洁"),
    FLUSH(DishWasherConstant.MODE_FLUSH,"护婴净存");

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
