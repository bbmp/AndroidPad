package com.robam.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.robam.common.IDeviceType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeviceUtils {
    public final static int VENDOR_LENGTH = 0;
    public final static int DEVICE_TYPE_LENGTH = 5;
    public final static int DEVICE_NUMBER_LENGTH = 12;
    public final static int GUID_LENGTH = VENDOR_LENGTH + DEVICE_TYPE_LENGTH + DEVICE_NUMBER_LENGTH;

    //设备类型
    public static String getDeviceTypeId(String guid) {
        if (null == guid)
            return "";
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
    //设备序列号
    public static String getDeviceSerial() {
        String serial = "unknown";
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            serial = (String)method.invoke(new Build(), "ro.serialno");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serial;
    }
    //获取包版本
    public static String getVersionName(Context context){
//获取包管理器
        PackageManager pm = context.getPackageManager();
//获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
//返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
}
