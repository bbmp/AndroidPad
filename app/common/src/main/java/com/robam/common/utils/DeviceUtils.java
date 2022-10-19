package com.robam.common.utils;

import com.robam.common.IDeviceType;

public class DeviceUtils {
    public final static int VENDOR_LENGTH = 0;
    public final static int DEVICE_TYPE_LENGTH = 5;
    public final static int DEVICE_NUMBER_LENGTH = 12;
    public final static int GUID_LENGTH = VENDOR_LENGTH + DEVICE_TYPE_LENGTH + DEVICE_NUMBER_LENGTH;

    //设备类型
    public static String getDeviceTypeId(String guid) {

        int venderLen = VENDOR_LENGTH;
        if (guid.length() == GUID_LENGTH) {
            venderLen = 0;
        }
        return guid.substring(venderLen, venderLen + DEVICE_TYPE_LENGTH);
    }
    //厂商
    public static String getVendor(String guid) {
        int venderLen = VENDOR_LENGTH;
        if (guid.length() == GUID_LENGTH) {
            venderLen = 0;
        }
        return guid.substring(0, venderLen);
    }
    public static String getDeviceNumber(String guid) {
        int venderLen = VENDOR_LENGTH;
        if (null == guid)
            return "";
        if (guid.length() == GUID_LENGTH) {
            venderLen = 0;
        }
        return guid.substring(venderLen + DEVICE_TYPE_LENGTH);

    }


    public static boolean isStove(String guid) {//判断是否为灶具
        if (IDeviceType.RRQZ.equals(getDeviceTypeId(guid)))
            return true;
        return false;
    }
}
