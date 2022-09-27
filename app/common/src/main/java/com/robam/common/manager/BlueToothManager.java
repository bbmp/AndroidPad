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
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.ble.BleDeviceProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlueToothManager {
    //每台设备对应decoder
    private static final Map<Device, BleDecoder> connection_map = new HashMap<>();//BLE链路MAP
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

    public static void bleParser(BleDevice bleDevice, byte[] data) {
        BleDecoder decoder = getBleDecoder(bleDevice);
        if(decoder != null) { //decoder一定是不为null的
            decoder.push_raw_data(byteArraysToByteArrays(data));
            Byte [] ret;
            do {
                ret = decoder.decode_data();
                if(ret != null) {
                    Byte [] resp_payload;
                    Byte [] resp;
                    boolean response = true;
                    byte [] ret2 = ByteArraysTobyteArrays(ret);
                    switch(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]) {
                        case BleDecoder.ROKI_UART_CMD_KEY_INTERNAL://收到内部指令
                            switch(ret2[BleDecoder.DECODE_CMD_ID_OFFSET]) {
                                case BleDecoder.CMD_PAIRING_REQUEST_INT://设备应用层请求配对
                                    if(ret2.length >= BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + 12) {
                                        if(ret2.length < BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1  + ret2[11] + 12) {
                                            response = false;
                                        } else {
                                            byte[] guid;//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid;//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id;//业务编码长度
                                            int ble_type;//蓝牙产品品类
                                            guid = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET, 5);//得到设备业务类型
                                            int_guid = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5, 1 + 3);
                                            biz_id = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1, ret2[11]);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + ret2[11], guid, 5, 12);
                                            ble_type = ret2[BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + ret2[11] + 12];
//                                            int i;
//                                            for(i = 0; i < dev_list.size(); i++) {
//                                                BleDeviceProperty dev = dev_list.get(i);
//                                                if(dev.getBle_type() == ble_type) {
//                                                    response = false;
//                                                    break;
//                                                }
//                                                if(dev.getGuid().equals(new String(guid))) { //update info
//                                                    boolean update = false;
//                                                    if(dev.getChan() != channel) {
//                                                        dev.setChan(channel);
//                                                        update = true;
//                                                    }
//                                                    if(!Arrays.equals(dev.getInt_guid(), int_guid)) {
//                                                        dev.setInt_guid(int_guid);
//                                                        update = true;
//                                                    }
//                                                    if(!dev.getBiz_id().equals(new String(biz_id))) {
//                                                        dev.setBiz_id(new String(biz_id));
//                                                        update = true;
//                                                    }
//                                                    if(dev.getBle_type() != ble_type) {
//                                                        dev.setBle_type(ble_type);
//                                                    }
//                                                    if(update) {
//                                                        dev_list.set(i, dev);
//                                                    }
//                                                    break;
//                                                }
//                                            }
//                                            if(i == dev_list.size()) { //insert
//                                                dev_list.add(new BleDeviceProperty(channel, new String(guid), int_guid, new String(biz_id), ble_type));
//                                            }
                                        }
                                    } else {
                                        response = false;
                                    }
                                    resp_payload = new Byte[] {BleDecoder.RC_FAIL, 0, 0, 0, 0};
                                    if(response) {
                                        resp_payload[0] = BleDecoder.RC_SUCCESS;
                                    }
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_PAIRING_REQUEST_INT, resp_payload);
                                    ble_write_no_resp(bleDevice, ByteArraysTobyteArrays(resp));
                                    if(!response) {
                                        delay_disconnect_ble(bleDevice);//若响应失败,则延迟一会强制断开该通道
                                    }
                                    break;
                                case BleDecoder.CMD_DEVICE_ONLINE_INT://设备上线通知
                                    if(ret2.length >= BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + 12) {
                                        if (ret2.length < BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + ret2[13] + 12 || ret2[3] != 2) {
                                            response = false;
                                        } else {
                                            int version = ret2[BleDecoder.DECODE_PAYLOAD_OFFSET];
                                            byte[] guid;//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid;//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id;//业务编码长度
                                            guid = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1, 5);//得到设备业务类型
                                            int_guid = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5, 3 + 1);
                                            biz_id = Arrays.copyOfRange(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1, ret2[13]);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + ret2[13], guid, 5, 12);
