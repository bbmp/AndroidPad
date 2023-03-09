package com.robam.steamoven.constant;

/**
 * @author r210190
 *  工作模式对应枚举
 */
public enum SteamModeEnum {

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

    SHOUDONGJIASHIKAO(SteamConstant.SHOUDONGJIASHIKAO,"焙烤"),
    JIASHIBEIKAO(SteamConstant.JIASHIBEIKAO,"风焙烤"),
    JIASHIFENGBEIKAO(SteamConstant.JIASHIFENGBEIKAO,"强烤烧"),


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

    public static int matchCode(String value) {
        int code = SteamConstant.NO_MOEL;
        for (SteamModeEnum s : values()) {
            if (s.getName().equals(value)) {
               return s.getMode();
            }
        }
        return SteamConstant.NO_MOEL;
    }

    /**
     * 是否辅助工作模式
     * @return
     */
    public static boolean isAuxModel(int modeCode){
        if(modeCode == SteamModeEnum.FAJIAO.getMode() ||
                modeCode == SteamModeEnum.GANZAO.getMode() ||
                modeCode == SteamModeEnum.SHAJUN.getMode() ||
                modeCode == SteamModeEnum.CHUGOU.getMode() ||
                modeCode == SteamModeEnum.JIEDONG.getMode() ||
                modeCode == SteamModeEnum.BAOWEN.getMode() ||
                modeCode == SteamModeEnum.QINGJIE.getMode()){
            return true;
        }
        return false;
    }



    /**
     * 非旋转烤模式
     * @param modeCode
     * @return
     */
    public static boolean isNotRotation(int modeCode){
        if(modeCode == SteamModeEnum.EXP.getMode()){
            return true;
        }
        return false;
    }

    /**
     * 是否显示加蒸汽
     * @param modeCode
     * @return
     */
    public static boolean isAddStream(int modeCode){
        if(modeCode == SteamModeEnum.BEIKAO.getMode() ||
                modeCode == SteamModeEnum.FENGSHANKAO.getMode() ||
                modeCode == SteamModeEnum.QIANGSHAOKAO.getMode() ||
                modeCode == SteamModeEnum.KUAIRE.getMode() ||
                modeCode == SteamModeEnum.FENGSHANKAO.getMode() ||
                modeCode == SteamModeEnum.EXP.getMode()){
            return true;
        }
        return false;
    }

    /**
     * 是否是EXP获取加湿烤
     * @param modeCode
     * @return
     */
    public static boolean isExp(int modeCode){
        return modeCode == SteamConstant.EXP;
    }

    /**
     * 模式选择是否包含蒸汽量
     * @param modeCode
     * @return
     */
    public static boolean isAddSteam(int modeCode){
        if(SteamConstant.ZHIKONGZHENG == modeCode ||
                SteamConstant.SHOUDONGJIASHIKAO == modeCode ||
                SteamConstant.JIASHIBEIKAO == modeCode ||
                SteamConstant.JIASHIFENGBEIKAO == modeCode){
            return true;
        }
        return false;
    }

    /**
     * 是否可以手动添加蒸汽
     * @param modeCode
     * @return
     */
    public static boolean isManuallyAddSteam(int modeCode) {
        if (SteamConstant.KUAIRE == modeCode ||
                SteamConstant.FENGBEIKAO == modeCode ||
                SteamConstant.BEIKAO == modeCode ||
                SteamConstant.FENGSHANKAO == modeCode ||
                SteamConstant.QIANGSHAOKAO == modeCode ||
                SteamConstant.EXP == modeCode) {
            return true;
        }
        return false;
    }

    public static boolean needWater(int modeCode){
        boolean needWater = (modeCode == SteamConstant.XIANNENZHENG
                || modeCode == SteamConstant.YIYANGZHENG
                || modeCode == SteamConstant.GAOWENZHENG
                || modeCode == SteamConstant.WEIBOZHENG
                || modeCode == SteamConstant.ZHIKONGZHENG
                || modeCode == SteamConstant.SHOUDONGJIASHIKAO
                || modeCode == SteamConstant.JIASHIBEIKAO
                || modeCode == SteamConstant.JIASHIFENGBEIKAO
                || modeCode == SteamConstant.SHAJUN
                || modeCode == SteamConstant.JIEDONG
                || modeCode == SteamConstant.FAJIAO
                || modeCode == SteamConstant.QINGJIE
                || modeCode == SteamConstant.CHUGOU);
        return needWater;
    }

    /**
     * 是否支持旋转烤
     * @param modeCode
     * @return
     */
    public static boolean isRation(int modeCode){
        boolean supportRadio = (modeCode == SteamConstant.XIANNENZHENG
                || modeCode == SteamConstant.GAOWENZHENG
                || modeCode == SteamConstant.ZHIKONGZHENG
                || modeCode == SteamConstant.BEIKAO
                || modeCode == SteamConstant.FENGBEIKAO
                || modeCode == SteamConstant.QIANGSHAOKAO
                || modeCode == SteamConstant.KUAIRE
                || modeCode == SteamConstant.FENGSHANKAO
                || modeCode == SteamConstant.KONGQIZHA
                || modeCode == SteamConstant.SHOUDONGJIASHIKAO
                || modeCode == SteamConstant.JIASHIBEIKAO
                || modeCode == SteamConstant.JIASHIFENGBEIKAO);
        return supportRadio;
    }

    /**
     * 是否温度不可调节
     * @return
     */
    public static boolean isFixedTemp(int modeCode){
        if(SteamConstant.GANZAO == modeCode ||
                SteamConstant.SHAJUN == modeCode ||
                SteamConstant.QINGJIE == modeCode ){
            return true;
        }
        return false;
    }

    /**
     * 模式的时间与温度均不可调节
     * @param modeCode
     * @return
     */
    public static boolean isModelParamIsFix(int modeCode){
        if(SteamConstant.GANZAO == modeCode ||
                SteamConstant.SHAJUN == modeCode ||
                SteamConstant.QINGJIE == modeCode ){
            return true;
        }
        return false;
    }








}
