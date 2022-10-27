package com.robam.dishwasher.constant;

import com.robam.dishwasher.R;

public enum DishWasherWaringEnum {
    //主模式
    E0(DishWasherConstant.DEVICE_WARING_E0,"",-1,-1),
    E1(DishWasherConstant.DEVICE_WARING_E1,"", R.string.dishwasher_waring_e1_title,R.string.dishwasher_waring_e1_content),
    E3(DishWasherConstant.DEVICE_WARING_E3,"",R.string.dishwasher_waring_e3_title,R.string.dishwasher_waring_e3_content),
    E4(DishWasherConstant.DEVICE_WARING_E4,"",R.string.dishwasher_waring_e4_title,R.string.dishwasher_waring_e4_content),
    E5(DishWasherConstant.DEVICE_WARING_E5,"",R.string.dishwasher_waring_e5_title,R.string.dishwasher_waring_e5_content),
    E6(DishWasherConstant.DEVICE_WARING_E6,"",R.string.dishwasher_waring_e6_title,R.string.dishwasher_waring_e6_content),
    E7(DishWasherConstant.DEVICE_WARING_E7,"",R.string.dishwasher_waring_e7_title,R.string.dishwasher_waring_e7_content),
    E8(DishWasherConstant.DEVICE_WARING_E9,"",R.string.dishwasher_waring_e9_title,R.string.dishwasher_waring_e9_content),
    E10(DishWasherConstant.DEVICE_WARING_E10,"",R.string.dishwasher_waring_e10_title,R.string.dishwasher_waring_e10_content);


    private int code;
    private String value;
    private int promptTitleRes;
    private int promptContentRes;

    DishWasherWaringEnum(int code, String value,int promptTitleRes,int promptContentRes) {
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

    public static DishWasherWaringEnum match(int code) {
        for (DishWasherWaringEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return E0;
    }


}
