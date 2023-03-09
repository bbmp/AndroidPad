package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class LoginQrcodeReq {
    private String account;
    private String pwd;
    private String key;
    private String appType;
    private String loginType;
    private String access_token;

    public LoginQrcodeReq(String access_token) {
        this.access_token = access_token;
    }

    public LoginQrcodeReq(String account, String pwd) {
        this.account = account;
        this.pwd = pwd;
    }

    public LoginQrcodeReq(String key, String appType, String loginType) {
        this.key = key;
        this.appType = appType;
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, LoginQrcodeReq.class);
    }
}
