package com.robam.steamoven.utils;

import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DeviceWarnInfoManager;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.response.GetDeviceErrorRes;
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
        return RecipeManager.getInstance().getRecipeName(guid,recipeId);
//        String steamContent = getSteamContent(guid);
//        if(StringUtils.isNotBlank(steamContent)){
//            GetDeviceParamsRes getDeviceParamsRes = new Gson().fromJson(steamContent, GetDeviceParamsRes.class);
//            if (null == getDeviceParamsRes || null == getDeviceParamsRes.modelMap ||
//                    getDeviceParamsRes.modelMap.otherFunc == null ||
//                    getDeviceParamsRes.modelMap.otherFunc.deviceConfigurationFunctions == null){
//                return "";
//            }
//            List<DeviceConfigurationFunctions>  dFunctions = getDeviceParamsRes.modelMap.otherFunc.deviceConfigurationFunctions;
//            for (DeviceConfigurationFunctions itemFuc: dFunctions) {
//                if ("localCookbook".equals(itemFuc.functionCode)) {
//                    if (null == itemFuc.subView || null == itemFuc.subView.modelMap || itemFuc.subView.modelMap.subView== null ||
//                            itemFuc.subView.modelMap.subView.deviceConfigurationFunctions == null) {
//                        break;
//                    }
//                    List<DeviceConfigurationFunctions> functions = itemFuc.subView.modelMap.subView.deviceConfigurationFunctions;
//                    for (DeviceConfigurationFunctions itemF: functions) {
//                        if (!"ckno".equals(itemF.functionCode)){
//                            if(itemF.subView == null || itemF.subView.modelMap == null ||
//                                    itemF.subView.modelMap.subView == null ||
//                                    itemF.subView.modelMap.subView.deviceConfigurationFunctions == null){
//                                break;
//                            }
//                            List<DeviceConfigurationFunctions> innerFunks = itemF.subView.modelMap.subView.deviceConfigurationFunctions;
//                            for(DeviceConfigurationFunctions dfItem : innerFunks){
//                                if(recipeId == dfItem.id){
//                                    return dfItem.functionName;
//                                }
//                            }
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//        return "";
    }

    public static String getModelName(Device device){
        if(device == null || !(device instanceof SteamOven)){
            return "";
        }
        SteamOven steamOven  = (SteamOven) device;
        if(steamOven.recipeId != 0){
            return getRecipeData(DeviceUtils.getDeviceTypeId(device.guid),steamOven.recipeId);
        }
        return SteamModeEnum.match(steamOven.mode);
    }

    /**
     * 获取菜谱或者模式名称
     * @param guid 设备GUID
     * @param modeCode 模式code
     * @param recipeId  菜谱code
     * @return 菜谱或者模式名称，菜谱名称优先
     */
    public static String getModelName(String guid,int modeCode,int recipeId){
        if(recipeId != 0){
            return getRecipeData(DeviceUtils.getDeviceTypeId(guid),recipeId);
        }
        if(modeCode != 0){
            return SteamModeEnum.match(modeCode);
        }
        return "";

    }

    /**
     * 获取一体机数据
     */
    /**
     *
     * @param iLife
     * @param guidType 设备guid类型(如 CQ928)
     */
    public static void getSteamData(ILife iLife,String guidType) {
        //String deviceTypeId = DeviceUtils.getDeviceTypeId(guid);
        UserInfo info = AccountInfo.getInstance().getUser().getValue();
        CloudHelper.getDeviceParams(iLife, (info != null) ? info.id:0, guidType, IDeviceType.RZKY, GetDeviceParamsRes.class,
                new RetrofitCallback<GetDeviceParamsRes>() {
                    @Override
                    public void onSuccess(GetDeviceParamsRes getDeviceParamsRes) {
                        if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
                            //SteamDataUtil.saveSteam(guidType,new Gson().toJson(getDeviceParamsRes, GetDeviceParamsRes.class));
                            RecipeManager.getInstance().setRecipeInfo(guidType,getDeviceParamsRes);
                        }
                    }

                    @Override
                    public void onFaild(String err) {

                    }
                });
    }

    /**
     * 获取告警文件数据
     * @param iLife
     */
    public static void getDeviceErrorInfo(ILife iLife){
        CloudHelper.getDeviceErrorInfo(iLife,  GetDeviceErrorRes.class,
                new RetrofitCallback<GetDeviceErrorRes>() {
                    @Override
                    public void onSuccess(GetDeviceErrorRes deviceErrorRes) {
                        if(StringUtils.isNotBlank(deviceErrorRes.url)){//设备告警文件链接
                            DeviceWarnInfoManager.getInstance().downFile(deviceErrorRes.url);
                        }
                    }

                    @Override
                    public void onFaild(String err) {

                    }
                });
    }




}
