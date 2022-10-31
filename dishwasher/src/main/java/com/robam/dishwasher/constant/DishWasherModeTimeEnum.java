package com.robam.dishwasher.constant;

public enum DishWasherModeTimeEnum {
    SMART(DishWasherConstant.MODE_SMART,"智能洗"),
    POWFULL(DishWasherConstant.MODE_POWFULL,"强力洗"),
    QUICK(DishWasherConstant.MODE_QUICK,"快速洗"),
    DAILY(DishWasherConstant.MODE_DAILY,"日常洗"),
    SAVING(DishWasherConstant.MODE_SAVING,"节能洗"),
    BRIGHT(DishWasherConstant.MODE_BRIGHT,"晶亮洗"),
    BABYCARE(DishWasherConstant.MODE_BABYCARE,"护婴洗"),
    SELFCLEAN(DishWasherConstant.MODE_SELFCLEAN,"自清洁"),
    FLUSH(DishWasherConstant.MODE_FLUSH,"护婴净存"),
    DRAIN_WATER(DishWasherConstant.MODE_DRAIN_WATER,"排水"),
    SELF_INSPECTION(DishWasherConstant.MODE_SELF_INSPECTION,"自检测试"),
    AUTO_AERATION(DishWasherConstant.MODE_AUTO_AERATION,"自动换气等待");

    private int code;
    private String value;

    DishWasherModeTimeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
