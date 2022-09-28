package com.robam.ventilator.protocol.ble;

import com.clj.fastble.data.BleDevice;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.ble.BleDeviceInfo;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//蓝牙解析
public class BleVentilator {

    public static void bleParser(BleDevice bleDevice, byte[] data) {
        if (null == data)
            return;
        LogUtils.e("bleParser " + StringUtils.bytes2Hex(data));
        BleDecoder decoder = AccountInfo.getInstance().getBleDecoder(bleDevice.getMac());
        if(decoder != null) { //decoder一定是不为null的
            decoder.push_raw_data(BleDecoder.byteArraysToByteArrays(data));
            Byte [] ret;
            do {
                ret = decoder.decode_data(1000);
                if(ret != null) {
                    Byte [] resp_payload;
                    Byte [] resp;
                    boolean response = true;
                    byte [] ret2 = BleDecoder.ByteArraysTobyteArrays(ret);
                    LogUtils.e("ret2 =" + StringUtils.bytes2Hex(ret2));
                    switch(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]) {
                        case BleDecoder.ROKI_UART_CMD_KEY_INTERNAL://收到内部指令
                            switch(ret2[BleDecoder.DECODE_CMD_ID_OFFSET]) {
                                case BleDecoder.CMD_PAIRING_REQUEST_INT://设备应用层请求配对
                                    if(ret2.length >= BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + 12) {
                                        if(ret2.length < BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1  + ret2[11] + 12) {
                                            response = false;
                                        } else {
                                            byte[] guid = new byte[17];//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid = new byte[4];//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id = new byte[ret2[11]];//业务编码长度
                                            int ble_type;//蓝牙产品品类
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET, guid,0,  5);//得到设备业务类型
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5, int_guid, 0, 1 + 3);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1, biz_id, 0, ret2[11]);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + ret2[11], guid, 5, 12);
                                            ble_type = ret2[BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + ret2[11] + 12];
                                            int i;
                                            for(i = 0; i<AccountInfo.getInstance().deviceList.size(); i++) {
                                                Device device = AccountInfo.getInstance().deviceList.get(i);
                                                if (device.bleType == ble_type) {
                                                    response = false;
                                                    break;
                                                }
                                                if ((new String(guid)).equals(device.guid)) { //update info

                                                    if (!Arrays.equals(device.int_guid, int_guid)) {
                                                        device.int_guid = int_guid;
                                                    }
                                                    if (!(new String(biz_id)).equals(device.bid)) {
                                                        device.bid = new String(biz_id);
                                                    }
                                                    if (device.bleType != ble_type) {
                                                        device.bleType = ble_type;
                                                    }

                                                    break;
                                                }

                                            }
                                            if(i == AccountInfo.getInstance().deviceList.size()) { //insert
                                                AccountInfo.getInstance().deviceList.add(new Device(new String(guid), int_guid, new String(biz_id), ble_type));
                                            }
                                        }
                                    } else {
                                        response = false;
                                    }
                                    resp_payload = new Byte[] {BleDecoder.RC_FAIL, 0, 0, 0, 0};
                                    if(response) {
                                        resp_payload[0] = BleDecoder.RC_SUCCESS;
                                    }
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_PAIRING_REQUEST_INT, resp_payload);
                                    ble_write_no_resp(bleDevice, BleDecoder.ByteArraysTobyteArrays(resp));
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
                                            byte[] guid = new byte[17];//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid = new byte[4];//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id = new byte[ret2[13]];//业务编码长度
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1, guid, 0, 5);//得到设备业务类型
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5, int_guid, 0, 3 + 1);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1, biz_id, 0, ret2[13]);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + ret2[13], guid, 5, 12);
                                            int i;
                                            for(i = 0; i < AccountInfo.getInstance().deviceList.size(); i++) {
                                                Device device = AccountInfo.getInstance().deviceList.get(i);
                                                if ((new String(guid)).equals(device.guid) && (new String(biz_id)).equals(device.bid)) {

                                                    if (!Arrays.equals(device.int_guid, int_guid)) {
                                                        response = false;
                                                        break;
                                                    }
                                                    break;
                                                }

                                            }
                                            if(i == AccountInfo.getInstance().deviceList.size()) {
                                                response = false;
                                            }
                                        }
                                    } else {
                                        response = false;
                                    }
                                    resp_payload = new Byte[] {BleDecoder.RC_FAIL};
                                    if(response) {
                                        resp_payload[0] = BleDecoder.RC_SUCCESS;
                                    }
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_DEVICE_ONLINE_INT, resp_payload);
                                    ble_write_no_resp(bleDevice, BleDecoder.ByteArraysTobyteArrays(resp));
                                    if(!response) {
                                        delay_disconnect_ble(bleDevice);//若响应失败,则延迟一会强制主动断开该通道
                                    }
                                    break;
                                case BleDecoder.CMD_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知请求
                                    resp_payload = new Byte[] {BleDecoder.RC_SUCCESS};
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE, resp_payload);
                                    ble_write_no_resp(bleDevice, BleDecoder.ByteArraysTobyteArrays(resp));
