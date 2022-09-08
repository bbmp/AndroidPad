package com.robam.steamoven.constant;

/**
 * @author r210190
 *  工作模式对应枚举
 */

public enum SteamModeEnum {
    /**
     *
     */
//    NO_MOEL(0,"无模式"),
    XIANNENZHENG(1, "鲜嫩蒸", 100, 15, 30, 100, 1, 180, 0, 1, 0),
    YIYANGZHENG(2,"营养蒸", 100, 15, 30, 100, 1, 180, 0, 1, 0),
    GAOWENZHENG(3,"高温蒸", 120, 30, 101, 150, 1, 180, 0, 1, 0),
//    WEIBOZHENG(4,"微波蒸"),
    ZHIKONGZHENG(5,"澎湃蒸", 100, 15, 30, 100, 1, 180, 0, 1, 0),

    KUAIRE(6,"快热", 200, 30, 35, 250, 1, 120, 0, 0, 1),
    FENGBEIKAO(7,"风焙烤", 200, 30, 35, 250, 1, 120, 0, 0, 1),
    BEIKAO(8,"焙烤", 60, 1, 35, 250, 1, 120, 0, 0, 1),
    FENGSHANKAO(9,"风扇烤", 220, 30, 35, 250, 1, 120, 0, 0, 1),
    QIANGSHAOKAO(10,"强烤烧", 108, 30, 35, 250, 1, 120, 0, 0, 1),
//    SHAOKAO(11,"烤烧"),
//    KUAISUYURE(12,"快速预热"),
//    GUOSHUHONGGAN(13,"果蔬烘干 "),
    EXP(14,"EXP", 180, 20, 100, 200, 1, 120, 0, 0, 1),
//    WEIBOKAO(15,"微波烤"),

    KONGQIZHA(18,"空气炸", 160, 30, 35, 250, 1, 180, 0, 0, 0),
//    WEIBO(24,"微波 "),
//    CESHI(25,"节能测试模式 "),

    SHOUDONGJIASHIKAO(22,"加湿烤焙烤", 60, 1, 35, 250, 1, 120, 0, 1, 0, 1, 3, 2),
    JIASHIBEIKAO(23,"加湿烤风焙烤", 200, 30, 35, 250, 1, 120, 0, 1, 0, 1, 3, 2),
    JIASHIFENGBEIKAO(24,"加湿烤强烤烧", 180, 30, 35, 250, 1, 120, 0, 1, 0, 1, 3, 2),


//    SHUIZHI1(29, "P1"),
//    SHUIZHI2(30, "P2"),
//    SHUIZHI3(31, "P3"),
//    SHUIZHI4(32, "P4"),
//    SHUIZHI5(33, "P5"),

    FAJIAO(32,"发酵", 35, 15, 35, 40, 1, 720, 0, 1),
    GANZAO(33,"干燥", 250, 10, 250, 250, 10, 10, 1, 0),
    SHAJUN(34,"杀菌", 100, 30, 30, 100, 1, 180, 0, 1),
    CHUGOU(35,"除垢", 0, 0, 0, 0, 0, 0, 1, 1),
    BAOWEN(36,"保温", 60, 120, 50, 80, 120, 120, 0, 0),
    JIEDONG(37,"解冻", 55, 25, 40, 60, 1, 180, 0, 1),
    QINGJIE(38,"清洁", 100, 15, 100, 100, 15, 15, 0, 1),
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

    SteamModeEnum(int mode, String name, int defTemp, int defTime, int minTemp, int maxTemp, int minTime, int maxTime, int openDoorWork, int needWater, int addSteam, int minSteam, int maxSteam, int defSteam) {
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
        this.minSteam = minSteam;
        this.maxSteam = maxSteam;
        this.defSteam = defSteam;
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

}
