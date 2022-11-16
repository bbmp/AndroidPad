package com.robam.cabinet.bean;

public class WorkModeBean extends CabModeBean{

    public WorkModeBean(int code, String name, int defTime, int minTime, int maxTime, int stepTime) {
        super(code, name, defTime, minTime, maxTime, stepTime);
    }

    public WorkModeBean(CabModeBean cabModeBean) {
        super(cabModeBean.code, cabModeBean.name, cabModeBean.defTime, cabModeBean.minTime, cabModeBean.maxTime, cabModeBean.stepTime);
    }

    public WorkModeBean(int code,int orderSurplusTime,int modelSurplusTime){
        super(code,"",0,0,0,0);
        this.orderSurplusTime = orderSurplusTime;
        this.modelSurplusTime = modelSurplusTime;
    }


    public int orderSurplusTime;//预约时间
    public int modelSurplusTime;//模式剩余运行时间

}
