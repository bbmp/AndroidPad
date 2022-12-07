package com.robam.dishwasher.constant;

import com.robam.dishwasher.R;

/**
 * 附加模式
 */
public enum DishWasherAuxEnum {

    AUX_NONE(DishWasherConstant.AUX_NONE," ",-1),
    AUX_PAN_POWFULL(DishWasherConstant.AUX_PAN_POWFULL,"锅具强洗", R.string.dishwasher_prompt_pan_powfull),
    AUX_KILLPOWFULL(DishWasherConstant.AUX_KILL_POWFULL,"加强除菌", R.string.dishwasher_prompt_kill_powfull),
    AUX_FLUSH(DishWasherConstant.AUX_FLUSH,"长效净存",R.string.dishwasher_prompt_flush),
    AUX_DOWN_WASH(DishWasherConstant.AUX_DOWN_WASH,"下层洗",R.string.dishwasher_prompt_down_wash);

    private int code;
    private String value;
    private int promptRes;

    DishWasherAuxEnum(int code, String value,int promptRes) {
        this.code = code;
        this.value = value;
        this.promptRes =promptRes;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public int getPromptRes() {
        return promptRes;
    }

    public static String match(int key) {
        String value = "";
        for (DishWasherAuxEnum s : values()) {
            if (s.getCode() == key) {
                value = s.getValue();
                break;
            }
        }
        return value;
    }

    public static int matchValue(String value){
        int code = DishWasherConstant.AUX_NONE;

        for (DishWasherAuxEnum s : values()) {
            if (s.getValue().equals(value)) {
                code = s.getCode();
                break;
            }
        }
        return code;
    }

    public static int matchPromptRes(String value){
        int promptRes = DishWasherConstant.AUX_NONE;

        for (DishWasherAuxEnum s : values()) {
            if (s.getValue().equals(value)) {
                promptRes = s.getPromptRes();
                break;
            }
        }
        return promptRes;
    }



}
