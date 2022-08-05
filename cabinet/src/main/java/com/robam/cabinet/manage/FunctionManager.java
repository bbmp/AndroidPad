package com.robam.cabinet.manage;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.cabinet.bean.FunctionBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class FunctionManager {
    /**
     * 获取功能列表
     */
    public static List<FunctionBean> getFuntionList(Context context) {
        try {
            String jsonString = getFileString(context , "cabinet");
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<FunctionBean>>() {
            }.getType();
            List<FunctionBean> funtionBeans = new Gson().fromJson(jsonString, type);
            return funtionBeans;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取资产目录下面文件的字符串（Json）
     */
    private static String getFileString(Context context , String file) {
        try {
            int resId = context.getResources().getIdentifier(file, "raw", context.getPackageName());
            InputStream inputStream = context.getResources().openRawResource(resId);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inputStream.close();
            return outStream.toString();
        } catch (IOException e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }
}
