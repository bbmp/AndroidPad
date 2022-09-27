package com.robam.ventilator.constant;

//烟机属性定义
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
     * 工作状态
     */
    short workState = 3;
    /**
     * 工作控制
     */
    short workCtrl = 4;  //0停止 1启动
    /**
     * 灯 控制
     */
    short lightCtrl = 5;
    /**
     * 是否需要清洗
     */
    short cleanStatus = 6;
    /**
     * 联网状态
     */
    short netStatus = 7;
}
