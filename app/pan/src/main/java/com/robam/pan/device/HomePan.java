package com.robam.pan.device;


import androidx.lifecycle.MutableLiveData;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;

public class HomePan {
    //当前进入的锅首页
    public static HomePan getInstance() {
        return HomePan.PanHolder.instance;
    }

    private static class PanHolder {
        private static final HomePan instance = new HomePan();
    }

    //当前guid
    public String guid;
    //锅温度
    public MutableLiveData<Integer> panTemp = new MutableLiveData<Integer>(0);

    public boolean isPanOffline() {
        for (Device device : AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device.status == Device.ONLINE) {

                return false;
            }
        }
        return true;
    }
}
