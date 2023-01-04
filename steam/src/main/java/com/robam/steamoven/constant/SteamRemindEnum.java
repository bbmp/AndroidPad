package com.robam.steamoven.constant;

import com.robam.steamoven.R;

public enum SteamRemindEnum {
    /**
     *
     */
    NEED_WATER(1, R.string.steam_need_water),//无蒸汽
    SMALL_STEAM(2,R.string.steam_need_water),//小蒸汽
    MID_STEAM(3,R.string.steam_need_water),//中蒸汽
    MAX_STEAM(4,R.string.steam_need_water);//大蒸汽

    private int code;
    private int codeRes;

    private SteamRemindEnum(int code, int codeRes) {
        this.code = code;
        this.codeRes = codeRes;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return codeRes;
    }

    public void setValue(int codeRes) {
        this.codeRes = codeRes;
    }



    public static int matchValue(int code){
        for (SteamRemindEnum s : values()) {
            if (s.getCode() == code) {
                return s.getValue();
            }
        }
        return 0;
    }




}
