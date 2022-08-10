package com.robam.cabinet.constant;

public enum CabinetEnum {

    DISINFECT(1,"消毒"),
    CLEAN(2,"快洁"),
    DRY(3,"烘干"),
    FLUSH(4,"净存"),
    SMART(5,"智能"),
    ;

    private int code;
    private String value;

    CabinetEnum(int code, String value) {
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

        for (CabinetEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }
}
