package com.robam.common.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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
    public static <T> List<T> getFuntionList(Context context, Class<T> cls, String fileName) {
        List<T> funtionList = new ArrayList<>();
        try {
            String jsonString = getFileString(context , fileName);
            if (jsonString == null) {
                return null;
            }
            JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
            Gson gson = new Gson();
            for (JsonElement jsonElement : jsonArray) {
                funtionList.add(gson.fromJson(jsonElement,cls));
            }
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return funtionList;
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
