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
    KUAISUYURE(1, "快速预热"),
    KUAIRE(2, "快热"),
    FENGPEIKAO(3, "风焙烤"),
    PEIKAO(4, "焙烤"),
    FENGSHANKAO(5, "风扇烤"),
    SHAOKAO(6, "烧烤"),
    QIANGSHAOKAO(7, "强烧烤"),
    JIANKAO(8, "煎烤"),
    DIJIARE(9, "底加热"),
    EXP(10, "辅助"),

    GUOSHUHONGGAN(11, "果蔬烘干"),
    BAOWEN(12, "保温"),
     FAXIAO(13, "发酵"),
     XIANNENZHENG(14, "鲜嫩蒸"),
     YINGYANGZHENG(15, "营养蒸"),
     GAOWENZHENG(16, "高温蒸"),
     JIEDONG(17, "解冻"),
     ZHENGQISHAJUN(18, "蒸汽杀菌"),
     GANZAO(19, "干燥"),
     QINGJIE(20, "清洁"),
     CHUGO(21, "除垢"),
     JIASHIKAO(22, "加湿烤"),
     KONGQIZHA(23, "空气炸"),
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
