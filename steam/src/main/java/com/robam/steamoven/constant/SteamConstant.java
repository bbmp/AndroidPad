package com.robam.steamoven.constant;

public class SteamConstant {
    //主功能
    public final static int FUN_STEAM = 1;//蒸
    public final static int FUN_OVEN = 2;          //烤
    public final static int FUN_FRY = 3; //炸
    public final static int FUN_JIASHI = 4; //加湿烤
    public final static int FUN_MULTI = 5; //多段
    public final static int FUN_RECIPE = 6; //菜谱
    public final static int FUN_AUX = 7; //辅助
    public final static int FUN_CURVE = 8;// 曲线
    //模式
    public final static int NO_MOEL = 0;
    public final static int XIANNENZHENG = 1;
    public final static int YIYANGZHENG = 2;
    public final static int GAOWENZHENG = 3;
    public final static int WEIBOZHENG = 4;
    public final static int ZHIKONGZHENG = 5;
    public final static int KUAIRE = 6;
    public final static int FENGBEIKAO = 7;
    public final static int BEIKAO = 8;
    public final static int FENGSHANKAO = 9;
    public final static int QIANGSHAOKAO = 10;

    public final static int SHAOKAO = 11;
    public final static int KUAISUYURE = 12;
    public final static int GUOSHUHONGGAN = 13;
    public final static int EXP = 14;
    public final static int WEIBOKAO = 15;

    public final static int KONGQIZHA = 18;

    public final static int SHOUDONGJIASHIKAO = 22;
    public final static int JIASHIBEIKAO = 23;
    public final static int JIASHIFENGBEIKAO = 24;

    public final static int  FAJIAO = 32;
    public final static int  GANZAO = 33;
    public final static int  SHAJUN = 34;
    public final static int  CHUGOU = 35;
    public final static int  BAOWEN = 36;
    public final static int  JIEDONG = 37;
    public final static int  QINGJIE = 38;

    public final static String EXTRA_MODE_LIST = "modelist";

    /**
     * 一体机状态
     */
    public final static String SteameOvenStatus = "SteameOvenStatus";
    /**
    *   故障码
     */
    public final static String SteamFaultCode = "SteamFaultCode";

    /**
     * 一体机工作状态
     */
    public final static String SteameOvenWorknStatus = "SteameOvenWorknStatus";
    /**
     * 一体机工作模式
     */
    public final static String SteameOvenMode = "SteameOvenMode";
    public final static String SteameOvenLeftTime = "SteameOvenLeftTime";
    public final static String EXTRA_CURVE_ID = "curve_id";
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_CURVE_DETAIL = "curve_detail";

    public final static String MSG_ID = "MSG_ID";//预约时间targetGuid
    public final static String TARGET_GUID = "target_guid";
    /**
     * 用户编码[10Byte]
     */
    public final static String UserId = "UserId";


    /**
     * 属性设置功能区分
     * 0 ：设置一体机属性（单模式）
     * 1 ：设置一体机单一工作状态 暂停 开始 停止
     * 2 ：设置多段
     * 3 : 设置菜谱
     * 4 : 控制烟机风量 开启 关闭
     */
    public final static String BS_TYPE = "bsType";

    public final static String ARGUMENT_NUMBER = "ArgumentNumber";//参数个数

    public final static String OvenRecipeStep = "OvenRecipeStep";//菜谱步骤
    public final static String ArgumentNumber = "ArgumentNumber";//参数个数
    public final static String SetTempDown = "SetTempDown";
    public final static String SetTempDownKey = "SetTempDownKey";
    public final static String SetTempDownLength = "SetTempDownLength";
    public final static String SetTempDownValue = "SetTempDownValue";
    //    String CurrentTempDown = "CurrentTempDown";
    public final static String CurrentTempDownKey = "CurrentTempDownKey";
    public final static String CurrentTempDownLength = "CurrentTempDownLength";
    public final static String CurrentTempDownValue = "CurrentTempDownValue";
    public final static String OrderTime_key = "OrderTime_key";
    public final static String SetTime_H_key = "SetTime_H_key";
    public final static String SetTime_H = "SetTime_H";
    public final static String SetTime_H_length = "SetTime_H_length";
    public final static String SetTime_H_Value = "SetTime_H_Value";
    public final static String OrderTime_value_min = "OrderTime_value_min";
    public final static String OrderTime_value_hour = "OrderTime_value_hour";
    public final static String OrderTime_length = "OrderTime_length";

    /**
     * 电源控制
     */
    public final static  String powerCtrlKey = "powerCtrlKey" ;
    public final static  String powerCtrlLength = "powerCtrlLength" ;
    public final static  String powerCtrlKeyValue = "powerCtrlValue" ;


