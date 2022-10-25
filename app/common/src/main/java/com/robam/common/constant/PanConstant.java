package com.robam.common.constant;

public class PanConstant {
    public final static String EXTRA_RECIPE_ID = "recipeid";
    public final static String EXTRA_RECIPE_DETAIL = "recipedetail";
    public final static String EXTRA_RECIPE_STEP = "recipestep";
    public final static String EXTRA_CURVE_STEP = "curvestep";
    public final static String EXTRA_CURVE_ID = "curveid";
    public final static String EXTRA_CURVE_DETAIL = "curvedetail";
    public final static String EXTRA_ENTRY_LIST = "entry_list";
    public final static String EXTRA_RESTORE_LIST = "restore_list";
    public final static String EXTRA_STEP_LIST = "step_list";
    public static final String EXTRA_NEED_TIME = "needtime";
    public static final String EXTRA_FAVORITE = "favorite";
    public static final String EXTRA_ORDER_ID = "order_id";//我的最爱菜谱序号

    public final static int MSG_PRECIPE = 0; //P档菜谱
    public final static int MSG_CURVE_RESTORE = 1; //曲线还原

    //锅状态
    public final static String Pot_status = "Pot_status";
    //搅拌功能
    public final static int MODE_CLOSE_FRY = 0;
    public final static int MODE_QUICK_FRY = 1; //持续快炒
    public final static int MODE_STIR_FRY = 2;//十秒翻炒
    //工作状态
    //0: 待机 1: 开机 2: 干烧预警(烟机WiFi检测到该标志且灶有开火,主动关闭 灶具所有炉头) 3: 低电量提醒 4: 温度传感器故障 5: 电机故障
    public final static int WORK_0 = 0;
    public final static int WORK_1 = 1;
    public final static int WORK_2 = 2;
    public final static int WORK_3 = 3;
    public final static int WORK_4 = 4;
    public final static int WORK_5 = 5;

    public final static int KEY1 = 1;//p档菜谱启停
    public final static int KEY2 = 2;//实时曲线记录启停
    public final static int KEY3 = 3;//本地曲线上报
    public final static int KEY4 = 4;//曲线还原启停
    public final static int KEY5 = 5;//更换绑定炉头
    public final static int KEY6 = 6;//电机控制

    public final static int start = 1;
    public final static int stop = 0;
    //mqtt 参数
    public final static String pno = "pno";// p档菜谱序号
    public final static String recipeId = "recipe_id";
    public final static String stoveId = "stove_id";
    public final static String level = "level";   //挡位
    public final static String attributeNum = "attribute_num";//参数个数
    public final static String control = "control";
    public final static String steps = "steps";
    public final static String stepTemp = "step_temp";//温度
    public final static String stepTime = "step_time";//时间
    public final static String temp = "temp";  //锅温
    public final static String systemStatus = "system_status"; //系统状态
    public final static String lidStatus = "lid_status";//锅盖状态
    public final static String battery = "battery"; //电量
    public final static String mode = "mode";//工作模式
    public final static String runTime = "run_time"; //运行时间
    public final static String setTime = "set_time"; //设置时间
    public final static String fryMode = "fry_mode";//电机模式
    public final static String interaction = "interaction";//互动参数
    public final static String panParams = "panparams"; //设置锅参数
    public final static String stoveParams = "stoveParams"; //设置灶参数
    public final static String key = "key";//
    public final static String value = "value";
}
