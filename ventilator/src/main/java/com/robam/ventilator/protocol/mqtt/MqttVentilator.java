package com.robam.ventilator.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.protocol.ble.BleVentilator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//烟机mqtt私有协议
public class MqttVentilator extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;


    private void decodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
//从payload中取值角标
        //内部命令
        switch (msg.getID()) {
            case BleDecoder.EVENT_POT_TEMPERATURE_DROP: //锅温度骤降且烟锅联动开启,且烟机不工作烟机爆炒档
                if (HomeVentilator.getInstance().gear == (byte) 0xA0) { //不工作
                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机
                        VentilatorAbstractControl.getInstance().powerOnGear(VentilatorConstant.FAN_GEAR_FRY);
                        Plat.getPlatform().screenOn();
                        Plat.getPlatform().openPowerLamp();
                    } else
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_FRY);
                }
                break;
            case BleDecoder.EVENT_POT_TEMPERATURE_OV: //防干烧预警 锅温280以上且烟锅联动开启
                //关闭灶具
                StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, IPublicStoveApi.STOVE_LEFT, 0x00, StoveConstant.STOVE_CLOSE);
                StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, IPublicStoveApi.STOVE_RIGHT, 0x00, StoveConstant.STOVE_CLOSE);
                break;
            case BleDecoder.EVENT_POT_LINK_2_RH://烟锅联动锅温50以上，烟机未开且烟锅联动开启
                //烟机开2挡
                if (HomeVentilator.getInstance().gear == (byte) 0xA0) { //不工作
                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机
                        VentilatorAbstractControl.getInstance().powerOnGear(VentilatorConstant.FAN_GEAR_MID);
                        Plat.getPlatform().screenOn();
                        Plat.getPlatform().openPowerLamp();
                    } else
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                }
                break;
            case BleDecoder.CMD_RH_SET_INT: //内部远程烟机交互
                short type = ByteUtils.toShort(payload[offset++]); //蓝牙品类
                //属性个数
                short attributeNum = ByteUtils.toShort(payload[offset]);
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case 1:
                            int gear = MsgUtils.getByte(payload[offset++]); //请求联动挡位
                            msg.putOpt(VentilatorConstant.FanGear, gear);
                            break;
                        case 2: {
                            int min = 0;
                            if (length == 1)
                                min = MsgUtils.getByte(payload[offset++]); //请求定时关机时间
                            else if (length == 2) {
                                min = MsgUtils.bytes2ShortLittle(payload, offset);
                                offset += 2;
                            } else if (length == 4) {
                                min = MsgUtils.bytes2IntLittle(payload, offset);
                                offset += 4;
                            }
                            msg.putOpt(VentilatorConstant.DelayTime, min);
                        }
                            break;
                        case 101:
                            int num101 = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(VentilatorConstant.PRecipe1, num101);
                            break;
                        case 102:
                            int num102 = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(VentilatorConstant.PRecipe2, num102);
                            break;
                        case 103:
                            int num103 = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(VentilatorConstant.PRecipe3, num103);
                            break;
                        case 104:
                            int num104 = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(VentilatorConstant.PRecipe4, num104);
                            break;
                        case 105:
                            int num105 = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(VentilatorConstant.PRecipe5, num105);
                            break;
                    }
                }
                if (msg.has(VentilatorConstant.FanGear) || msg.has(VentilatorConstant.DelayTime)) { //烟机回设备
                    if (type != 0xff) {
                        MqttMsg newMsg = new MqttMsg.Builder()
                                .setMsgId((short) BleDecoder.RSP_RH_SET_INT)
                                .setGuid(Plat.getPlatform().getDeviceOnlySign())
                                .setDt(Plat.getPlatform().getDt())
                                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                                .build();
                        newMsg.putOpt(VentilatorConstant.BleType, type);
                        MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                    }
                }
                if (msg.has(VentilatorConstant.PRecipe1) || msg.has(VentilatorConstant.PRecipe2) || msg.has(VentilatorConstant.PRecipe3) ||
                        msg.has(VentilatorConstant.PRecipe4) || msg.has(VentilatorConstant.PRecipe5)) { //无人锅上报转发到云端

                    String topic = "/b/" + msg.getGuid().substring(0, 5) + "/" + msg.getGuid().substring(5);
                    byte[] mqtt_data = payload;
                    mqtt_data[BleDecoder.GUID_LEN] = (byte) MsgKeys.PanReportStatistics_Req; //修改命令号
                    MqttManager.getInstance().publish(topic, mqtt_data);
                }
                break;

            default:

                break;
        }
    }
    //烟机端的协议
    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        //远程控制其他设备或通知上报

        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                buf.put((byte) msg.optInt(ComnConstant.DEVICE_NUM));
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put(Plat.getPlatform().getMac().getBytes()); //mac

                buf.put(msg.getGuid().getBytes());
                buf.put((byte) Plat.getPlatform().getMac().length());
                buf.put(Plat.getPlatform().getMac().getBytes());
                buf.put((byte) 1);
                buf.put((byte) 9);
                buf.put((byte) 1);

                if (null != msg.optJSONArray(VentilatorConstant.SUB_DEVICES)) {
                    JSONArray jsonArray = msg.optJSONArray(VentilatorConstant.SUB_DEVICES);
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String guid = jsonObject.optString(VentilatorConstant.DEVICE_GUID);
                        buf.put(guid.getBytes());
                        String biz = jsonObject.optString(VentilatorConstant.DEVICE_BIZ);
                        buf.put((byte) biz.length());
                        buf.put(biz.getBytes());
                        buf.put((byte) 0);
                        buf.put((byte) 0);
                        buf.put((byte) jsonObject.optInt(VentilatorConstant.DEVICE_STATUS));
                    }
                }

                buf.put((byte) 0); //蓝牙版本
                buf.put((byte) 0); //参数个数
                break;
            case MsgKeys.GetFanStatus_Req: //烟机状态查询
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.GetFanStatus_Rep: //属性查询响应
                buf.put(HomeVentilator.getInstance().status);//状态

                buf.put(HomeVentilator.getInstance().gear); //挡位

                buf.put(HomeVentilator.getInstance().lightOn); //灯开关

                buf.put((byte) 0); //是否需要清洗

                buf.put((byte) (AccountInfo.getInstance().getConnect().getValue()?1:0));//联网状态
                buf.put((byte) 0);//参数个数

                break;
            case MsgKeys.SetFanStatus_Rep: //设置烟机应答
                buf.put((byte) 0); //rc
                break;
            case MsgKeys.SetFanLevel_Rep: //设置挡位应答
                buf.put((byte) 0);
                break;
            case MsgKeys.SetFanLight_Rep: //设置灯开关应答
                buf.put((byte) 0);
                break;
            case MsgKeys.SetFanAllParams_Rep: //设置整体响应
                buf.put((byte) 0);
                break;
            case MsgKeys.RestFanCleanTime_Rep: //重置烟机清洗计时回复
                buf.put((byte) 0);
                break;
            case MsgKeys.SetFanTimeWork_Rep: //设置定时工作响应
                buf.put((byte) 0);
                break;
        }
    }

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{
        String targetGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum();
        LogUtils.e("ventilator targuid = " + targetGuid);
        //控制烟机需校验是否是本机
        if (targetGuid.equals(Plat.getPlatform().getDeviceOnlySign())) {
            switch (msg.getID()) {
                case MsgKeys.GetFanStatus_Req: { //查询烟机
                    //属性个数
                    short attributeNum = ByteUtils.toShort(payload[offset]);
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.GetFanStatus_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.SetFanStatus_Req: { //设置烟机
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //userid
                    ByteUtils.toString(payload, offset, 10);
                    offset += 10;
                    short workStatus = ByteUtils.toShort(payload[offset++]);
                    switch (workStatus) {
                        case 0: //关机
                            VentilatorAbstractControl.getInstance().shutDown();
                            break;
                        case 1: //开机
                            VentilatorAbstractControl.getInstance().powerOn();
                            break;
                        case 4: //清洗锁定
                            VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_CLOSE);
                            break;
                    }
                    short attributeNum = ByteUtils.toShort(payload[offset++]);
                    while (attributeNum > 0) {
                        attributeNum--;
                    }
                    //设置响应
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanStatus_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.SetFanLevel_Req: {//设置烟机挡位
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //
                    //userid
                    ByteUtils.toString(payload, offset, 10);
                    offset += 10;
                    //挡位
                    short gear = ByteUtils.toShort(payload[offset++]);

                    VentilatorAbstractControl.getInstance().setFanGear(gear);
                    //设置烟机挡位回复
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanLevel_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.SetFanLight_Req: { //设置烟机灯
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //
                    //userid
                    ByteUtils.toString(payload, offset++, 10);
                    offset += 10;
                    //灯开关
                    short light = ByteUtils.toShort(payload[offset++]);

                    VentilatorAbstractControl.getInstance().setFanLight(light);
                    //设置烟机灯回复
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanLight_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.SetFanAllParams_Req: { //设置烟机整体状态
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //userid
                    ByteUtils.toString(payload, offset++, 10);
                    offset += 10;
                    //挡位
                    short gear = ByteUtils.toShort(payload[offset++]);
                    //灯开关
                    short light = ByteUtils.toShort(payload[offset++]);

                    VentilatorAbstractControl.getInstance().setFanAll(gear, light);
                    //设置整体状态回复
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanAllParams_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.RestFanCleanTime_Req: { //重置烟机清洗
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //userid
                    ByteUtils.toString(payload, offset++, 10);
                    offset += 10;
                    //参数个数
                    short num = ByteUtils.toShort(payload[offset++]);
                    //重置烟机清洗响应
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.RestFanCleanTime_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.DeviceConnected_Noti: { //子设备更新

                }
                break;
                case MsgKeys.SetFanTimeWork_Req: { //设置烟机定时工作
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //挡位
                    short gear = ByteUtils.toShort(payload[offset++]);
                    //定时时间
                    short time = ByteUtils.toShort(payload[offset++]);
                    //参数个数
                    short attributeNum = ByteUtils.toShort(payload[offset++]);
                    //定时工作响应
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanTimeWork_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
            }
        }
        //其他通知类消息
        decodeMsg(msg, payload, offset);
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //内部命令
        switch (msg.getID()) {
            case BleDecoder.RSP_RH_SET_INT: //内部交互回复
                buf.put((byte) msg.optInt(VentilatorConstant.BleType)); //蓝牙品类
                buf.put((byte) 0x00);//rc
                buf.put(HomeVentilator.getInstance().gear);//烟机挡位
                buf.put((byte) (AccountInfo.getInstance().getConnect().getValue() ? 1: 0));//联网状态
                break;
        }
        encodeMsg(buf, msg);
    }
}
