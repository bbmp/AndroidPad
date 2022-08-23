package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class LoginQrcodeReq {
    private String account;
    private String pwd;

    public LoginQrcodeReq(String account, String pwd) {
        this.account = account;
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, LoginQrcodeReq.class);
    }
}
