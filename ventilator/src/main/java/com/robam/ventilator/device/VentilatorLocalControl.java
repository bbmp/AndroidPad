package com.robam.ventilator.device;

import android.serialport.helper.SerialPortHelper;

import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.protocol.serial.SerialVentilator;

//烟机只有本地控制，串口通信
public class VentilatorLocalControl implements VentilatorFunction{
    @Override
    public void shutDown() {
        byte[] data = SerialVentilator.shutDown();
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void powerOn() {
        byte[] data = SerialVentilator.powerOn();
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void setFanStatus(int status) {
        byte byteStatus;
        if (status == VentilatorConstant.FAN_POWERON)
            byteStatus = (byte) 0x01;
        else
            byteStatus = (byte) 0x00;
        byte[] data = SerialVentilator.setFanStatus(byteStatus);
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void setFanGear(int gear) {
        byte byteGear;
        if (gear == VentilatorConstant.FAN_GEAR_WEAK)
            byteGear = (byte) 0xA1;
        else if (gear == VentilatorConstant.FAN_GEAR_MID)
            byteGear = (byte) 0xA2;
        else if (gear == VentilatorConstant.FAN_GEAR_FRY)
            byteGear = (byte) 0xA6;
        else
            byteGear = (byte) 0xA0;

        byte[] data = SerialVentilator.setGear(byteGear);
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void setFanLight(int light) {
        byte byteLight;
        if (light == VentilatorConstant.FAN_LIGHT_OPEN)
            byteLight = (byte) 0xA1;
        else
            byteLight = (byte) 0xA0;
        byte[] data = SerialVentilator.setLight(byteLight);
        SerialPortHelper.getInstance().addCommands(data);
    }

    @Override
    public void setFanAll(int gear, int light) {

    }

    @Override
    public void setSmart(int smart) {
        byte byteSmart;
        if (smart == VentilatorConstant.FAN_SMART_OPEN)
            byteSmart = (byte) 0x01;
        else
            byteSmart = (byte) 0x00;
        byte[] data = SerialVentilator.setSmart(byteSmart);
        SerialPortHelper.getInstance().addCommands(data);
    }
}
