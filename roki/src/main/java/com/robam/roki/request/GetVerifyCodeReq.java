package com.robam.roki.request;

import com.google.gson.GsonBuilder;

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
