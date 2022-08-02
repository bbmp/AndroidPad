package com.robam.steamoven.constant;

/**
 * @author r210190
 *  工作模式对应枚举
 */

public enum SteamOvenModeEnum {
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

    private int code;
    private String value;

    private SteamOvenModeEnum(int code, String message) {
        this.code = code;
        this.value = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String message) {
        this.value = message;
    }

    public static SteamOvenModeEnum match(int key) {

        SteamOvenModeEnum result = null;

        for (SteamOvenModeEnum s : values()) {
            if (s.getCode()==key) {
                result = s;
                break;
            }
        }

        return result;
    }

    public static SteamOvenModeEnum catchMessage(String msg) {

        SteamOvenModeEnum result = null;

        for (SteamOvenModeEnum s : values()) {
            if (s.getValue().equals(msg)) {
                result = s;
                break;
            }
        }

        return result;
    }

}
