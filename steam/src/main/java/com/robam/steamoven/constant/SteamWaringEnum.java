package com.robam.steamoven.constant;

import com.robam.steamoven.R;

public enum SteamWaringEnum {
    /**
     *
     */
    E2(Constant.WARING_CODE_2, R.string.steam_waring_2,R.string.steam_waring_2_content),
    E3(Constant.WARING_CODE_3, R.string.steam_waring_3,R.string.steam_waring_3_content),
    E4(Constant.WARING_CODE_4,R.string.steam_waring_4,R.string.steam_waring_4_content),
    E5(Constant.WARING_CODE_5, R.string.steam_waring_5,R.string.steam_waring_5_content),
    E6(Constant.WARING_CODE_6, R.string.steam_waring_6,R.string.steam_waring_6_content),
    E7(Constant.WARING_CODE_7, R.string.steam_waring_7,R.string.steam_waring_7_content),
    E8(Constant.WARING_CODE_8, R.string.steam_waring_8,R.string.steam_waring_8_content),
    E9(Constant.WARING_CODE_9, R.string.steam_waring_9,R.string.steam_waring_9_content),
    E10(Constant.WARING_CODE_10, R.string.steam_waring_10,R.string.steam_waring_10_content),
    E11(Constant.WARING_CODE_11, R.string.steam_waring_11,R.string.steam_waring_11_content),
    E14(Constant.WARING_CODE_14, R.string.steam_waring_14,R.string.steam_waring_14_content),
    E16(Constant.WARING_CODE_16, R.string.steam_waring_16,R.string.steam_waring_16_content),
    E17(Constant.WARING_CODE_17, R.string.steam_waring_17,R.string.steam_waring_17_content);

    private int code;
    private int titleResId;
    private int contentResId;

    private SteamWaringEnum(int code, int titleResId,int contentResId) {
        this.code = code;
        this.titleResId = titleResId;
        this.contentResId = contentResId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }

    public int getContentResId() {
        return contentResId;
    }

    public void setContentResId(int contentResId) {
        this.contentResId = contentResId;
    }






}
