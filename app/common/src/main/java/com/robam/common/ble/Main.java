package com.robam.common.ble;

import com.robam.common.utils.ByteUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private final Map<Integer, BleDecoder> connection_map = new HashMap<>();//BLE链路MAP
    private final List<BleDeviceProperty> dev_list = new LinkedList<>();//设备链表
    private final Map<Integer, String> send_map = new HashMap<>();//外部命令发送关系表
    private final Lock lock = new ReentrantLock();


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
    //假装是BLE连接回调
    public void on_ble_connect_event_cb(int channel) {
        BleDecoder decoder = new BleDecoder(0);
        lock.lock();
        connection_map.put(channel, decoder);
        lock.unlock();
    }
    //假装是BLE断开回调
    public void on_ble_disconnect_event_cb(int channel) {
        lock.lock();
        mapRemove(channel);
        for(int i = 0; i < dev_list.size(); i++) {
            BleDeviceProperty dev = dev_list.get(i);
            if(dev.getChan() == channel) {
                dev.setChan(-1);
                dev_list.set(i, dev);
                break;
            }
        }
        lock.unlock();
    }
    //假装是BLE Notify接收回调,channel是通道号,data是BLE收到的数据
    public void on_ble_notify_cb(int channel, Byte [] data) {
        lock.lock();
        BleDecoder decoder = connection_map.get(channel);
        if(decoder != null) { //decoder一定是不为null的
            decoder.push_raw_data(data);
            Byte [] ret;
            do {
                ret = decoder.decode_data(1000);
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
                                            int i;
                                            for(i = 0; i < dev_list.size(); i++) {
                                                BleDeviceProperty dev = dev_list.get(i);
                                                if(dev.getBle_type() == ble_type) {
                                                    response = false;
                                                    break;
                                                }
                                                if(dev.getGuid().equals(new String(guid))) { //update info
                                                    boolean update = false;
                                                    if(dev.getChan() != channel) {
                                                        dev.setChan(channel);
                                                        update = true;
                                                    }
                                                    if(!Arrays.equals(dev.getInt_guid(), int_guid)) {
                                                        dev.setInt_guid(int_guid);
                                                        update = true;
                                                    }
                                                    if(!dev.getBiz_id().equals(new String(biz_id))) {
                                                        dev.setBiz_id(new String(biz_id));
                                                        update = true;
                                                    }
                                                    if(dev.getBle_type() != ble_type) {
                                                        dev.setBle_type(ble_type);
                                                    }
                                                    if(update) {
                                                        dev_list.set(i, dev);
                                                    }
                                                    break;
                                                }
                                            }
                                            if(i == dev_list.size()) { //insert
                                                dev_list.add(new BleDeviceProperty(channel, new String(guid), int_guid, new String(biz_id), ble_type));
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
                                    ble_write_no_resp(channel, ByteArraysTobyteArrays(resp));
                                    if(!response) {
                                        delay_disconnect_ble(channel);//若响应失败,则延迟一会强制断开该通道
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
                                            int i;
                                            for(i = 0; i < dev_list.size(); i++) {
                                                BleDeviceProperty dev = dev_list.get(i);
                                                if(dev.getGuid().equals(new String(guid)) && dev.getBiz_id().equals(new String(biz_id))) {
                                                    if(dev.getChan() < 0) {
                                                        response = false;
                                                        break;
                                                    }
                                                    if(!Arrays.equals(dev.getInt_guid(), int_guid)) {
                                                        response = false;
                                                        break;
                                                    }
                                                    break;
                                                }
                                            }
                                            if(i == dev_list.size()) {
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
                                    ble_write_no_resp(channel, ByteArraysTobyteArrays(resp));
                                    if(!response) {
                                        delay_disconnect_ble(channel);//若响应失败,则延迟一会强制主动断开该通道
                                    }
                                    break;
                                case BleDecoder.CMD_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知请求
                                    resp_payload = new Byte[] {BleDecoder.RC_SUCCESS};
                                    resp = BleDecoder.make_internal_send_packet(BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE, resp_payload);
                                    ble_write_no_resp(channel, ByteArraysTobyteArrays(resp));
                                    listRemove(channel);
                                    break;
                                case BleDecoder.RESP_DISCONNECT_BLE_PRIOR_NOTICE://收到BLE从机的断开预通知响应
                                    listRemove(channel);
                                    mapRemove(channel);
                                    delay_disconnect_ble(channel);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_BROADCAST://收到广播上报指令
                            for(BleDeviceProperty dev : dev_list) {
                                if(dev.getChan() == channel) {
                                    String target_guid = dev.getGuid();
                                    String topic = "/b/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                    ble_mqtt_publish(topic, dev.getGuid(), ret2);
                                }
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_DYNAMIC://收到外部指令(一般用于响应外部设备),通过MQTT转发出去
                            for(BleDeviceProperty dev : dev_list) {
                                String target_guid = send_map.get((int) ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]);
                                String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                ble_mqtt_publish(topic, dev.getGuid(), ret2);
                            }
                            break;
                    }
                }
            } while(ret != null);
        }
        lock.unlock();
    }
    //MQTT接收回调
    public void on_mqtt_receive_subscribe_cb(String topic, byte[] mqtt_data) {
        if(is_topic_to_self(topic)) {
            //TODO 执行烟机相关逻辑
        } else {
            lock.lock();
            for(BleDeviceProperty dev : dev_list) {
                String dev_guid = dev.getGuid();
                String dev_topic = "/u/" + dev_guid.substring(0, 5) + "/" + dev_guid.substring(5, dev_guid.length());
                if(topic.equals(dev_topic)) {
                    byte[] send_guid_bytes = Arrays.copyOfRange(mqtt_data, 0, BleDecoder.GUID_LEN);
                    int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
                    Byte[] mqtt_payload = byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
                    BleDecoder.ExternBleData data = BleDecoder.make_external_send_packet(cmd_id, mqtt_payload);
                    send_map.put(data.cmd_key, new String(send_guid_bytes));
                    ble_write_no_resp(dev.getChan(), ByteArraysTobyteArrays(data.payload));
                }
            }
            lock.unlock();
        }
    }
    private void listRemove(int channel) {
        Iterator<BleDeviceProperty> iterator = dev_list.iterator();
        while (iterator.hasNext()) {
            BleDeviceProperty bleDeviceProperty = iterator.next();
            if (bleDeviceProperty.chan == channel)
                iterator.remove();
        }
    }

    private void mapRemove(int channel) {
        Iterator<Integer> iterator = connection_map.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer.intValue() == channel)
                iterator.remove();
        }
    }
    //判断TOPIC是不是发给烟机自己的,是则返回true
    public boolean is_topic_to_self(String topic) {
        //这里用来判断topic是发给烟机自身的还是发给蓝牙子设备(灶具或无人锅)的,通过烟机GUID进行对比
        String self_guid = "5010SXXXXXXXXXXXX";//TODO 需要实现获取烟机自身GUID的代码
        String self_topic = "/u/" + self_guid.substring(0, 5) + "/" + self_guid.substring(5, self_guid.length());
        return topic.equals(self_topic);
    }
    //从BLE收到的数据通过MQTT发出去
    public void ble_mqtt_publish(String topic, String sender_guid, byte[] ble_payload) {
        byte[] guid_bytes = sender_guid.getBytes();
        byte [] mqtt_payload = new byte[guid_bytes.length + ble_payload.length - 1];
        System.arraycopy(guid_bytes, 0, mqtt_payload, 0, guid_bytes.length);
        System.arraycopy(ble_payload, BleDecoder.DECODE_CMD_ID_OFFSET, mqtt_payload, guid_bytes.length,
                ble_payload.length - BleDecoder.DECODE_CMD_ID_OFFSET);
        //TODO 将数据通过MQTT上报至云端:mqtt_publish(topic, mqtt_payload, qos0)
    }
    //将数据通过BLE指定通道发送出去
    public void ble_write_no_resp(int channel, byte[] data) {
        //TODO 这里实现BLE Write no response
    }
    //延迟断开BLE指定通道
    public void delay_disconnect_ble(int channel) {
        //TODO 延迟一段时间后主动断开指定通道的BLE连接,主动断开时的操作和on_ble_disconnect_event_cb()方法内的操作一致
    }
}
