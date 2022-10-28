package com.robam.ventilator.module;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.serialport.helper.SerialPortHelper;

import com.robam.common.module.IPublicVentilatorApi;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.activity.LoginPhoneActivity;
import com.robam.ventilator.ui.activity.MatchNetworkActivity;

public class PublicVentilatorApi implements IPublicVentilatorApi {
    //给外部模块调用
    @Override
    public void setFanGear(int gear) {
        VentilatorAbstractControl.getInstance().setFanGear(gear);
    }

    @Override
    public int getFanGear() {
        return HomeVentilator.getInstance().gear & 0xFF;
    }

    @Override
    public void setFanLight(int lightOn) {
        VentilatorAbstractControl.getInstance().setFanLight(lightOn);
    }

    @Override
    public int getFanLight() {
        if (HomeVentilator.getInstance().lightOn == (byte) 0xA1)
            return VentilatorConstant.FAN_LIGHT_OPEN;
        else
            return VentilatorConstant.FAN_LIGHT_CLOSE;
    }

    @Override
    public void startMatchNetwork(Context context, String model) {
        Intent intent = new Intent();
        intent.putExtra(VentilatorConstant.EXTRA_MODEL, model);
        intent.setClass(context, MatchNetworkActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void startLogin(Context context) {
        //调用手机登录
        context.startActivity(new Intent(context, LoginPhoneActivity.class));
    }

    @Override
    public void queryAttribute() {
        VentilatorAbstractControl.getInstance().queryAttribute(); //查询状态
    }

    @Override
    public boolean isStartUp() {
        return (HomeVentilator.getInstance().startup == 0x01) ? true:false;
    }

    @Override
    public void shutDown() {
        VentilatorAbstractControl.getInstance().shutDown();
    }

    @Override
    public void powerOn() {
        VentilatorAbstractControl.getInstance().powerOn();
    }

    @Override
    public void setColorLamp() {
        VentilatorAbstractControl.getInstance().setColorLamp();
    }
}
