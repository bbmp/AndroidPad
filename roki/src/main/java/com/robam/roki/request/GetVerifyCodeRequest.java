package com.robam.roki.request;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class GetVerifyCodeRequest {
    private String phone;

    public GetVerifyCodeRequest(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetVerifyCodeRequest.class);
    }
}