//                                            int i;
//                                            for(i = 0; i < dev_list.size(); i++) {
//                                                BleDeviceProperty dev = dev_list.get(i);
//                                                if(dev.getGuid().equals(new String(guid)) && dev.getBiz_id().equals(new String(biz_id))) {
//                                                    if(dev.getChan() < 0) {
//                                                        response = false;
//                                                        break;
//                                                    }
//                                                    if(!Arrays.equals(dev.getInt_guid(), int_guid)) {
//                                                        response = false;
//                                                        break;
//                                                    }
//                                                    break;
//                                                }
//                                            }
//                                            if(i == dev_list.size()) {
//                                                response = false;
//                                            }
                                        }
                                    } else {
                                        response = false;
                                    }
                                    resp_payload = new Byte[] {BleDecoder.RC_FAIL};
                                    if(response) {
                                        resp_payload[0] = BleDecoder.RC_SUCCESS;
                                    }
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_DEVICE_ONLINE_INT, resp_payload);
                                    ble_write_no_resp(bleDevice, ByteArraysTobyteArrays(resp));
                                    if(!response) {
                                        delay_disconnect_ble(bleDevice);//若响应失败,则延迟一会强制主动断开该通道
                                    }
                                    break;
                                case BleDecoder.CMD_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知请求
                                    resp_payload = new Byte[] {BleDecoder.RC_SUCCESS};
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE, resp_payload);
                                    ble_write_no_resp(bleDevice, ByteArraysTobyteArrays(resp));
//                                    listRemove(channel);
                                    break;
                                case BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知响应
//                                    listRemove(channel);
                                    removeDeviceFromMap(bleDevice);
                                    delay_disconnect_ble(bleDevice);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_BROADCAST://收到广播上报指令
//                            for(BleDeviceProperty dev : dev_list) {
//                                if(dev.getChan() == channel) {
//                                    String target_guid = dev.getGuid();
//                                    String topic = "/b/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
//                                    ble_mqtt_publish(topic, dev.getGuid(), ret2);
//                                }
//                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_DYNAMIC://收到外部指令(一般用于响应外部设备),通过MQTT转发出去
//                            for(BleDeviceProperty dev : dev_list) {
//                                String target_guid = send_map.get((int) ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]);
//                                String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
//                                ble_mqtt_publish(topic, dev.getGuid(), ret2);
//                            }
                            break;
                    }
                }
            } while(ret != null);
        }
    }
    //Byte数组转byte数组
    public static byte [] ByteArraysTobyteArrays(Byte [] input) {
        if(input == null) {
            return null;
        }
        byte [] output = new byte[input.length];
        for(int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }
    //byte转Byte数组
    public static Byte [] byteArraysToByteArrays(byte [] input) {
        if(input == null) {
            return null;
        }
        Byte [] output = new Byte[input.length];
        for(int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    //将数据通过BLE指定通道发送出去
    public static void ble_write_no_resp(BleDevice bleDevice, byte[] data) {
        //TODO 这里实现BLE Write no response
    }

    //延迟断开BLE指定通道
    public static void delay_disconnect_ble(BleDevice bleDevice) {
        //TODO 延迟一段时间后主动断开指定通道的BLE连接,主动断开时的操作和on_ble_disconnect_event_cb()方法内的操作一致
    }

    public static void removeDeviceFromMap(BleDevice bleDevice) {
        if (null != bleDevice) {
            Iterator<Device> iterator = connection_map.keySet().iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                if (device.mac.equals(bleDevice.getMac()))
                    iterator.remove();
            }
        }
    }

    public static BleDecoder getBleDecoder(BleDevice bleDevice) {
        Iterator iterator = connection_map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Device, BleDecoder> entry = (Map.Entry<Device, BleDecoder>) iterator.next();
            if (entry.getKey().mac.equals(bleDevice.getMac()))
                return entry.getValue();
        }
        return null;
    }

    public static void addDeviceToMap(BleDevice bleDevice) {
        if (null != bleDevice) {
            Iterator<Device> iterator = connection_map.keySet().iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                if (device.mac.equals(bleDevice.getMac())) { //已经有
                    return;
                }
            }
            BleDecoder decoder = new BleDecoder(0);
            connection_map.put(new Device(bleDevice.getMac()), decoder);
        }
    }

    //从BLE收到的数据通过MQTT发出去
    public static void ble_mqtt_publish(String topic, String sender_guid, byte[] ble_payload) {
        byte[] guid_bytes = sender_guid.getBytes();
        byte [] mqtt_payload = new byte[guid_bytes.length + ble_payload.length - 1];
        System.arraycopy(guid_bytes, 0, mqtt_payload, 0, guid_bytes.length);
        System.arraycopy(ble_payload, BleDecoder.DECODE_CMD_ID_OFFSET, mqtt_payload, guid_bytes.length,
                ble_payload.length - BleDecoder.DECODE_CMD_ID_OFFSET);
        //TODO 将数据通过MQTT上报至云端:mqtt_publish(topic, mqtt_payload, qos0)
    }
}
