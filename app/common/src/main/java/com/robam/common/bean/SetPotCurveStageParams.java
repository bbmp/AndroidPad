package com.robam.common.bean;

public class SetPotCurveStageParams {
    public int time;
    public int temp;
    public int control;
    public int gear;

    public SetPotCurveStageParams(int time, int temp, int control, int gear) {
        this.time = time;
        this.temp = temp;
        this.control = control;
        this.gear = gear;
    }

    public SetPotCurveStageParams(Integer time, Integer temp, Integer control ) {
        this.time = time;
        this.temp = temp;
        this.control = control;
    }
}
