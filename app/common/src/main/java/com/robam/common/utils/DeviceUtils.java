package com.robam.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.robam.common.IDeviceType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

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
    //检查包名安装
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {

        }
        if (null == packageInfo)
            return false;
        return true;
    }
    /**
     * 拷贝assets文件
     */
    public static boolean copyFileIfNeed(Context context, String srcFilePath, String desFilePath) {

        File desFile = new File(desFilePath);
        try {
            if (desFile.exists()) {
                desFile.delete();
            }

            desFile.createNewFile();
            InputStream in = context.getApplicationContext().getAssets().open(srcFilePath);
            if (in == null) {

                return false;
            }

            OutputStream out = new FileOutputStream(desFile);
            byte[] buffer = new byte[4096];
            int n;
            int sum = 0;
            while ((n = in.read(buffer)) > 0) {
                sum += n;

                out.write(buffer, 0, n);
                out.flush();
            }

            in.close();
            in = null;
            out.close();
            out = null;
        } catch (IOException e) {

            desFile.delete();

            return false;
        }

        return true;
    }
    //静默安装
    public static boolean installSilent(String path) {
        boolean result = false;
        BufferedReader es = null;
        DataOutputStream os = null;

        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            String command = "pm install -r " + path + "\n";
            os.write(command.getBytes(Charset.forName("utf-8")));
            os.flush();
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();
            es = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = es.readLine()) != null) {
                builder.append(line);
            }

        /* Installation is considered a Failure if the result contains
            the Failure character, or a success if it is not.
             */
            if (!builder.toString().contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {

        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (es != null) {
                    es.close();
                }
            } catch (IOException e) {

            }
        }

        return result;
    }
}
