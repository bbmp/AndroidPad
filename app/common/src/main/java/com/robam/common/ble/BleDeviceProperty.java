package com.robam.common.ble;

public class BleDeviceProperty {
    protected int    chan;

    protected boolean online;
    protected String guid;
    protected byte [] int_guid;
    protected String biz_id;
    protected int ble_type;

    protected int version;

    public BleDeviceProperty(int chan, String guid, byte[] int_guid, String biz_id, int ble_type) {
        this.chan = chan;
        this.guid = guid;
        this.int_guid = int_guid;
        this.biz_id = biz_id;
        this.ble_type = ble_type;
        online = false;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }

    public int getChan() {
        return chan;
    }

    public boolean isOnline() {
        return online;
    }

    public String getGuid() {
        return guid;
    }

    public void setInt_guid(byte[] int_guid) {
        this.int_guid = int_guid;
    }

    public byte[] getInt_guid() {
        return int_guid;
    }

    public void setBiz_id(String biz_id) {
        this.biz_id = biz_id;
    }

    public String getBiz_id() {
        return biz_id;
    }

    public void setBle_type(int ble_type) {
        this.ble_type = ble_type;
    }

    public int getBle_type() {
        return ble_type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
