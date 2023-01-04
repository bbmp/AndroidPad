package com.robam.steamoven.utils;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

public class ModelUtil {

    public static final String mode_category = "mode_category";
    public static final String mode_his= "mode_his";
    public static void saveModelCategoryRecord(int funCode,int modeCode,int stream,int temp,int downTemp,int time){
        ModelRecord modelRecord = new ModelRecord(funCode,modeCode, stream, temp, downTemp, time);
        MMKV.defaultMMKV().putString(mode_category+funCode,modelRecord.toString());
    }

    public static ModelRecord getModelCategoryRecord(int funCode){
        String string = MMKV.defaultMMKV().getString(mode_category + funCode, null);
        if(string != null){
            return  new Gson().fromJson(string,ModelRecord.class);
        }
        return null;
    }

    public static void saveModelRecord(int funCode,int modeCode,int stream,int temp,int downTemp,int time){
        ModelRecord modelRecord = new ModelRecord(funCode,modeCode, stream, temp, downTemp, time);
        MMKV.defaultMMKV().putString(mode_his+modeCode,modelRecord.toString());
    }

    public static ModelRecord getModelRecord(int modeCode){
        String string = MMKV.defaultMMKV().getString(mode_his + modeCode, null);
        if(string != null){
            return  new Gson().fromJson(string,ModelRecord.class);
        }
        return null;
    }






    public static class ModelRecord{
        public int modeCode;
        public int funCode;
        public int stream;
        public int temp;
        public int downTemp;
        public int time;

        public ModelRecord(int funCode,int modeCode,int stream,int temp,int downTemp,int time){
            this.modeCode = modeCode;
            this.funCode = funCode;
            this.stream = stream;
            this.temp = temp;
            this.downTemp = downTemp;
            this.time = time;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this, ModelRecord.class);
        }
    }

    public static class ModelIndex{
        public int modeIndex = -1;
        public int steamIndex = -1;
        public int tempIndex = -1;
        public int downTempIndex = -1;
        public int timeIndex = -1;
    }

}
