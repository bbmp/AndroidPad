package com.robam.common.manager;


import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.scan.BleScanner;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.ble.BleDeviceProperty;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlueToothManager {
    public static final Map<Integer, String> send_map = new HashMap<>();//外部命令发送关系表
    private static final Lock lock = new ReentrantLock();

    //扫描规则
    public static void setScanRule(String[] names) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }
    //扫描规则
    public static void setScanRule(String mac) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }

    //开始扫描
    public static void startScan(BleScanCallback callback) {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) { //扫描中
                if (null != callback)
                    callback.onScanFinished(null);
                return;
            }
            BleManager.getInstance().scan(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //取消扫描
    public static void cancelScan() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //连接设备
    public static void connect(final BleDevice bleDevice, BleGattCallback callback) {
        try {
            BleManager.getInstance().connect(bleDevice, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //断开连接
    public static void disConnect(final BleDevice bleDevice) {
        try {
            BleManager.getInstance().disconnect(bleDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //订阅通知
    public static void notify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, BleNotifyCallback callback) {
        try {
            BleManager.getInstance().notify(
                    bleDevice,
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString(), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //取消通知
    public static void stopNotify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        try {
            BleManager.getInstance().stopNotify(
                    bleDevice,
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //订阅可靠通知
    public static void indicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, BleIndicateCallback callback) {
        try {
            BleManager.getInstance().indicate(
                    bleDevice,
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString(), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //取消订阅
    public static void stopIndicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        try {
            BleManager.getInstance().stopIndicate(
                    bleDevice,
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //写数据
    public static void write_no_response(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, byte[] payload, BleWriteCallback bleWriteCallback) {
        try {
            LogUtils.e(StringUtils.bytes2Hex(payload));
            LogUtils.e(characteristic.getUuid().toString());
            BleManager.getInstance().write(
                    bleDevice,
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString(),
                    payload,
                    bleWriteCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
