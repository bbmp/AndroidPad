package com.robam.ventilator.module;

import android.content.Context;
import android.content.Intent;
import android.serialport.helper.SerialPortHelper;

import com.robam.common.module.IPublicVentilatorApi;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.protocol.serial.SerialVentilator;
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
    public void startMatchNetwork(Context context, String model) {
        Intent intent = new Intent();
        intent.putExtra(VentilatorConstant.EXTRA_MODEL, model);
        intent.setClass(context, MatchNetworkActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void queryAttribute() {
        SerialPortHelper.getInstance().addCommands(SerialVentilator.packQueryCmd()); //查询状态
    }
}
