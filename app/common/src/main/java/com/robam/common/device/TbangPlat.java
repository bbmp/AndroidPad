package com.robam.common.device;

import android.content.Context;

public class TbangPlat implements IPlat{
    @Override
    public void init(Context context) {

    }

    @Override
    public void openDoorLamp() {

    }

    @Override
    public void closeDoorLamp() {

    }

    @Override
    public void openWaterLamp() {

    }

    @Override
    public void closeWaterLamp() {

    }

    @Override
    public void openPowerLamp() {

    }

    @Override
    public void closePowerLamp() {

    }

    @Override
    public String getDt() {
        return "DB620";
    }

    @Override
    public String getDeviceOnlySign() {
        return getDt() + getMac().replace(":" , "");
    }

    @Override
    public String getMac() {
        return "121212121212";
    }
}
