package com.robam.common.device;

import android.content.Context;

import com.robam.common.utils.LogUtils;
import com.topband.tbapi.TBManager;

public class TbangPlat implements IPlat{
    private TBManager tbManager;

    @Override
    public void init(Context context) {
        tbManager = new TBManager(context);
        tbManager.init();
    }

    @Override
    public void openDoorLamp() {

    }

    @Override
    public void closeDoorLamp() {

    }

    @Override
    public void openWaterLamp() {
        tbManager.setGpioDirection(6, 1, 0);
        tbManager.setGpio(6, 1);
    }

    @Override
    public void closeWaterLamp() {
        tbManager.setGpioDirection(6, 1, 0);
        tbManager.setGpio(6, 0);
    }

    @Override
    public void openPowerLamp() {
        tbManager.setGpioDirection(5, 1, 0);
        tbManager.setGpio(5, 1);
    }

    @Override
    public void closePowerLamp() {
        tbManager.setGpioDirection(5, 1, 0);
        tbManager.setGpio(5, 0);
    }

    @Override
    public String getDt() {
        return "5917S";
    }

    @Override
    public String getDeviceOnlySign() {
        return getDt() + getMac().replace(":" , "");
    }

    @Override
    public String getMac() {
        LogUtils.e("mac = " + tbManager.getWiFiMac().replace(":" , ""));
        return "121212121212";
    }

    @Override
    public void screenOn() {
//       tbManager.screenOn();
        tbManager.setBackLight(true);
    }

    @Override
    public void screenOff() {
//        tbManager.screenOff();
        tbManager.setBackLight(false);
    }
}
