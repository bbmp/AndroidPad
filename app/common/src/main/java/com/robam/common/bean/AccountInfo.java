package com.robam.common.bean;

import androidx.lifecycle.MutableLiveData;

public class AccountInfo {
    private static AccountInfo instance;

    private MutableLiveData<Boolean> isLogin = new MutableLiveData<>(false);
    private MutableLiveData<UserInfo> user = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> connect = new MutableLiveData<>(false); //联网情况
    private AccountInfo() {}

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
//    private final RxLiveData<UserInfo> userData = new RxLiveData<>();
//
//    public RxLiveData<UserInfo> getUserData() {
//        return userData;
//    }
}
