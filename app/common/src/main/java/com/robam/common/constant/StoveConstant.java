package com.robam.common.constant;

public class StoveConstant {
    //主功能
    public final static int FUN_SMART = 1;
    public final static int FUN_CURVE = 2;
    public final static int FUN_RECIPE = 3;
    public final static int FUN_TIMING = 4;

    //模式
    public final static int MODE_STEW = 1;   //炖煮
    public final static int MODE_STEAM = 2; //清蒸
    public final static int MODE_FRY = 3;  //煎炸
    public final static int MODE_TIMING = 4;   //定时
    public final static int SUBMODE_HIGH = 3; //高温
    public final static int SUBMODE_MID = 4; //中温
    public final static int SUBMODE_LOW = 5; //低温

    //工作状态
    public final static int WORK_CLOSE = 0; //关机
    public final static int WORK_STANDBY = 1; //待机
    public final static int WORK_WORKING = 2;//工作中


    public final static String EXTRA_RECIPE_ID = "recipeid";
    public final static String EXTRA_RECIPE_STEP = "recipestep";
    public final static String EXTRA_RECIPE_DETAIL = "recipedetail";
    public final static String EXTRA_CURVE_ID = "curveid";
    public final static String EXTRA_CURVE_DETAIL = "curvedetail";
    public final static String EXTRA_MODE_LIST = "modelist";
    public final static String EXTRA_ENTRY_LIST = "entry_list";
    public final static String EXTRA_STEP_LIST = "step_list";
    public final static String EXTRA_STOVE_ID = "stove_id";
    public final static String EXTRA_NEED_TIME = "needtime";
    public final static String EXTRA_PAN_GUID = "pan_guid";

    //炉头id
    public final static String isCook = "is_cook";
    public final static String stoveId = "stove_id";
    public final static String recipeId = "recipe_id";//菜谱id
    public final static String recipeStep = "recipe_step";//菜谱步骤
    public final static String level = "level";   //挡位
    public final static String stoveNum = "stove_num";
    public final static String leftStatus = "left_status";
    public final static String leftMode = "left_mode";
    public final static String leftLevel = "left_level";
    public final static String leftTemp = "left_temp";
    public final static String leftTime = "left_time";//剩余秒数
    public final static String leftSetTime = "left_set_time";//左灶设置时间
    public final static String leftAlarm = "left_alarm";//报警状态
    public final static String rightStatus = "right_status";
    public final static String rightMode = "right_mode"; //工作模式
    public final static String rightLevel = "right_level";
    public final static String rightTemp = "right_temp";
    public final static String rightTime = "right_time";//剩余秒数
    public final static String rightSetTime = "right_set_time";//右灶设置时间
    public final static String rightAlarm = "right_alarm";//
    public final static String workStatus = "work_status";
    public final static String timingtime = "timing_time"; //定时时间
    public final static String lockStatus = "lock_status";//锁状态
    public final static String attributeNum = "attribute_num";//参数个数
    public final static String control = "control";
    public final static String steps = "steps";
    public final static String setMode = "set_mode";// 设定模式
    public final static String stepTemp = "step_temp";//温度
    public final static String stepTime = "step_time";//时间

    //炉头状态
    public static final int STOVE_CLOSE = 0;
    public static final int STOVE_OPEN = 1;
    public static final byte LOCK = 0x01;
    public static final byte UNLOCK = 0x00;

    //告警信息
    public final static int DEVICE_WARING_E1 = 1;
    public final static int DEVICE_WARING_E2 = 2;
    public final static int DEVICE_WARING_E3 = 3;
    public final static int DEVICE_WARING_E4 = 4;
    public final static int DEVICE_WARING_E5 = 5;
    public final static int DEVICE_WARING_E6 = 6;
    public final static int DEVICE_WARING_E7 = 7;
    public final static int DEVICE_WARING_E8 = 8;
    public final static int DEVICE_WARING_E9 = 9;
    public final static int DEVICE_WARING_E10 = 10;
    public final static int DEVICE_WARING_E255 = 255; //无报警
}
