package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class BindDeviceReq extends GetUserReq{

    /** guid */
    private String guid;
    /** 设备名称 */
    private String name;
    /** 验证码 */
    private boolean isOwner;

    public BindDeviceReq(long userId, String guid, String name, boolean isOwner) {
        super(userId);
        this.guid = guid;
        this.name = name;
        this.isOwner = isOwner;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, BindDeviceReq.class);
    }
}
