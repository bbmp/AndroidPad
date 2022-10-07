package com.robam.common.ble;

import com.clj.fastble.data.BleDevice;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//ble设备
public class BleDeviceInfo {
    //每台设备对应decoder
    private final Map<Device, BleDecoder> connection_map = new HashMap<>();//BLE链路MAP

    private static class Holder {
        private static BleDeviceInfo instance = new BleDeviceInfo();
    }

    public static BleDeviceInfo getInstance() {
        return Holder.instance;
    }


//    public BleDecoder getBleDecoder(BleDevice bleDevice) {
//        if (null != bleDevice) {
//            Iterator<Device> iterator = connection_map.keySet().iterator();
//            while (iterator.hasNext()) {
//                Device device = iterator.next();
//                if (bleDevice.getMac().equals(device.mac))
//                    return connection_map.get(device);
//            }
//        }
//        return null;
//    }
//
//    public void addDeviceToMap(BleDevice bleDevice) {
//        if (null != bleDevice) {
//            Iterator<Device> iterator = connection_map.keySet().iterator();
//            while (iterator.hasNext()) {
//                Device device = iterator.next();
//                if (bleDevice.getMac().equals(device.mac)) { //重新连接
//                    BleDecoder bleDecoder = getBleDecoder(bleDevice);
//                    if (null != bleDecoder)
//                        bleDecoder.init_decoder(0);
//                    return;
//                }
//            }
//            Device device = new Device(bleDevice.getMac());
//            BleDecoder decoder = new BleDecoder(0);
//            connection_map.put(device, decoder);
//        }
//    }
//
//    public void removeDeviceFromMap(BleDevice bleDevice) {
//        if (null != bleDevice) {
//            Iterator<Device> iterator = connection_map.keySet().iterator();
//            while (iterator.hasNext()) {
//                Device device = iterator.next();
//                if (bleDevice.getMac().equals(device.mac))
//                    iterator.remove();
//            }
//        }
//    }
}