//                                    listRemove(channel);
                                    break;
                                case BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知响应
//                                    listRemove(channel);
//                                    BleDeviceInfo.getInstance().removeDeviceFromMap(bleDevice);
                                    delay_disconnect_ble(bleDevice);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_BROADCAST://收到广播上报指令
                            for(Device device: AccountInfo.getInstance().deviceList) {
                                if(bleDevice.getMac().equals(device.mac)) {
                                    String target_guid = device.guid;
                                    String topic = "/b/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                    ble_mqtt_publish(topic, device.guid, ret2);
                                    break;
                                }
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_DYNAMIC://收到外部指令(一般用于响应外部设备),通过MQTT转发出去
//                            for(Device device : AccountInfo.getInstance().deviceList) {
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

//    public static BleDecoder getBleDecoder(BleDevice bleDevice) {
//        if (connection_map.containsKey(bleDevice.getMac()))
//            return connection_map.get(bleDevice.getMac());
//
//        return null;
//    }
//
//    public static void addDeviceToMap(BleDevice bleDevice) {
//        if (null != bleDevice) {
//            if (connection_map.containsKey(bleDevice.getMac())) { //重新连接上
//                BleDecoder bleDecoder = getBleDecoder(bleDevice);
//                if (null != bleDecoder)
//                    bleDecoder.init_decoder(0);
//                return;
//            }
//
//            BleDecoder decoder = new BleDecoder(0);
//            connection_map.put(bleDevice.getMac(), decoder);
//        }
//    }

    //将数据通过BLE指定通道发送出去
    public static void ble_write_no_resp(BleDevice bleDevice, byte[] data) {
        //TODO 这里实现BLE Write no response
        for (Device device: AccountInfo.getInstance().deviceList) {

        }
    }

    //延迟断开BLE指定通道
    public static void delay_disconnect_ble(BleDevice bleDevice) {
        //TODO 延迟一段时间后主动断开指定通道的BLE连接,主动断开时的操作和on_ble_disconnect_event_cb()方法内的操作一致
        BlueToothManager.disConnect(bleDevice);
    }

//    public static void removeDeviceFromMap(BleDevice bleDevice) {
//        if (null != bleDevice) {
//            if (connection_map.containsKey(bleDevice.getMac()))
//                connection_map.remove(bleDevice.getMac());
//        }
//    }



    //从BLE收到的数据通过MQTT发出去
    public static void ble_mqtt_publish(String topic, String sender_guid, byte[] ble_payload) {
        byte[] guid_bytes = sender_guid.getBytes();
        byte [] mqtt_payload = new byte[guid_bytes.length + ble_payload.length - 1];
        System.arraycopy(guid_bytes, 0, mqtt_payload, 0, guid_bytes.length);
        System.arraycopy(ble_payload, BleDecoder.DECODE_CMD_ID_OFFSET, mqtt_payload, guid_bytes.length,
                ble_payload.length - BleDecoder.DECODE_CMD_ID_OFFSET);
        //TODO 将数据通过MQTT上报至云端:mqtt_publish(topic, mqtt_payload, qos0)
        MqttManager.getInstance().publish(topic, mqtt_payload);
    }

}
