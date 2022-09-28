package com.robam.common.bean;

import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.data.BleDevice;
import com.robam.common.ble.BleDecoder;

import java.util.ArrayList;
import java.util.List;

public class AccountInfo {
    private static AccountInfo instance;

    private MutableLiveData<Boolean> isLogin = new MutableLiveData<>(false);
    private MutableLiveData<UserInfo> user = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> connect = new MutableLiveData<>(false); //联网情况
    private MutableLiveData<String> guid = new MutableLiveData<>(""); //设备状态变化
    private AccountInfo() {}

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

    //    private final RxLiveData<UserInfo> userData = new RxLiveData<>();
//
//    public RxLiveData<UserInfo> getUserData() {
//        return userData;
//    }

    public BleDecoder getBleDecoder(String mac) {
        for (Device device: deviceList) {
            if (null != mac && mac.equals(device.mac)) {
                return device.bleDecoder;
            }
        }
        return null;
    }
}
