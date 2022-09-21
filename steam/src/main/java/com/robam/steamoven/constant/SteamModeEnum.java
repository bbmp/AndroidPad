package com.robam.steamoven.constant;

/**
 * @author r210190
 *  工作模式对应枚举
 */

public enum SteamModeEnum {
    /**
     *
     */
    NO_MOEL(0,"无模式"),
    XIANNENZHENG(1,"鲜嫩蒸"),
    YIYANGZHENG(2,"营养蒸"),
    GAOWENZHENG(3,"高温蒸 "),
    WEIBOZHENG(4,"微波蒸 "),
    ZHIKONGZHENG(5,"澎湃蒸"),

    KUAIRE(6,"快热 "),
    FENGBEIKAO(7,"风焙烤"),
    BEIKAO(8,"焙烤"),
    FENGSHANKAO(9,"风扇烤"),
    QIANGSHAOKAO(10,"强烤烧"),
    SHAOKAO(11,"烤烧 "),
    KUAISUYURE(12,"快速预热"),
    GUOSHUHONGGAN(13,"果蔬烘干 "),
    EXP(14,"EXP"),
    WEIBOKAO(15,"微波烤"),

    KONGQIZHA(18,"空气炸 "),
//    WEIBO(24,"微波 "),
//    CESHI(25,"节能测试模式 "),

    SHOUDONGJIASHIKAO(22,"加湿烤焙烤"),
    JIASHIBEIKAO(23,"加湿烤风焙烤"),
    JIASHIFENGBEIKAO(24,"加湿烤强烤烧"),


//    SHUIZHI1(29, "P1"),
//    SHUIZHI2(30, "P2"),
//    SHUIZHI3(31, "P3"),
//    SHUIZHI4(32, "P4"),
//    SHUIZHI5(33, "P5"),

    FAJIAO(32,"发酵"),
    GANZAO(33,"干燥"),
    SHAJUN(34,"杀菌"),
    CHUGOU(35,"除垢"),
    BAOWEN(36,"保温"),
    JIEDONG(37,"解冻"),
    QINGJIE(38,"清洁"),
    ;

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
