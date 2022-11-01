package com.robam.steamoven.constant;

/**
 * @author r210190
 *  工作模式对应枚举
 */

public enum SteamModeEnum {
    /**
     *
     */
    NO_MOEL(SteamConstant.NO_MOEL,"无模式"),
    XIANNENZHENG(SteamConstant.XIANNENZHENG,"鲜嫩蒸"),
    YIYANGZHENG(SteamConstant.YIYANGZHENG,"营养蒸"),
    GAOWENZHENG(SteamConstant.GAOWENZHENG,"高温蒸 "),
    WEIBOZHENG(SteamConstant.WEIBOZHENG,"微波蒸 "),
    ZHIKONGZHENG(SteamConstant.ZHIKONGZHENG,"澎湃蒸"),

    KUAIRE(SteamConstant.KUAIRE,"快热"),
    FENGBEIKAO(SteamConstant.FENGBEIKAO,"风焙烤"),
    BEIKAO(SteamConstant.BEIKAO,"焙烤"),
    FENGSHANKAO(SteamConstant.FENGSHANKAO,"风扇烤"),
    QIANGSHAOKAO(SteamConstant.QIANGSHAOKAO,"强烤烧"),
    SHAOKAO(SteamConstant.SHAOKAO,"烤烧 "),
    KUAISUYURE(SteamConstant.KUAISUYURE,"快速预热"),
    GUOSHUHONGGAN(SteamConstant.GUOSHUHONGGAN,"果蔬烘干 "),
    EXP(SteamConstant.EXP,"EXP"),
    WEIBOKAO(SteamConstant.WEIBOKAO,"微波烤"),

    KONGQIZHA(SteamConstant.KONGQIZHA,"空气炸 "),
//    WEIBO(24,"微波 "),
//    CESHI(25,"节能测试模式 "),

    SHOUDONGJIASHIKAO(SteamConstant.SHOUDONGJIASHIKAO,"加湿烤焙烤"),
    JIASHIBEIKAO(SteamConstant.JIASHIBEIKAO,"加湿烤风焙烤"),
    JIASHIFENGBEIKAO(SteamConstant.JIASHIFENGBEIKAO,"加湿烤强烤烧"),


//    SHUIZHI1(29, "P1"),
//    SHUIZHI2(30, "P2"),
//    SHUIZHI3(31, "P3"),
//    SHUIZHI4(32, "P4"),
//    SHUIZHI5(33, "P5"),

    FAJIAO(SteamConstant.FAJIAO,"发酵"),
    GANZAO(SteamConstant.GANZAO,"干燥"),
    SHAJUN(SteamConstant.SHAJUN,"杀菌"),
    CHUGOU(SteamConstant.CHUGOU,"除垢"),
    BAOWEN(SteamConstant.BAOWEN,"保温"),
    JIEDONG(SteamConstant.JIEDONG,"解冻"),
    QINGJIE(SteamConstant.QINGJIE,"清洁");

    private int mode;
    private String name;
    private int defTemp;
    private int defTime;
    private int minTemp;
    private int maxTemp;
    private int minTime;
    private int maxTime;
    private int openDoorWork;
    private int needWater;
    private int addSteam;

    private int minSteam;
    private int maxSteam;
    private int defSteam;

    SteamModeEnum(int mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    SteamModeEnum(int mode, String name, int defTemp, int defTime, int minTemp, int maxTemp, int minTime, int maxTime, int openDoorWork, int needWater) {
        this.mode = mode;
        this.name = name;
        this.defTemp = defTemp;
        this.defTime = defTime;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.openDoorWork = openDoorWork;
        this.needWater = needWater;
    }

    SteamModeEnum(int mode, String name, int defTemp, int defTime, int minTemp, int maxTemp, int minTime, int maxTime, int openDoorWork, int needWater, int addSteam) {
        this.mode = mode;
        this.name = name;
        this.defTemp = defTemp;
        this.defTime = defTime;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.openDoorWork = openDoorWork;
        this.needWater = needWater;
        this.addSteam = addSteam;
    }

    public static String match(int key) {

        String result = null;

        for (SteamModeEnum s : values()) {
            if (s.getMode() == key) {
                result = s.getName();
                break;
            }
        }

        return result;
    }
}