    /**
     * 电源控制
     */
    public final static String powerCtrl = "powerCtrl" ;
//    public final static String powerCtrlKey = "powerCtrlKey" ;
//    public final static String powerCtrlLength = "powerCtrlLength" ;
    /**
     * 工作状态
     */
    public final static String workState = "workState" ;
    /**
     * 工作控制
     */
    public final static String workCtrl  = "workCtrl" ;
    public final static String workCtrlKey  = "workCtrlKey" ;
    public final static String workCtrlLength  = "workCtrlLength" ;
    /**
     * 设置预约时间
     */
    public final static String setOrderMinutes  = "setOrderMinutes" ;
    public final static String setOrderMinutesKey  = "setOrderMinutesKey" ;
    public final static String setOrderMinutesLength  = "setOrderMinutesLength" ;
    public final static String setOrderMinutes01="setOrderMinutes01";
    public final static String setOrderMinutes02="setOrderMinutes02";
    public final static String setOrderMinutes03="setOrderMinutes03";
    public final static String setOrderMinutes04="setOrderMinutes04";
    /**
     * 剩余预约时间
     */
    public final static String orderLeftMinutes = "orderLeftMinutes" ;
    public final static String orderRightMinutes = "orderRightMinutes" ;
    public final static String orderLeftMinutes1 = "orderLeftMinutes1" ;
    public final static String orderRightMinutes1 = "orderRightMinutes1" ;
    public final static String orderMinutesLength="orderMinutesLength";
    //剩余预约时间 秒
    public final static String orderLeftTime = "orderLeftTime" ;
    /**
     * 故障码
     */
    public final static String faultCode = "faultCode" ;
    /**
     * 灯开关
     */
    public final static String lightSwitch = "lightSwitch" ;
    public final static String lightSwitchKey = "lightSwitchKey" ;
    public final static String lightSwitchLength = "lightSwitchLength" ;
    /**
     * 旋转烤开关
     */
    public final static String rotateSwitch = "rotateSwitch" ;
    public final static String rotateSwitchKey = "rotateSwitchKey" ;
    public final static String rotateSwitchLength = "rotateSwitchLength" ;
    /**
     * 水箱状态
     */
    public final static String waterBoxState = "waterBoxState" ;
    /**
     * 水箱控制
     */
    public final static String waterBoxCtrl = "waterBoxCtrl" ;
    public final static String waterBoxCtrlKey = "waterBoxCtrlKey" ;
    public final static String waterBoxCtrlLength = "waterBoxCtrlLength" ;
    /**
     * 水位状态
     */
    public final static String waterLevelState = "waterLevelState" ;
    /**
     * 门状态
     */
    public final static String doorState = "doorState" ;
    /**
     * 门控制开关
     */
    public final static String doorSwitch = "doorSwitch" ;
    public final static String doorSwitchKey = "doorSwitchKey" ;
    public final static String doorSwitchLength = "doorSwitchLength" ;
    /**
     * 加蒸汽工作状态
     */
    public final static String steamState = "steamState" ;
    /**
     * 加蒸汽控制
     */
    public final static String steamCtrl = "steamCtrl" ;
    public final static String steamCtrlKey = "steamCtrlKey" ;
    public final static String steamCtrlLength = "steamCtrlLength" ;
    /**
     * 菜谱编号
     */
    public final static  String recipeId = "recipeId" ;
    public final static String recipeId01 = "recipeId01" ;
    public final static String recipeIdKey = "recipeIdKey" ;
    public final static String recipeIdLength = "recipeIdLength" ;
    /**
     * 菜谱设置总时间
     */
    public final static String recipeSetMinutes = "recipeSetMinutes" ;
    public final static String recipeSetMinutesH = "recipeSetMinutesH" ;
    public final static String recipeSetMinutes1 = "recipeSetMinutes1" ;
    public final static String recipeSetMinutesH1 = "recipeSetMinutesH1" ;
    public final static String recipeSetMinutesKey = "recipeSetMinutesKey" ;
    public final static String recipeSetMinutesLength = "recipeSetMinutesLength" ;

