package com.robam.common.bean;

public class LineChartDataBean {
    public float xValue;
    public float yValue;
    public boolean isBigPoint;
    public String stepName;
    public int trendType;//与上一个点对比的趋势  1上升  2平衡  3下降  -1第一个点
    public int power;
    public int gear;
    public int type;//临时趋势   锅控制方式使用这个字段 1上升  3平衡  2下降

    public LineChartDataBean() {

    }
    public LineChartDataBean(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }
    public LineChartDataBean(float xValue, float yValue,int power,int gear) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.power = power;
        this.gear = gear;
    }
    public LineChartDataBean(float xValue, String stepName) {
        this.xValue = xValue;
        this.stepName = stepName;
    }
}
