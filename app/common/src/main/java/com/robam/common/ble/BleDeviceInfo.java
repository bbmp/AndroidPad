package com.robam.common.ble;

import com.clj.fastble.data.BleDevice;
import com.robam.common.bean.AccountInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//ble设备
public class BleDeviceInfo {
    //每台设备对应decoder
    private final Map<String, BleDecoder> connection_map = new HashMap<>();//BLE链路MAP
    private final List<BleDeviceProperty> dev_list = new LinkedList<>();//设备链表

    private static class Holder {
        private static BleDeviceInfo instance = new BleDeviceInfo();
    }

    public static BleDeviceInfo getInstance() {
        return Holder.instance;
    }

    public void addDeviceToMap(BleDevice bleDevice) {
        if (null != bleDevice) {
            if (!connection_map.containsKey(bleDevice.getMac())) {
                BleDecoder decoder = new BleDecoder(0);
                connection_map.put(bleDevice.getMac(), decoder);
            }
        }
    }

    public void removeDeviceFromMap(BleDevice bleDevice) {
        if (null != bleDevice) {
            if (connection_map.containsKey(bleDevice.getMac()))
                connection_map.remove(bleDevice.getMac());
        }
    }
}
