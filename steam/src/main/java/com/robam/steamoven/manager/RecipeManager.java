package com.robam.steamoven.manager;


import android.util.Log;

import com.google.gson.Gson;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.OtherFunc;
import com.robam.steamoven.bean.SubViewModelMapSubView;
import com.robam.steamoven.response.GetDeviceParamsRes;
import com.robam.steamoven.utils.SteamDataUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeManager {
    private static class Holder {
        private static RecipeManager instance = new RecipeManager();
    }

    public static RecipeManager getInstance() {
        return RecipeManager.Holder.instance;
    }

    private Map<String,Map<Long,String>> recipeName = new ConcurrentHashMap<>();

    public void setRecipeInfo(String guidType,GetDeviceParamsRes getDeviceParamsRes){
        SteamDataUtil.saveSteam(guidType,new Gson().toJson(getDeviceParamsRes, GetDeviceParamsRes.class));
        Map<Long, String> integerStringMap = recipeName.get(guidType);
        if(integerStringMap == null){
            recipeName.put(guidType,new HashMap<>());
        }
        initRecipeInfo(getDeviceParamsRes,recipeName.get(guidType));
    }
    private void initRecipeInfo(GetDeviceParamsRes getDeviceParamsRes,Map<Long,String> map){
        if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
            setRecipeData(getDeviceParamsRes,map);
        }
    }

    private void setRecipeData(GetDeviceParamsRes getDeviceParamsRes,Map<Long,String> map) {
        List<DeviceConfigurationFunctions> deviceConfigurationFunctionsList = new ArrayList<>();
        OtherFunc otherFunc = getDeviceParamsRes.modelMap.otherFunc;
        if (null != otherFunc && null != otherFunc.deviceConfigurationFunctions) {
            for (DeviceConfigurationFunctions deviceConfigurationFunctions: otherFunc.deviceConfigurationFunctions) {
                if ("localCookbook".equals(deviceConfigurationFunctions.functionCode)) {
                    if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                        SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                        if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                            for (DeviceConfigurationFunctions deviceConfigurationFunctions1: subViewModelMapSubView.deviceConfigurationFunctions) {
                                if (!"ckno".equals(deviceConfigurationFunctions1.functionCode))
                                    deviceConfigurationFunctionsList.add(deviceConfigurationFunctions1);
                            }
                        }
                    }
                    break;
                }
            }
        }

        //添加分类
        for (int i =0; i<deviceConfigurationFunctionsList.size(); i++) {
            DeviceConfigurationFunctions deviceConfigurationFunctions = deviceConfigurationFunctionsList.get(i);
            if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                    for (DeviceConfigurationFunctions recepItem : subViewModelMapSubView.deviceConfigurationFunctions) {
                        String functionParams = recepItem.functionParams;
                        if(StringUtils.isNotBlank(functionParams)){
                            try {
                                JSONObject object = new JSONObject(functionParams);
                                JSONObject model = object.getJSONObject("model");
                                String value = model.getString("value");
                                map.put(Long.parseLong(value),recepItem.functionName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            map.put(recepItem.id,recepItem.functionName);
                        }
                    }
                }
            }
        }
    }

    public String getRecipeName(String guidType,long recipeId){
        if(recipeId == 0){
            return "";
        }
        Map<Long, String> longStringMap = recipeName.get(guidType);
        if(longStringMap == null){
            return "";
        }
        String recipeName = longStringMap.get(recipeId);
        return StringUtils.isBlank(recipeName) ? "" : recipeName;
    }


}
