package com.robam.ventilator.constant;


import com.robam.cabinet.constant.CabinetWaringEnum;
import com.robam.ventilator.R;

public enum VentilatorWarningEnum {
    E0(VentilatorConstant.DEVICE_WARING_E0,"",-1, -1), //无告警
    E1(VentilatorConstant.DEVICE_WARING_E1,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e1_content),
    E2(VentilatorConstant.DEVICE_WARING_E2,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e2_content),
    E3(VentilatorConstant.DEVICE_WARING_E3,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e3_content),
    E4(VentilatorConstant.DEVICE_WARING_E4,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e4_content),
    E5(VentilatorConstant.DEVICE_WARING_E5,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e5_content),
    E6(VentilatorConstant.DEVICE_WARING_E6,"",R.string.ventilator_warning_e1_title, R.string.ventilator_warning_e6_content);

    private int code;
    private String value;
    private int promptTitleRes;
    private int promptContentRes;

    VentilatorWarningEnum(int code, String value, int promptTitleRes, int promptContentRes) {
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

    public static VentilatorWarningEnum match(int code) {
        for (VentilatorWarningEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return E0;
    }
}
