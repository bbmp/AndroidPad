package com.robam.dishwasher.constant;

public enum DishWorkStateEnum {
    //主模式
    XD(0,"洗涤"),
    PX(1,"漂洗"),
    GZ(2,"干燥");


    private int code;
    private String value;

    DishWorkStateEnum(int code, String value) {
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
        String value = XD.value;
        for (DishWorkStateEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }


}
