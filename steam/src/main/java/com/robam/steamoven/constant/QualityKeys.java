package com.robam.steamoven.constant;

public interface QualityKeys {
    /**
     * 电源状态
     */
    short powerState = 1;
    /**
     * 电源控制
     */
    short powerCtrl = 2;
    /**
     * 电源状态
     */
    short workState = 3;
    /**
     * 工作控制
     */
    short workCtrl = 4;
    /**
     * 设置预约时间
     */
    short setOrderSecs = 5;
    /**
     * 设置预约时间
     */
    short orderLeftSecs = 6;
    /**
     * 故障码
     */
    short faultCode = 7;


    /**
     * 灯开关
     */
    short lightSwitch = 8;
    /**
     * 旋转烤开关
     */
    short rotateSwitch = 9;
    /**
     * 水箱状态
     */
    short waterBoxState = 10;
    /**
     * 水箱控制
     */
    short waterBoxCtrl = 11;
    /**
     * 水位状态
     */
    short waterLevelState = 12;
    /**
     * 门状态
     */
    short doorState = 13;
    /**
     * 门控制开关
     */
    short doorSwitch = 14;
    /**
     * 手动加蒸汽工作状态
     */
    short steamState = 15;
    /**
     * 手动加蒸汽控制
     */
    short steamCtrl = 16;
    /**
     * 菜谱编号
     */
    short recipeId = 17;

    /**
     * 菜谱设置总时间
     */
    short recipeSetSecs = 18;
    /**
     * 当前上管温度
     */
    short curTemp = 19;
    /**
     * 当前下管温度（EXP）
     */
    short curTemp2 = 20;
    /**
     * 总剩余时间
     */
    short totalRemainSeconds = 21;
    /**
     * 请求除垢标志
     */
    short descaleFlag = 22;
    /**
     * 当前蒸模式累计工作时间
     */
    short curSteamTotalHours = 23;
    /**
     * 蒸模式累计需要除垢时间
     */
    short curSteamTotalNeedHours = 24;
    /**
     * 实际运行时间
     */
    short cookedTime = 25;
    /**
     * 除垢当前段数
     */
    short descaleIndex = 26;
    /**
     * 除垢模式总段数
     */
    short descaleNum = 27;

    /**
     * 废水箱状态（1- 关闭；1 - 打开）
     */
    short wasteWaterBox = 35;

    /**
     * 废水箱水位（0表示正常,1表示水满）
     */
    short wasteWaterLevel = 36;

    /**
     * 当前工作段数
     */
    short curSectionNbr = 99;
    /**
     * 设置多段总段数 0 表示普通专业模式
     */
    short sectionNumber = 100;

    /**
     * 设置专业模式（首段）
     */
    short mode = 101;

    /**
     * 首段上温度
     */
    short setUpTemp = 102;

    /**
     * 首段下温度
     */
    short setDownTemp = 103;

    /**
     * 首段设置时间
     */
    short setTime = 104;

    /**
     * 首段剩余时间
     */
    short restTime = 105;

    /**
     * 蒸汽量
     */
    short steam = 106;

    /**
     * 设置专业模式（第二段）
     */
    short mode2 = 111;

    /**
     * 首段上温度
     */
    short setUpTemp2 = 112;

    /**
     * 首段下温度
     */
    short setDownTemp2 = 113;

    /**
     * 首段设置时间
     */
    short setTime2 = 114;

    /**
     * 首段剩余时间
     */
    short restTime2 = 115;

    /**
     * 蒸汽量
     */
    short steam2 = 116;

    /**
     * 设置专业模式（第三段）
     */
    short mode3 = 121;

    /**
     * 首段上温度
     */
    short setUpTemp3 = 122;

    /**
     * 首段下温度
     */
    short setDownTemp3 = 123;

    /**
     * 首段设置时间
     */
    short setTime3 = 124;

    /**
     * 首段剩余时间
     */
    short restTime3 = 125;

    /**
     * 蒸汽量
     */
    short steam3 = 126;

}
