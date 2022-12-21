package com.robam.stove.constant;

import com.robam.stove.R;

public enum StoveWarningEnum {

    E1(StoveConstant.DEVICE_WARING_E1,"", R.string.stove_warning_e1_title, R.string.stove_warning_e1_content),
    E2(StoveConstant.DEVICE_WARING_E2,"",R.string.stove_warning_e1_title, R.string.stove_warning_e2_content),
    E3(StoveConstant.DEVICE_WARING_E3,"",R.string.stove_warning_e1_title, R.string.stove_warning_e3_content),
    E4(StoveConstant.DEVICE_WARING_E4,"",R.string.stove_warning_e1_title, R.string.stove_warning_e4_content),
    E5(StoveConstant.DEVICE_WARING_E5,"",R.string.stove_warning_e1_title, R.string.stove_warning_e5_content),
    E6(StoveConstant.DEVICE_WARING_E6,"",R.string.stove_warning_e1_title, R.string.stove_warning_e6_content),
    E7(StoveConstant.DEVICE_WARING_E7,"",R.string.stove_warning_e1_title, R.string.stove_warning_e7_content),
    E8(StoveConstant.DEVICE_WARING_E8,"",R.string.stove_warning_e1_title, R.string.stove_warning_e8_content),
    E9(StoveConstant.DEVICE_WARING_E9,"",R.string.stove_warning_e1_title, R.string.stove_warning_e9_content),
    E10(StoveConstant.DEVICE_WARING_E10,"",R.string.stove_warning_e1_title, R.string.stove_warning_e10_content),
    E255(StoveConstant.DEVICE_WARING_E255,"", -1, -1);

    private int code;
    private String value;
    private int promptTitleRes;
    private int promptContentRes;

    StoveWarningEnum(int code, String value, int promptTitleRes, int promptContentRes) {
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

    public static StoveWarningEnum match(int code) {
        for (StoveWarningEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return E255;
    }
}
