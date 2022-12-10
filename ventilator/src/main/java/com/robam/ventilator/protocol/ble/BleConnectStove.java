package com.robam.ventilator.protocol.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.robam.common.IDeviceType;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BleConnectStove {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());

    //开始扫描
    public static void startScan(BleVentilator.BleCallBack bleCallBack) {

        WeakReference<BleVentilator.BleCallBack> bleCallBackWeakReference = new WeakReference<>(bleCallBack);
        BlueToothManager.cancelScan();

        BlueToothManager.startScan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtils.e("stove onScanStarted");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                LogUtils.e("stove onLeScan " + bleDevice.getName());
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                //LogUtils.e("onScanFinished ");

                if (null != scanResultList && scanResultList.size() > 0) {
                    for (BleDevice bleDevice: scanResultList) {
                        if (bleDevice.getName().contains(BlueToothManager.stove) || bleDevice.getName().contains(BlueToothManager.pan))
                            connect(bleDevice, bleCallBackWeakReference);
                    }
                } else {
                    if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                        bleCallBackWeakReference.get().onScanFinished();
                }
            }
        });
    }
    //连接设备
    public static void connect(final BleDevice bleDevice, WeakReference<BleVentilator.BleCallBack> bleCallBackWeakReference) {
        LogUtils.e("stove connect " + bleDevice.getMac() + " " + bleDevice.getName() + " rssi " + bleDevice.getRssi());
        BlueToothManager.connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtils.e("stove onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                LogUtils.e("stove onConnectFail " + exception.getDescription());
                if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                    bleCallBackWeakReference.get().onConnectFail();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("stove onConnectSuccess " + bleDevice.getName());
                //设置mtu
//                BlueToothManager.setMtu(bleDevice);
                //连接成功
                BleVentilator.addSubDevice(IDeviceType.RRQZ, bleDevice);

                BleVentilator.getBuletoothGatt(bleDevice);

                if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                    bleCallBackWeakReference.get().onConnectSuccess();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("stove onDisConnected");
                //掉线
                if (null != gatt)
                    gatt.close();
                //清除设备蓝牙信息
                if (BleVentilator.setBleDevice(bleDevice.getMac(), null, null)) {
                    //重新连接
                    try {
                        threadPoolExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                }
                                connect(bleDevice, bleCallBackWeakReference);
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        });
    }
}
