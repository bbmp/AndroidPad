package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

//获取验证码请求
public class GetVerifyCodeReq {
    private String phone;

    public GetVerifyCodeReq(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetVerifyCodeReq.class);
    }
}
