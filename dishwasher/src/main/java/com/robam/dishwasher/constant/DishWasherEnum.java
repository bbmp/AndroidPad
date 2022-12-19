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
    DRAIN_WATER(DishWasherConstant.MODE_DRAIN_WATER,"排水"),
    SELF_INSPECTION(DishWasherConstant.MODE_SELF_INSPECTION,"自检"),
    AUTO_AERATION(DishWasherConstant.MODE_AUTO_AERATION,"换气等待"),
    AUTO_AERATION_AWAIT(DishWasherConstant.MODE_AUTO_AERATION_AWAIT,"换气等待"),
    PRE_RINSE(DishWasherConstant.MODE_PRE_RINSE,"预冲洗"),
    SILENT_WASH(DishWasherConstant.MODE_SILENT_WASH,"静音洗"),
    FLUSH(DishWasherConstant.MODE_FLUSH,"护婴净存"),
    FLUSH_AWAIT(DishWasherConstant.MODE_FLUSH_AWAIT,"护婴净存等待"),
    LONG_STORAGE(DishWasherConstant.MODE_LONG_STORAGE,"长效净存"),
    LONG_STORAGE_AWAIT(DishWasherConstant.MODE_LONG_STORAGE_AWAIT,"长效净存等待");

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
        String value = "";
        for (DishWasherEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }


}
