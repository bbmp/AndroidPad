package com.robam.cabinet.constant;

import com.robam.cabinet.bean.CabModeBean;

import java.util.ArrayList;
import java.util.List;

public enum CabinetModeEnum {
    MODE_DISINFECT(CabinetConstant.MODE_DISINFECT, "消毒", 40, 40, 60, 5),
    MODE_CLEAN(CabinetConstant.MODE_CLEAN, "快洁", 3, 3, 15, 1),
    MODE_DRY(CabinetConstant.MODE_DRY, "烘干", 20, 20, 40, 1),
    MODE_FLUSH(CabinetConstant.MODE_FLUSH, "净存", 20, 20, 30, 5),
    MODE_SMART(CabinetConstant.MODE_SMART, "智能", 45, 45, 45, 1),
    ;
    //模式
    public int code;
    /**
     * 模式名
     */
    public String name;
    /**
     * 模式时间
     */
    public int defTime;
    /**
     * 最小时间
     */
    public int minTime;
    /**
     * 最大时间
     */
    public int maxTime;

    public int stepTime;

    CabinetModeEnum(int code, String name, int defTime, int minTime, int maxTime, int stepTime) {
        this.code = code;
        this.name = name;
        this.defTime = defTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.stepTime = stepTime;
    }

    /**
     * 将枚举值转化成list集合
     *
     * @return
     */
    public static List<CabModeBean> getModeList(){
        List<CabModeBean> list = new ArrayList<>();
        for (CabinetModeEnum bean: CabinetModeEnum.values()) {
            list.add(new CabModeBean(bean.code, bean.name, bean.defTime, bean.minTime, bean.maxTime, bean.stepTime));
        }
        return list;
    }

}
