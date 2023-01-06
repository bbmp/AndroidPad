package com.robam.steamoven.constant;

public interface Constant {
    /**
     *  功能 识别
     */
    int FUNTION_DISCEM = 0 ;
    /**
     * 功能 蒸
     */
    int FUNTION_STEAM = 1 ;
    /**
     * 功能 烤
     */
    int FUNTION_OVEN = 2 ;
    /**
     * 功能 炸
     */
    int FUNTION_FRY= 3 ;
    /**
     * 功能 多段
     */
    int FUNTION_MODE_MULTI= 4 ;
    /**
     * 功能 辅助
     */
    int FUNTION_AUX = 7 ;
    /**
     * 功能 设置
     */
    int FUNTION_SET = 8 ;

    /**
     * 功能
     */
    String FUNTION_BEAN  = "funtionBean";
    /**
     * 模式intent携带值
     */
    String INTENT_MODE  = "intent_mode";
    /**
     * 网络连接intent携带值
     */
    String INTENT_WIFI_DATA  = "wifiData";
    /**
     * 网络连接intent携带值
     */
    String INTENT_MULTI_MODE  = "multi_mode";

    /**
     * 加时
     */
    Integer[] ADD_TIME_DATA = {1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 } ;
    /**
     * 加时携带值
     */
    String ADD_TIME  = "add_time";
    /**
     * 菜谱id
     */
    String RECIPE_ID  = "recipe_id";
    /**
     * 主食材
     */
    String MAIN_FOOD  = "main_food";
    /**
     * 曲线的值
     */
    String CARVE_DATA = "carve_data" ;
    /**
     * 曲线的名称
     */
    String CARVE_NAME = "carve_name" ;
    /**
     * 曲线的名称
     */
    String CARVE_ID = "carve_id" ;

    /**
     * 识别/重新识别
     */
    String DISCEN_TYPE = "discen_type" ;
    /**
     * 识别
     */
    String DISCEN = "discen" ;
    /**
     * 重新识别
     */
    String DISCEN_AGAIN = "discen_again" ;
    /**
     * 工作guid
     */
    String WORK_GUID = "work_guid" ;

    String NEED_SET_RESULT = "needSetResult";


    String SEGMENT_DATA_FLAG = "segmentData";

    String SEGMENT_SECTION = "segmentSection";//多端来源

    String SEGMENT_WORK_FLAG = "isWork";

    String RECIPE_LIST_FLAG = "RecipeList";

    String DESCALING_FLAG = "除垢";

    int TIME_DELAYED = 50;

    String UNIT_TEMP = "°c";
    String UNIT_TIME_MIN = "min";
    String UNIT_TIME_H = "h";

    String RECIPE_MODEL_RULE = "recipe_model_rule";

    String CURVE_ID = "curve_id";

    String WORK_FROM = "work_from";

    String AUX_MODEL = "aux_model";
    String MAIN_MODEL = "aux_model";

    int APPOINT_CODE = 1005;

    String APPOINTMENT_RESULT = "appointment_result";

    String REMIND_BUS_CODE = "remind_bus_code";

    /**
     * 菜谱id
     */
    String WORK_MODE  = "work_mode";


    int WARING_CODE_2  = 2;//上温度传感器故障
    int WARING_CODE_3  = 3;//下温度传感器故障
    int WARING_CODE_4  = 4;//散热风机故障
    int WARING_CODE_5  = 5;//通讯故障
    int WARING_CODE_6  = 6;//水位传感器故障
    int WARING_CODE_7  = 7;//按键板温度传感器故障
    int WARING_CODE_8  = 8;//高温报警故障
    int WARING_CODE_9  = 9;//温度加热异常故障
    int WARING_CODE_10  = 10;//冷气阀故障
    int WARING_CODE_11  = 11;//缺水报警
    int WARING_CODE_14  = 14;//按键板温度过高
    int WARING_CODE_16  = 16;//变频板通讯故障
    int WARING_CODE_17  = 17;//磁控管温度传感器故障


}
