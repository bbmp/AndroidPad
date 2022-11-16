package com.robam.cabinet.constant;

public enum CabinetEnum {
    //主功能
    DISINFECT(CabinetConstant.FUN_DISINFECT,"消毒"),
    CLEAN(CabinetConstant.FUN_CLEAN,"快洁"),
    DRY(CabinetConstant.FUN_DRY,"烘干"),
    FLUSH(CabinetConstant.FUN_FLUSH,"净存"),
    SMART(CabinetConstant.FUN_SMART,"智能"),
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

        String value = "";

        for (CabinetEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }

        return value;
    }
}