    /**
     * 当前温度 上温度
     */
    public final static String curTemp = "curTemp" ;
    /**
     * 当前温度 下温度
     */
    public final static String curTemp2 = "curTemp2" ;
    /**
     * 剩余总时间
     */
    public final static String totalRemainSeconds = "totalRemainSeconds" ;
    public final static String totalRemainSeconds2 = "totalRemainSeconds2" ;
    public final static String totalRemain = "totalRemain" ;
    /**
     * 除垢请求标识
     */
    public final static String descaleFlag = "descaleFlag" ;
    /**
     * 当前蒸模式累计工作时间
     */
    public final static String curSteamTotalHours = "curSteamTotalHours" ;
    /**
     * 蒸模式累计需除垢时间
     */
    public final static String curSteamTotalNeedHours = "curSteamTotalNeedHours" ;
    /**
     * 实际运行时间
     */
    public final static String cookedTime = "cookedTime" ;
    /**
     * 除垢状态
     */
    public final static String chugouType = "chugouType" ;
    /**
     * 当前段数/段序
     */
    public final static String curSectionNbr = "curSectionNbr" ;
    /**
     * 设置段数
     */
    public final static String sectionNumber = "sectionNumber" ;
    public final static String sectionNumberKey = "sectionNumberKey" ;
    public final static String sectionNumberLength = "sectionNumberLength" ;
    /**
     * 首段模式
     */
    public final static String mode = "mode" ;
    public final static String modeKey = "modeKey" ;
    public final static String modeLength = "modeLength" ;
    /**
     * 首段设置的上温度
     */
    public final static String setUpTemp = "setUpTemp" ;
    public final static String setUpTempKey = "setUpTempKey" ;
    public final static String setUpTempLength = "setUpTempLength" ;
    /**
     * 首段设置的下温度
     */
    public final static String setDownTemp = "setDownTemp" ;
    public final static String setDownTempKey = "setDownTempKey" ;
    public final static String setDownTempLength = "setDownTempLength" ;
    /**
     * 首段设置的时间
     */
    public final static String setTime = "setTime" ;
    public final static String setTime0b = "setTime0b" ;
    public final static String setTime1b = "setTime1b" ;
    public final static String setTime2b = "setTime2b" ;
    public final static String setTime3b = "setTime3b" ;
    public final static String setTimeH = "setTimeH" ;
    public final static String setTimeKey = "setTimeKey" ;
    public final static String setTimeLength = "setTimeLength" ;
    /**
     * 首段剩余的时间
     */
    public final static String restTime = "restTime" ;
    public final static String restTimeH = "restTimeH" ;
    /**
     * 首段蒸汽量
     */
    public final static String steam = "steam" ;
    public final static String steamKey = "steamKey" ;
    public final static String steamLength = "steamLength" ;

    /**
     * 第2段段模式
     */
    public final static String mode2 = "mode2" ;
    /**
     * 第2段设置的上温度
     */
    public final static String setUpTemp2 = "setUpTemp2" ;
    /**
     * 第2段设置的下温度
     */
    public final static String setDownTemp2 = "setDownTemp2" ;
    /**
     * 第2段设置的时间
     */
    public final static String setTime2 = "setTime2" ;
    public final static String setTimeH2 = "setTimeH2" ;
    /**
     * 第2段剩余的时间
     */
    public final static String restTime2= "restTime2" ;
    public final static String restTimeH2= "restTimeH2" ;
    /**
     * 第2段蒸汽量
     */
    public final static String steam2 = "steam2" ;

    /**
     * 第3段段模式
     */
    public final static String mode3 = "mode3" ;
    /**
     * 第3段设置的上温度
     */
    public final static String setUpTemp3 = "setUpTemp3" ;
    /**
     * 第3段设置的下温度
     */
    public final static String setDownTemp3 = "setDownTemp3" ;
    /**
     * 第3段设置的时间
     */
    public final static String setTime3 = "setTime3" ;
    public final static String setTimeH3 = "setTimeH3" ;
    /**
     * 第3段剩余的时间
     */
    public final static String restTime3= "restTime3" ;
    public final static String restTimeH3= "restTimeH3" ;
    /**
     * 第3段蒸汽量
     */
    public final static String steam3 = "steam3" ;


    /**
     * 童锁 灶具
     */
    public final static String stove_v_chip2 = "stove_v_chip2" ;
    /**
     * 菜谱ID 灶具
     */
    public final static String repice_id2 = "repice_id2" ;
    /**
     * 灶具温度  灶具
     */
    public final static String stove_temp2 = "stove_temp2" ;
    /**
     * 已工作时间 灶具
     */
    public final static String stove_time2 = "stove_time2" ;

    /**
     * 报警状态 灶具
     */
    public final static String stove_faultCode2 = "stove_faultCode2" ;
    public final static String userid = "userid";


    /**
     * (首段)微波档
     * 位
     */
    public final static  String microWaveLevelCtrl  = "microWaveLevelCtrl" ;
    public final static String microWaveLevelCtrlKey  = "microWaveLevelCtrlKey" ;
    public final static String microWaveLevelLength  = "microWaveLevelLength" ;
    public final static String microWaveLevelCtrl01  = "microWaveLevelCtrl01" ;

    /**
     * 复热模式重量
     *
     */
    public final static String microWaveWeightCtrl  = "microWaveWeightCtrl" ;
    public final static String microWaveWeightCtrlKey  = "microWaveWeightCtrlKey" ;
    public final static String microWaveWeightLength  = "microWaveWeightLength" ;

    /**
     *
     *加时
     */

    public final static String addExtraTimeCtrl  = "addExtraTimeCtrl" ;
    public final static String addExtraTimeCtrl1  = "addExtraTimeCtrl1" ;
    public final static String addExtraTimeCtrlKey  = "addExtraTimeCtrlKey" ;
    public final static String addExtraTimeCtrlLength  = "addExtraTimeCtrlLength" ;


    /**
     * 电源控制 烟机
     */
    public final static String fan_powerCtrl = "powerCtrl" ;
    public final static String fan_powerCtrlKey = "powerCtrlKey" ;
    public final static String fan_powerCtrlLength= "powerCtrlLength" ;
    /**
     * 档位 烟机
     */
    public final static String fan_gear = "gear" ;
    public final static String fan_gearKey = "gearKey" ;
    public final static String fan_gearLength = "gearLength" ;



}
