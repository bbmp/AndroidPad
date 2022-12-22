package com.robam.pan.constant;

import com.robam.common.constant.PanConstant;
import com.robam.pan.R;

public enum PanWarningEnum {
    E0(PanConstant.WORK_0,"", -1, -1),
    E4(PanConstant.WORK_4,"", R.string.pan_warning_e1_title, R.string.pan_warning_e4_content),
    E5(PanConstant.WORK_5,"",R.string.pan_warning_e1_title, R.string.pan_warning_e5_content);

    private int code;
    private String value;
    private int promptTitleRes;
    private int promptContentRes;

    PanWarningEnum(int code, String value, int promptTitleRes, int promptContentRes) {
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

    public static PanWarningEnum match(int code) {
        for (PanWarningEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return E0;
    }
}
