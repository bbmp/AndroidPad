package com.robam.stove.constant;

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


    public final static String EXTRA_RECIPE_ID = "recipeid";
    public final static String EXTRA_RECIPE_STEP = "recipestep";
    public final static String EXTRA_RECIPE_DETAIL = "recipedetail";
    public final static String EXTRA_CURVE_ID = "curveid";
    public final static String EXTRA_CURVE_DETAIL = "curvedetail";
    public final static String EXTRA_MODE_LIST = "modelist";
    public final static String EXTRA_ENTRY_LIST = "entry_list";
    public final static String EXTRA_STEP_LIST = "step_list";

    //炉头id
    public final static String isCook = "is_cook";
    public final static String stoveId = "stove_id";
    public final static String level = "level";
    public final static String workStatus = "work_status";
    public final static String timingtime = "timing_time"; //定时时间
}
