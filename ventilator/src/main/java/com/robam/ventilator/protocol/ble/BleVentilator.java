package com.robam.ventilator.protocol.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.device.PanFactory;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveFactory;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//蓝牙解析
public class BleVentilator {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());
    private static WeakReference<BleCallBack> bleCallBackWeakReference;

    public interface BleCallBack {
        void onScanFinished();

        void onConnectFail();

        void onConnectSuccess();
    }

    //开始扫描
    public static void startScan(String model, BleCallBack bleCallBack) {
        bleCallBackWeakReference = new WeakReference<>(bleCallBack);

        BlueToothManager.startScan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtils.e("onScanStarted");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                LogUtils.e("onLeScan " + bleDevice.getName());
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtils.e("onScanFinished ");

                if (null != scanResultList && scanResultList.size() > 0) {
                    for (BleDevice bleDevice: scanResultList) {
                        if (bleDevice.getName().contains(BlueToothManager.stove) || bleDevice.getName().contains(BlueToothManager.pan))
                            connect(model, bleDevice);
                    }
                } else {
                    if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                        bleCallBackWeakReference.get().onScanFinished();
                }
            }
        });
    }

    //连接设备
    public static void connect(String model, final BleDevice bleDevice) {
        LogUtils.e("connect " + bleDevice.getMac());
        BlueToothManager.connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtils.e("onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                LogUtils.e("onConnectFail " + exception.getDescription());
                if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                    bleCallBackWeakReference.get().onConnectFail();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onConnectSuccess " + bleDevice.getName());
                //设置mtu
//                BlueToothManager.setMtu(bleDevice);
                //连接成功
                addSubDevice(model, bleDevice);

                getBuletoothGatt(bleDevice);

                if (null != bleCallBackWeakReference && null != bleCallBackWeakReference.get())
                    bleCallBackWeakReference.get().onConnectSuccess();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onDisConnected");
                //掉线
                if (null != gatt)
                    gatt.close();
                //清除设备蓝牙信息
                if (setBleDevice(bleDevice.getMac(), null, null)) {
                    //重新连接
                    try {
                        threadPoolExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                }
                                connect(model, bleDevice);
                            }
                        });
                    } catch (Exception e) {}
                }
            }
        });
    }
    //添加子设备到设备列表
    public static void addSubDevice(String model, BleDevice bleDevice) {
        LogUtils.e("bleDevice mac " + bleDevice.getMac());
        String deviceNum = changeMac(bleDevice.getMac());
        for (Device device: AccountInfo.getInstance().deviceList) {
            //已经存在锅或灶，mac地址判断
            if (bleDevice.getMac().equals(device.mac) || deviceNum.equals(DeviceUtils.getDeviceNumber(device.guid))) {
                if (device instanceof Pan) {
                    BleDecoder bleDecoder = ((Pan) device).bleDecoder;
                    if (null != bleDecoder)
                        bleDecoder.init_decoder(0);
                    else
                        ((Pan) device).bleDecoder = new BleDecoder(0);
                    device.mac = bleDevice.getMac();
                    ((Pan) device).bleDevice = bleDevice;
                } else if (device instanceof Stove) {
                    BleDecoder bleDecoder = ((Stove) device).bleDecoder;
                    if (null != bleDecoder)
                        bleDecoder.init_decoder(0);
                    else
                        ((Stove) device).bleDecoder = new BleDecoder(0);
                    device.mac = bleDevice.getMac();
                    ((Stove) device).bleDevice = bleDevice;
                }
                return;
            }
        }
        if (IDeviceType.RRQZ.equals(model)) {
            Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                if (device instanceof Stove)
                    iterator.remove();  //删除其他灶
            }
            Stove stove = new Stove("燃气灶", IDeviceType.RRQZ, "9B328");
            stove.mac = bleDevice.getMac();
            stove.bleDecoder = new BleDecoder(0);
            AccountInfo.getInstance().deviceList.add(stove);
        } else if (IDeviceType.RZNG.equals(model)) {
            Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                if (device instanceof Pan)
                    iterator.remove();  //删除其他锅
            }
            Pan pan = new Pan("明火自动翻炒锅", IDeviceType.RZNG, "KP100");
            pan.mac = bleDevice.getMac();
            pan.bleDecoder = new BleDecoder(0);
            AccountInfo.getInstance().deviceList.add(pan);
        }
    }
    //转变bledevice mac地址
    private static String changeMac(String mac) {
        String deviceNum = "";
        if (null != mac) {
            String[] data = mac.split(":");
            int length = data.length;
            while (length > 0) {
                length--;
                deviceNum += data[length];
            }
        }
        return deviceNum;
    }

    //设置蓝牙设备的读写特征符
    public static boolean setBleDevice(String mac, BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (mac.equals(device.mac)) {
                if (device instanceof Pan) {
                    ((Pan) device).bleDevice = bleDevice;
                    ((Pan) device).characteristic = characteristic;
                    if (null == bleDevice)
                        device.bleType = 0;
                    return true;
                } else if (device instanceof Stove) {
                    ((Stove) device).bleDevice = bleDevice;
                    ((Stove) device).characteristic = characteristic;
                    if (null == bleDevice)
                        device.bleType = 0;
                    return true;
                }
                break;
            }
        }
        return false;
    }
    //获取gatt提供的服务
    public static void getBuletoothGatt(BleDevice bleDevice) {
        //获取设备服务
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        List<BluetoothGattService> serviceList = new ArrayList<>();
        for (BluetoothGattService service : gatt.getServices()) {
            serviceList.add(service);
        }
        //
        for (BluetoothGattService service: serviceList) {
            UUID uuid = service.getUuid();
            if (uuid.toString().contains("fff0")) { //service uuid
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    uuid = characteristic.getUuid();
                    if (uuid.toString().contains("fff1")) {   //读写
                        LogUtils.e("uuid " + uuid);
                        //设置读写特征符
                        setBleDevice(bleDevice.getMac(), bleDevice, characteristic);
                    } else if (uuid.toString().contains("fff4")) {  //notify
                        int charaProp = characteristic.getProperties();
                        LogUtils.e("uuid " + uuid);
                        if (charaProp == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                            notify(bleDevice, characteristic);
                        }
                    }
                }
                break;
            }
        }
    }

    //订阅通知
    private static void notify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BlueToothManager.notify(bleDevice, characteristic,
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {

                        // 打开通知操作成功（UI线程）
                        LogUtils.e("onNotifySuccess");
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        // 打开通知操作失败（UI线程）
                        LogUtils.e("onNotifyFailure " + exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现（UI线程）
                        LogUtils.e("Thread " + Thread.currentThread() + " onCharacteristicChanged " + StringUtils.bytes2Hex(data));
                        for (Device device: AccountInfo.getInstance().deviceList) {
                            if (bleDevice.getMac().equals(device.mac)) {
                                if (device instanceof Pan)
                                    BleVentilator.bleParser(bleDevice, ((Pan) device).bleDecoder, data);
                                else if (device instanceof Stove)
                                    BleVentilator.bleParser(bleDevice, ((Stove) device).bleDecoder, data);
                                break;
                            }
                        }
                    }
                });
    }

    public static void bleParser(BleDevice bleDevice, BleDecoder decoder, byte[] data) {
        if (null == data)
            return;

//        BleDecoder decoder = AccountInfo.getInstance().getBleDecoder(bleDevice.getMac());
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
                    switch(ByteUtils.toInt(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET])) {
                        case BleDecoder.ROKI_UART_CMD_KEY_INTERNAL://收到内部指令
                            switch(ByteUtils.toInt(ret2[BleDecoder.DECODE_CMD_ID_OFFSET])) {
                                case BleDecoder.CMD_PAIRING_REQUEST_INT://设备应用层请求配对
                                    int biz_len = ByteUtils.toInt(ret2[11]);
                                    if(ret2.length >= BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + 12) {
                                        if(ret2.length < BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1  + biz_len + 12) {
                                            response = false;
                                        } else {
                                            byte[] guid = new byte[17];//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid = new byte[4];//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id = new byte[biz_len];//业务编码长度
                                            int ble_type;//蓝牙产品品类
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET, guid,0,  5);//得到设备业务类型
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5, int_guid, 0, 1 + 3);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1, biz_id, 0, biz_len);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + biz_len, guid, 5, 12);
                                            ble_type = ByteUtils.toInt(ret2[BleDecoder.DECODE_PAYLOAD_OFFSET + 5 + 1 + 3 + 1 + biz_len + 12]);
                                            LogUtils.e("guid CMD_PAIRING_REQUEST_INT =" + new String(guid));

                                            for(Device device: AccountInfo.getInstance().deviceList) {

                                                if (device.bleType == ble_type) {
                                                    response = false;
                                                    break;
                                                }
                                                //配对成功
                                                if (bleDevice.getMac().equals(device.mac)) { //update info

                                                    device.guid = new String(guid);

                                                    device.int_guid = new String(int_guid);

                                                    device.bid = new String(biz_id);

                                                    device.bleType = ble_type;

                                                    break;
                                                }

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
                                    int biz_len2 = ByteUtils.toInt(ret2[13]);
                                    int sub_dev_type = ByteUtils.toInt(ret2[3]);
                                    if(ret2.length >= BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + 12) {
                                        if (ret2.length < BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + biz_len2 + 12 || sub_dev_type != 2) {
                                            response = false;
                                        } else {
                                            int version = ret2[BleDecoder.DECODE_PAYLOAD_OFFSET];
                                            byte[] guid = new byte[17];//设备业务类型(5B)+设备GID(12B)组成GUID
                                            byte[] int_guid = new byte[4];//内部设备类型(1B)+内部设备编码(3B)
                                            byte[] biz_id = new byte[ret2[13]];//业务编码长度
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1, guid, 0, 5);//得到设备业务类型
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5, int_guid, 0, 3 + 1);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1, biz_id, 0, biz_len2);
                                            System.arraycopy(ret2, BleDecoder.DECODE_PAYLOAD_OFFSET + 1 + 1 + 5 + 1 + 3 + 1 + biz_len2, guid, 5, 12);
                                            LogUtils.e("guid CMD_DEVICE_ONLINE_INT =" + new String(guid));
                                            int i = 0;
                                            for(Device device: AccountInfo.getInstance().deviceList) {

                                                if ((new String(guid)).equals(device.guid) && (new String(biz_id)).equals(device.bid)) {

                                                    if (!new String(int_guid).equals(device.int_guid)) {
                                                        response = false;
                                                        break;
                                                    }
                                                    //上线
                                                    if (device.status != Device.ONLINE) {
                                                        device.status = Device.ONLINE;
                                                        device.queryNum = 0;
                                                        AccountInfo.getInstance().getGuid().setValue(device.guid);

                                                        updateSubdevice(device);
                                                        //通知上线
                                                        HomeVentilator.getInstance().notifyOnline(new String(guid), new String(biz_id), 1);
                                                        //订阅主题
//                                                        MqttManager.getInstance().subscribe(DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid));
                                                    }
                                                    break;
                                                }
                                                i++;
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
                                    //通知下线
                                    break;
                                case BleDecoder.RESP_GET_POT_STATUS_INT:
                                case BleDecoder.CMD_COOKER_STATUS_RES: //烟机查询灶状态返回

                                    for (Device device: AccountInfo.getInstance().deviceList) {
                                        if (bleDevice.getMac().equals(device.mac)) {
                                            if (device instanceof Stove) {
                                                String target_guid = device.guid;
                                                String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                                byte paylaod[] = ble_make_external_mqtt(target_guid, ret2);
                                                MqttMsg msg = StoveFactory.getProtocol().decode(topic, paylaod);
                                                if (((Stove) device).onBleReceived(msg))
                                                    AccountInfo.getInstance().getGuid().setValue(device.guid); //更新灶具状态
                                            } else if (device instanceof Pan) {
                                                String target_guid = device.guid;
                                                String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                                byte paylaod[] = ble_make_external_mqtt(target_guid, ret2);
                                                MqttMsg msg = PanFactory.getProtocol().decode(topic, paylaod);
                                                if (((Pan) device).onBleReceived(msg))
                                                    AccountInfo.getInstance().getGuid().setValue(device.guid); //更新锅状态
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                case BleDecoder.CMD_COOKER_SET_RES: //设置灶状态返回
                                case BleDecoder.CMD_COOKER_TIME_RES: //设置定时关火返回
                                case BleDecoder.CMD_COOKER_LOCK_RES: { //设置童锁返回
                                    int rc = ByteUtils.toInt(ret2[2]);
                                    if (rc == 0) { //设置成功
                                        for (Device device : AccountInfo.getInstance().deviceList) {
                                            if (bleDevice.getMac().equals(device.mac) && device instanceof Stove) {
                                                StoveAbstractControl.getInstance().queryAttribute(device.guid);
//                                                Message msg = handler.obtainMessage();
//                                                msg.obj = device.guid;
//                                                handler.sendMessageDelayed(msg, 1000); //延时查询
                                                break;
                                            }
                                        }
                                    }
                                }
                                    break;
                                case BleDecoder.EVENT_POT_TEMPERATURE_DROP://锅温度骤变
                                case BleDecoder.EVENT_POT_TEMPERATURE_OV: //干烧预警
                                case BleDecoder.EVENT_POT_LINK_2_RH: {  //烟锅联动
                                    String target_guid = Plat.getPlatform().getDeviceOnlySign();
                                    String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                    byte payload[] = ble_make_external_mqtt(target_guid, ret2);
                                    MqttMsg msg = VentilatorFactory.getProtocol().decode(topic, payload);
                                }
                                    break;
                                case BleDecoder.CMD_RH_SET_INT: {//内部远程烟机交互
                                    String target_guid = Plat.getPlatform().getDeviceOnlySign();
                                    String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                    byte payload[] = ble_make_external_mqtt(target_guid, ret2);
//                                    MqttMsg msg = VentilatorFactory.getProtocol().decode(topic, paylaod);
                                }
                                    break;
                                case BleDecoder.CMD_COOKER_SET_INT: {//锅上报转发给灶
                                    for (Device device : AccountInfo.getInstance().deviceList) {
                                        if (bleDevice.getMac().equals(device.mac)) {
                                            String target_guid = device.guid;
                                            String topic = "/b/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                            VentilatorFactory.getTransmitApi().decode(topic, ble_make_external_mqtt(target_guid, ret2)); //发到锅解析温度
                                            break;
                                        }
                                    }
                                    //设置参数给灶
                                    StoveAbstractControl.getInstance().setStoveParams(BleDecoder.CMD_COOKER_SET_INT, ret2);
                                }
                                    break;
                                case BleDecoder.RSP_COOKER_SET_INT: { //灶上报转发给锅
                                    //设置参数给锅
                                    PanAbstractControl.getInstance().setPanParams(BleDecoder.RSP_COOKER_SET_INT, ret2);
                                }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BleDecoder.ROKI_UART_CMD_KEY_BROADCAST: //收到广播上报指令
                            for (Device device : AccountInfo.getInstance().deviceList) {
                                if (bleDevice.getMac().equals(device.mac)) {
                                    String target_guid = device.guid;
                                    String topic = "/b/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                    ble_mqtt_publish(topic, device.guid, ret2);
                                    break;
                                }
                            }
                            break;
                        default:
                            if (ByteUtils.toInt(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]) >= BleDecoder.ROKI_UART_CMD_KEY_DYNAMIC) { //收到外部指令(一般用于响应外部设备),通过MQTT转发出去
                                for (Device device : AccountInfo.getInstance().deviceList) {
                                    if (bleDevice.getMac().equals(device.mac)) {
                                        String target_guid = BlueToothManager.send_map.get(ByteUtils.toInt(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]));
                                        if (null != target_guid) {
                                            String topic = "/u/" + target_guid.substring(0, 5) + "/" + target_guid.substring(5);
                                            //移除消息
                                            BlueToothManager.send_map.remove(ByteUtils.toInt(ret2[BleDecoder.DECODE_CMD_KEY_OFFSET]));

                                            if (Plat.getPlatform().getDeviceOnlySign().equals(target_guid) && device instanceof Pan) { //烟机查询

                                                byte payload[] = ble_make_external_mqtt(target_guid, ret2);
                                                MqttMsg msg = PanFactory.getProtocol().decode(topic, payload);
                                                if (((Pan) device).onBleReceived(msg))
                                                    AccountInfo.getInstance().getGuid().setValue(device.guid); //更新锅状态
                                            } else
                                                //远程控制指令
                                                ble_mqtt_publish(topic, device.guid, ret2);

                                        }
                                        break;
                                    }
                                }
                            }

                            break;
                    }
                }
            } while(ret != null);
        }
    }
    //更新本地子设备信息
    public static void updateSubdevice(Device device) {
        Set<String> subDevices = MMKVUtils.getSubDevice();
        if (null != subDevices) {
            Iterator<String> iterator = subDevices.iterator();
            while (iterator.hasNext()) {
                String json = iterator.next();
                Device subDevice = new Gson().fromJson(json, Device.class);
                if (device.guid.equals(subDevice.guid))  //已经有记录
                    return;
            }
            subDevices.add(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(device));
            MMKVUtils.setSubDevice(subDevices);
        } else {
            Set<String> newSubDevices = new HashSet<>();
            newSubDevices.add(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(device));
            MMKVUtils.setSubDevice(newSubDevices);
        }
    }

    //将数据通过BLE指定通道发送出去
    public static void ble_write_no_resp(BleDevice bleDevice, byte[] data) {
        //TODO 这里实现BLE Write no response
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (bleDevice.getMac().equals(device.mac)) {
                BluetoothGattCharacteristic characteristic = null;
                if (device instanceof Pan) {
                    characteristic = ((Pan) device).characteristic;
                } else if (device instanceof Stove)
                    characteristic = ((Stove) device).characteristic;
                BlueToothManager.write_no_response(bleDevice, characteristic, data, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        LogUtils.e("onWriteSuccess");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        LogUtils.e("onWriteFailure");
                    }
                });
                break;
            }
        }
    }

    //延迟断开BLE指定通道
    public static void delay_disconnect_ble(BleDevice bleDevice) {
        //TODO 延迟一段时间后主动断开指定通道的BLE连接,主动断开时的操作和on_ble_disconnect_event_cb()方法内的操作一致
        BlueToothManager.disConnect(bleDevice);
    }
    //从ble收到的数据发到设备解析
    private static byte[] ble_make_external_mqtt(String sender_guid, byte[] ble_payload) {
        byte[] guid_bytes = sender_guid.getBytes();
        byte [] mqtt_payload = new byte[guid_bytes.length + ble_payload.length - 1];
        System.arraycopy(guid_bytes, 0, mqtt_payload, 0, guid_bytes.length);
        System.arraycopy(ble_payload, BleDecoder.DECODE_CMD_ID_OFFSET, mqtt_payload, guid_bytes.length,
                ble_payload.length - BleDecoder.DECODE_CMD_ID_OFFSET);
        return  mqtt_payload;
    }

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

    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (null != msg)
                StoveAbstractControl.getInstance().queryAttribute((String) msg.obj);
        }
    };
}
