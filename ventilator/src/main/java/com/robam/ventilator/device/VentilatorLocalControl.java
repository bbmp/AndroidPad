package com.robam.ventilator.device;

import android.serialport.helper.SerialPortHelper;

import com.robam.ventilator.protocol.serial.SerialVentilator;

//烟机只有本地控制，串口通信
public class VentilatorLocalControl implements VentilatorFunction{
    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {
        byte[] data = SerialVentilator.powerOn();
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void setFanStatus(int status) {

    }

    @Override
    public void setFanGear(int gear) {

    }

    @Override
    public void setFanLight(int light) {

    }

    @Override
    public void setFanAll(int gear, int light) {

    }
}
