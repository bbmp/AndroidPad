package com.robam.steamoven.constant;

import com.robam.steamoven.bean.FuntionBean;

import java.util.ArrayList;
import java.util.List;

public enum SteamEnum {
    STEAM(SteamConstant.FUN_STEAM,"蒸", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",SteamConstant.STEAM_FLAG_STEAM),
    OVEN(SteamConstant.FUN_OVEN,"烤", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",SteamConstant.STEAM_FLAG_OVEN),
    FRY(SteamConstant.FUN_FRY,"炸", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",SteamConstant.STEAM_FLAG_FRY),
    JIASHI(SteamConstant.FUN_JIASHI,"加湿烤", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",""),
    MULTI(SteamConstant.FUN_MULTI,"多段", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",""),
    RECIPE(SteamConstant.FUN_RECIPE,"菜谱", "", "com.robam.steamoven.ui.activity.RecipeActivity",""),
    AUX(SteamConstant.FUN_AUX,"辅助", "", "com.robam.steamoven.ui.activity.ModeSelectActivity",""),
    CURVE(SteamConstant.FUN_CURVE,"烹饪\n曲线", "", "com.robam.steamoven.ui.activity.CurveActivity",""),;

    public int fun;
    public String name;
    public String backgroundImg;
    public String into;
    public String funFlag;

    SteamEnum(int fun, String name, String backgroundImg, String into,String funFlag) {
        this.fun = fun;
        this.name = name;
        this.backgroundImg = backgroundImg;
        this.into = into;
        this.funFlag = funFlag;
    }

    /**
     * 将枚举值转化成list集合
     *
     * @return
     */

}
