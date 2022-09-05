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

    //
    public final static int STOVE_LEFT = 0;
    public final static int STOVE_RIGHT = 1;

    public final static String EXTRA_RECIPE_ID = "recipeid";
    public final static String EXTRA_RECIPE_STEP = "recipestep";
    public final static String EXTRA_RECIPE_DETAIL = "recipedetail";
    public final static String EXTRA_CURVE_ID = "curveid";
    public final static String EXTRA_CURVE_DETAIL = "curvedetail";
}
