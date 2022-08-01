package com.robam.steam.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.steam.bean.FuntionBean;
import com.robam.steam.bean.FuntionList;
import com.robam.steam.bean.ModeBean;
import com.robam.steam.bean.RecipeClassify;
import com.robam.steam.bean.RecipeClassifyMode;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FuntionModeManage {
    /**
     * 获取功能列表
     */
    public static List<FuntionBean> getFuntionList(Context context) {
        try {
            String jsonString = getFileString(context , "funtion");
            if (jsonString == null) {
                return null;
            }

            FuntionList funtionList = new Gson().fromJson(jsonString, FuntionList.class);
            return funtionList.getData();
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取功能下模式列表（蒸）
     */
    public static List<ModeBean> getSteamMode(Context context) {
        try {
            String jsonString = getFileString(context , "steam");
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<ModeBean>>() {
            }.getType();
//            List<ModeBean> modeBeans = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return modeBeans;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取功能下模式列表（烤）
     */
    public static List<ModeBean> getOvenMode(Context context) {
        try {
            String jsonString = getFileString(context , "oven");
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<ModeBean>>() {
            }.getType();
//            List<ModeBean> modeBeans = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return modeBeans;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取功能下模式列表（辅助）
     */
    public static List<ModeBean> getAuxMode(Context context) {
        try {
            String jsonString = getFileString(context , "aux");
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<ModeBean>>() {
            }.getType();
//            List<ModeBean> modeBeans = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return modeBeans;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取本地mode数据 , 根据fution里面携带的mode字符串匹配资源
     * @param context
     * @param mode
     * @return
     */
    public static List<ModeBean> getMode(Context context ,String mode) {
        try {
            String jsonString = getFileString(context , mode);
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<ModeBean>>() {
            }.getType();
//            List<ModeBean> modeBeans = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return modeBeans;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取本地菜谱数据 , 根据fution里面携带的mode字符串匹配资源
     * @param context
     * @param mode
     * @return
     */
    public static List<RecipeClassify> getRecipeClassify(Context context , String mode) {
        try {
            String jsonString = getFileString(context , mode);
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<RecipeClassify>>() {
            }.getType();
//            List<RecipeClassify> recipeClassifies = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return recipeClassifies;
        } catch (Exception e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取本地菜谱数据 , 根据fution里面携带的mode字符串匹配资源
     * @param context
     * @param mode
     * @return
     */
    public static List<RecipeClassifyMode> getRecipeClassBean(Context context , String mode) {
        try {
            String jsonString = getFileString(context , mode);
            if (jsonString == null) {
                return null;
            }
            Type type = new TypeToken<List<RecipeClassifyMode>>() {
            }.getType();
//            List<RecipeClassifyMode> recipeClassifies = GsonFactory.getSingletonGson().fromJson(jsonString, type);
//            return recipeClassifies;
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


    /**
     * 获取资产目录下面文件的字符串（Json）
     */
    private static JSONArray getFileJson(Context context , String file) {
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
            return new JSONArray(outStream.toString());
        } catch (IOException | JSONException e) {
//            CrashReport.postCatchedException(e);
        }
        return null;
    }

    /**
     * 获取mode下面时间范围
     * @param modeBean
     * @return
     */
    public static List<Integer> getTempData(ModeBean modeBean){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = modeBean.minTemp ; i <= modeBean.maxTemp ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }

    /**
     * 获取温度范围
     * @param modeBean
     * @return
     */
    public static List<Integer> getTimeData(ModeBean modeBean){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = modeBean.minTime ; i <= modeBean.maxTime ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }
}
