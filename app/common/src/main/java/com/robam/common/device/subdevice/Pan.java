package com.robam.common.device.subdevice;

import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.constant.PanConstant;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;

import java.util.Arrays;

public class Pan extends Device {
    public int msgId;
    //系统状态
    public int sysytemStatus;
    //工作模式
    public int mode = -1;
    //本地记录状态
    public int localStatus = -1;
    //运行秒数
    public int runTime = -1;
    //设置秒数
    public int setTime = -1;
    //电机参数
    public String electricParams;
    //绑定炉头id
    public int bindStoveId = -1;
    //运行炉头id
    public int runStoveId = -1;
    //锅温度
    public float panTemp;
    //锅电量
    public int battery = -1;
    //搅拌模式
    public int fryMode = -1;
    //锅盖状态
    public int lidStatus = -1;
    //蓝牙设备
    public BleDevice bleDevice;
    //蓝牙特征符
    public BluetoothGattCharacteristic characteristic;
    //蓝牙解析
    public BleDecoder bleDecoder;
    //p档序号
    public int orderNo = -1;
    //菜谱id
    public int recipeId = -1;
    //烟锅联动开关
    public int fanPan;

    public Pan(Device device) {
        this.ownerId = device.ownerId;
        this.mac = device.mac;
        this.guid = device.guid;
        this.bid = device.bid;
        this.dc = device.dc;
        this.dt = device.dt;
        this.dp = device.dp;
        this.displayType = device.displayType;
        this.categoryName = device.categoryName;
        this.subDevices = device.subDevices;
    }

    public Pan(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }


    @Override
    public boolean onMsgReceived(MqttMsg msg) {
        if (null != msg && msg.getID() != MsgKeys.GetPotTemp_Req && msg.getID() != BleDecoder.CMD_COOKER_SET_INT) { //非远程查询
            byte[] mqtt_data = msg.getBytes();
            String send_guid = msg.getGuid();
            int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
            Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
            //转化成蓝牙包
            BleDecoder.ExternBleData data = BleDecoder.make_external_send_packet(cmd_id, mqtt_payload);
            //保存回复guid
            BlueToothManager.send_map.put(data.cmd_key, send_guid);
//        ble_write_no_resp(dev.getChan(), BleDecoder.ByteArraysTobyteArrays(data.payload));
            //发送蓝牙数据
            BlueToothManager.write_no_response(bleDevice, characteristic, BleDecoder.ByteArraysTobyteArrays(data.payload), new BleWriteCallback() {

                @Override
                public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                }

                @Override
                public void onWriteFailure(final BleException exception) {

                }
            });
        }
        return super.onMsgReceived(msg);
    }

    //收到蓝牙消息
    public boolean onBleReceived(MqttMsg msg) {
        if (null != msg && msg.has(PanConstant.systemStatus)) {
            queryNum = 0; //查询超过一次无响应离线
            status = Device.ONLINE;
            panTemp = (float) msg.opt(PanConstant.temp);
            sysytemStatus = msg.optInt(PanConstant.systemStatus);
            fryMode = -1;
            lidStatus = -1;
            orderNo = -1;
            recipeId = -1;
            battery = -1;
            mode = -1;
            localStatus = -1;
            runTime = -1;
            bindStoveId = -1;
            setTime = -1;
            electricParams = null;
            runStoveId = -1;
            if (msg.has(PanConstant.fryMode))
                fryMode = msg.optInt(PanConstant.fryMode);
            if (msg.has(PanConstant.lidStatus))
                lidStatus = msg.optInt(PanConstant.lidStatus);
            if (msg.has(PanConstant.pno))
                orderNo = msg.optInt(PanConstant.pno);
            if (msg.has(PanConstant.recipeId))
                recipeId = msg.optInt(PanConstant.recipeId);
            if (msg.has(PanConstant.battery))
                battery = msg.optInt(PanConstant.battery);
            if (msg.has(PanConstant.mode))
                mode = msg.optInt(PanConstant.mode);
            if (msg.has(PanConstant.localStatus))
                localStatus = msg.optInt(PanConstant.localStatus);
            if (msg.has(PanConstant.runTime))
                runTime = msg.optInt(PanConstant.runTime);
            if (msg.has(PanConstant.bindStoveId))
                bindStoveId = msg.optInt(PanConstant.bindStoveId);
            if (msg.has(PanConstant.setTime))
                setTime = msg.optInt(PanConstant.setTime);
            if (msg.has(PanConstant.electricParams))
                electricParams = msg.optString(PanConstant.electricParams);
            if (msg.has(PanConstant.runStoveId))
                runStoveId = msg.optInt(PanConstant.runStoveId);
            return true;
        } else if (null != msg && msg.has(PanConstant.panParams)) { //设置锅参数返回
            int rc = msg.optInt(PanConstant.panParams);
            if (rc == 0)
                msgId = MsgKeys.POT_CURVETEMP_Req;
            //这里不返回true
        } else if (null != msg && msg.has(PanConstant.stoveParams)) { //设置灶参数返回
            int rc = msg.optInt(PanConstant.panParams);
            if (rc == 0)
                msgId = MsgKeys.POT_INTERACTION_Req;
        } else if (null != msg && msg.has(PanConstant.interaction)) { //设置互动参数返回
            int rc = msg.optInt(PanConstant.interaction);
            if (rc == 0)
                msgId = MsgKeys.POT_INTERACTION_Rep;
        } else if (null != msg && msg.has(PanConstant.fanpan)) { //烟锅联动状态
            fanPan = msg.optInt(PanConstant.fanpan);
//            IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
//            if (null != iPublicVentilatorApi) {
//                iPublicVentilatorApi.setSmartSet();
//            }
        }
        return false;
    }
}
