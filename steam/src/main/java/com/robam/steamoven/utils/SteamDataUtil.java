package com.robam.steamoven.utils;

import com.google.gson.Gson;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.OtherFunc;
import com.robam.steamoven.bean.SubViewModelMapSubView;
import com.robam.steamoven.response.GetDeviceParamsRes;
import com.tencent.mmkv.MMKV;

import java.util.List;

public class SteamDataUtil {

    public static final String KEY_STEAM = "key_steam";

    public static void saveSteam(String guid,String content){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.putString(guid,content);
    }

    public static String getSteamContent(String guid){
        return MMKV.defaultMMKV().getString(guid,null);
    }

    /**
     * 获取菜名称
     * @param recipeId
     * @return
     */
    public static String getRecipeData(String guid,long recipeId){
        String steamContent = getSteamContent(guid);
        if(StringUtils.isNotBlank(steamContent)){
            GetDeviceParamsRes getDeviceParamsRes = new Gson().fromJson(steamContent, GetDeviceParamsRes.class);
            if (null == getDeviceParamsRes || null == getDeviceParamsRes.modelMap ||
                    getDeviceParamsRes.modelMap.otherFunc == null ||
                    getDeviceParamsRes.modelMap.otherFunc.deviceConfigurationFunctions == null){
                return "";
            }
            List<DeviceConfigurationFunctions>  dFunctions = getDeviceParamsRes.modelMap.otherFunc.deviceConfigurationFunctions;
            for (DeviceConfigurationFunctions itemFuc: dFunctions) {
                if ("localCookbook".equals(itemFuc.functionCode)) {
                    if (null == itemFuc.subView || null == itemFuc.subView.modelMap || itemFuc.subView.modelMap.subView== null ||
                            itemFuc.subView.modelMap.subView.deviceConfigurationFunctions == null) {
                        break;
                    }
                    List<DeviceConfigurationFunctions> functions = itemFuc.subView.modelMap.subView.deviceConfigurationFunctions;
                    for (DeviceConfigurationFunctions itemF: functions) {
                        if (!"ckno".equals(itemF.functionCode)){
                            if(itemF.subView == null || itemF.subView.modelMap == null ||
                                    itemF.subView.modelMap.subView == null ||
                                    itemF.subView.modelMap.subView.deviceConfigurationFunctions == null){
                                break;
                            }
                            List<DeviceConfigurationFunctions> innerFunks = itemF.subView.modelMap.subView.deviceConfigurationFunctions;
                            for(DeviceConfigurationFunctions dfItem : innerFunks){
                                if(recipeId == dfItem.id){
                                    return dfItem.functionName;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return "";
    }


}
