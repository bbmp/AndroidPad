package com.robam.common.manager;

import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.scan.BleScanner;

public class BlueToothManager {

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

    //开始扫描
    public static void startScan(BleScanCallback callback) {
        BleManager.getInstance().scan(callback);
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
        BleManager.getInstance().connect(bleDevice, callback);
    }

    //订阅通知
    public static void notify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(), callback);
    }
    //取消通知
    public static void stopNotify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().stopNotify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString());
    }

    //订阅可靠通知
    public static void indicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, BleIndicateCallback callback) {
        BleManager.getInstance().indicate(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(), callback);
    }
    //取消订阅
    public static void stopIndicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().stopIndicate(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString());
    }
}
