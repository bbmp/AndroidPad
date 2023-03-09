package com.robam.common.manager;

import com.robam.common.bean.DeviceErrorInfo;
import com.robam.common.utils.CurveUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.tencent.mmkv.MMKV;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 设备告警信息
 */
public class DeviceWarnInfoManager {


    public static final String ERROR_INFO = "error_info";

    private Map<String,DeviceErrorInfo> infoMap = new ConcurrentHashMap<>();
    private static final String RZKY = "RZKY";//一体机标识
    private static final String RZKY_928 = "CQ928";//一体机标识
    private boolean isRZKY = true;//是否只缓存一体机
    private boolean isRZKY_928 = true;//是否只缓存928


    private static class Holder {
        private static DeviceWarnInfoManager instance = new DeviceWarnInfoManager();
    }

    public static DeviceWarnInfoManager getInstance() {
        return DeviceWarnInfoManager.Holder.instance;
    }


    private DeviceWarnInfoManager(){
        try {
            initInfoList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取告警对象
     * @param deviceCategory 设置类型(如：KZNZ\RDKX)
     * @param deviceType 设备型号(如 CQ928\CQ925)
     * @param waringCode 告警ID
     * @return
     */
    public DeviceErrorInfo getDeviceErrorInfo(String deviceCategory, String deviceType, int waringCode) {
       return infoMap.get(deviceCategory+deviceType+waringCode);
    }



    private void initInfoList() throws JSONException {
        infoMap.clear();
        String deviceErrorInfo = getDeviceErrorInfo();
        if(StringUtils.isBlank(deviceErrorInfo)){
            return;
        }
        JSONObject infoObj = new JSONObject(deviceErrorInfo);
        for (Iterator<String> categoryKeys = infoObj.keys(); categoryKeys.hasNext(); ) {
            String categoryKey = categoryKeys.next();
            JSONObject typeObj = infoObj.optJSONObject(categoryKey);
            if(typeObj == null){
                continue;
            }
            if(isRZKY && !categoryKey.equals(RZKY)){//只解析一体机
                continue;
            }
            for (Iterator<String> typeKeys = typeObj.keys(); typeKeys.hasNext(); ) {
                String typeKey = typeKeys.next();
                JSONObject idObj = typeObj.optJSONObject(typeKey);
                if(isRZKY_928){
                    if(!RZKY_928.equals(typeKey)){
                        continue;
                    }
                }
                if(idObj == null){
                    continue;
                }
                for (Iterator<String> idKeys = idObj.keys(); idKeys.hasNext(); ) {
                    String idKey = idKeys.next();
                    if(idKey.matches("[0-9]+")){//集成类型暂不解析
                        JSONObject errorObj = idObj.optJSONObject(idKey);

                        DeviceErrorInfo errorInfo = new DeviceErrorInfo();
                        errorInfo.code = Integer.parseInt(idKey);
                        errorInfo.alertCode = errorObj.getString("alertCode");
                        errorInfo.alertDescr = errorObj.getString("alertDescr");
                        errorInfo.alertLevel = errorObj.getString("alertLevel");
                        errorInfo.alertName = errorObj.getString("alertName");
                        infoMap.put(categoryKey+typeKey+idKey,errorInfo);
                    }
                }
            }
        }
        //LogUtils.e("fdasf "+ infoMap.size());
    }


    /**
     * 下载设备告警信息内容
     * @param url
     */
    public void downFile(String url) {
        OkHttpClient client;
        try{
            client = new OkHttpClient();
            Request build = new Request.Builder().url(url).build();
            Call call = client.newCall(build);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtils.i("Thread downFile " + Thread.currentThread().getId());
                    String content = response.body().string();
                    saveDeviceErrorInfo(content);
                    if(StringUtils.isNotBlank(content)){
                        try {
                            initInfoList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }finally {

        }

    }

    private  void saveDeviceErrorInfo(String errorInfo){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.putString(ERROR_INFO,errorInfo);
    }

    private  String getDeviceErrorInfo(){
        return MMKV.defaultMMKV().getString(ERROR_INFO,null);
    }



}
