package com.robam.common.bean;

import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.data.BleDevice;
import com.robam.common.IDeviceType;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;

import org.eclipse.paho.client.mqttv3.util.Strings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountInfo {
    private static AccountInfo instance;

    private MutableLiveData<Boolean> isLogin = new MutableLiveData<>(false);
    private MutableLiveData<UserInfo> user = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> connect = new MutableLiveData<>(false); //联网情况
    private MutableLiveData<String> guid = new MutableLiveData<>(""); //设备状态变化
    private MutableLiveData<Boolean> wifi = new MutableLiveData<>(false);//wifi列表变化
    private AccountInfo() {}

    public String topGuid = Plat.getPlatform().getDeviceOnlySign();//当前设备
    //设备列表
    public List<Device> deviceList = new ArrayList<>();

    private static class Holder {
        private static AccountInfo instance = new AccountInfo();
    }

    public static AccountInfo getInstance() {
        return Holder.instance;
    }

    public MutableLiveData<Boolean> getIsLogin() {
        return isLogin;
    }

    public MutableLiveData<Boolean> getConnect() {
        return connect;
    }

    public MutableLiveData<UserInfo> getUser() {
        return user;
    }

    public MutableLiveData<String> getGuid() {
        return guid;
    }

    public MutableLiveData<Boolean> getWifi() {
        return wifi;
    }
    //    private final RxLiveData<UserInfo> userData = new RxLiveData<>();
//
//    public RxLiveData<UserInfo> getUserData() {
//        return userData;
//    }

    public boolean isExist(List<Device> devices, Device device) {
        if (null != device && null != devices) {
            for (Device device1 : devices) {
                if (null != device1.guid && device1.guid.equals(device.guid))
                    return true;
            }
        }
        return false;
    }

    public String getUserString() {
        UserInfo userInfo = user.getValue();
        long id = (userInfo != null) ? userInfo.id : 0;
        String userId = String.valueOf(id);
        while (userId.length() < 10)
            userId = userId + "0";
        return userId;
    }
}
