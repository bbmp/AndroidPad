package com.robam.roki.utils;

import com.robam.roki.bean.Dc;
import com.robam.roki.constant.IDeviceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceNameHelper {
    public static String getDeviceName2(List<Dc> dcs){
        Map<Integer ,String> deviceNameMap = new HashMap<>();
        StringBuilder deviceNames = new StringBuilder();
        for (int i = 0 ; i < dcs.size() ; i ++){
            switch (dcs.get(i).dc) {
                case IDeviceType.RRQZ:
                case IDeviceType.RDCZ:
                    deviceNameMap.put(0 ,"灶具");
                    break;
                case IDeviceType.RDKX:
                    deviceNameMap.put(2,"烤箱");
                    break;
                case IDeviceType.RZQL:
                    deviceNameMap.put(3,"蒸箱");
                    break;

                case IDeviceType.RZKY:
                    deviceNameMap.put( 1,"一体机");
                    break;
                case IDeviceType.RWBL:
                    deviceNameMap.put(4 , IDeviceType.RWBL_ZN);
                    break;
                default:
                    break;
            }
        }
        int i = 0;
        while (i <= 4){
            String deviceName = deviceNameMap.get(i);
            if (deviceName != null){
                if (deviceNames.toString() != null && deviceNames.toString().length() != 0){
                    deviceNames.append("/").append(deviceName);
                }else {
                    deviceNames.append(deviceName);
                }
            }
            i ++ ;
        }
        return deviceNames.toString() ;
    }
}
