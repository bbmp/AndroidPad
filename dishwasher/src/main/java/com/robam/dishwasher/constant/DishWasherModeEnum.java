package com.robam.dishwasher.constant;

public enum DishWasherModeEnum {
    Modeless(0, "无模式"),

    StrongWash(1, "强力洗"),
    DailyWash(2, "日常洗"),
    EnergyWash(3, "节能洗"),
    FastWash(4, "快速洗"),
    IntelligentWash(5, "智能洗"),
    CrystalWash(6, "晶亮洗"),
    Drain(7, "排水"),
    SelfTest(8, "自检"),
    Ventilation(9, "自动换气"),
    VentilationWait(10, "自动换气等待");

    private int code;
    private String value;

    DishWasherModeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
