package com.robam.cabinet.constant;

import com.robam.cabinet.R;

public enum CabinetWaringEnum {
    //主模式
    E0(Constant.DEVICE_WARING_E0,"",R.string.cabinet_waring_e0_title,R.string.cabinet_waring_e0_content),
    E1(Constant.DEVICE_WARING_E1,"",R.string.cabinet_waring_e1_title ,R.string.cabinet_waring_e1_content),
    E3(Constant.DEVICE_WARING_E2,"",R.string.cabinet_waring_e2_title,R.string.cabinet_waring_e2_content),
    E5(Constant.DEVICE_WARING_E5,"",R.string.cabinet_waring_e5_title,R.string.cabinet_waring_e5_content),
    E6(Constant.DEVICE_WARING_E6,"",R.string.cabinet_waring_e6_title,R.string.cabinet_waring_e6_content),
    E255(Constant.DEVICE_WARING_E255,"",-1,-1);


    private int code;
    private String value;
    private int promptTitleRes;
    private int promptContentRes;

    CabinetWaringEnum(int code, String value, int promptTitleRes, int promptContentRes) {
        this.code = code;
        this.value = value;
        this.promptTitleRes = promptTitleRes;
        this.promptContentRes = promptContentRes;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public int getPromptContentRes() {
        return promptContentRes;
    }

    public int getPromptTitleRes() {
        return promptTitleRes;
    }

    public static CabinetWaringEnum match(int code) {
        for (CabinetWaringEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return E255;
    }


}
