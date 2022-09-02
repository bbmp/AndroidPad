package com.robam.common.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.robam.common.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FunctionManager {
    /**
     * 获取功能列表
     */
    public static <T> List<T> getFuntionList(Context context, Class<T> cls, int resId) {
        try {
            LogUtils.e("getFuntionList " + cls.getName());
            String jsonString = getFileString(context , resId);
            if (jsonString == null) {
                return null;
            }
            List<T> funtionList = new ArrayList<>();
            Gson gson = new Gson();
            JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                funtionList.add(gson.fromJson(jsonElement,cls));
            }
            LogUtils.e("getFuntionList return " + cls.getName());
            return funtionList;
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return null;
    }

    /**
     * 获取资产目录下面文件的字符串（Json）
     */
    private static String getFileString(Context context , int resId) {
        try {
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
