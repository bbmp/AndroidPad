package com.robam.ventilator.bean;

public class Device {
    //在线状态
    private int status;
    //设备名称
    private String name;
    //设备型号
    private String model;
    //工作状态
    private int workStatus;

    public Device() {
    }

    public Device(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public int getWorkStatus() {
        return workStatus;
    }
}
