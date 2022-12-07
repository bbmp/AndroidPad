package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class LinkageConfigReq {
    public String deviceGuid;
    public boolean doorOpenEnabled;
    public boolean enabled;
    public String targetGuid;
    public String targetDeviceName ;

    public LinkageConfigReq(String deviceGuid, boolean doorOpenEnabled, boolean enabled, String targetGuid, String targetDeviceName) {
        this.deviceGuid = deviceGuid;
        this.doorOpenEnabled = doorOpenEnabled;
        this.enabled = enabled;
        this.targetGuid = targetGuid;
        this.targetDeviceName = targetDeviceName;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, LinkageConfigReq.class);
    }
}
