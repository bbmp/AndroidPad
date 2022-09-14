package com.robam.ventilator.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

//烟机mqtt私有协议
public class MqttVentilator extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;


    private Map decodeMsg(int msgId, String guid, byte[] payload, int offset) {
        Map map = new HashMap();
//从payload中取值角标
        //远程被控制
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.GetFanStatus_Rep: //烟机查询返回
                short fanStatus =
                        ByteUtils.toShort(payload[offset++]);
                map.put(VentilatorConstant.FanStatus, fanStatus);
                short fanLevel =
                        ByteUtils.toShort(payload[offset++]);
                short fanLight =
                        ByteUtils.toShort(payload[offset++]);
                short needClean =
                        ByteUtils.toShort(payload[offset++]);

//                short argumentLength = (short) (payload.length - offset);

//                short aValue = ByteUtils.toShort(payload[offset]);
                break;
            default:

                break;
        }
        return map;
    }
    //烟机端的协议
    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        //远程控制其他设备或通知上报

        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                buf.put((byte) 1);
                buf.put("0000000000".getBytes());
                buf.put(VentilatorFactory.getPlatform().getMac().getBytes()); //mac
                buf.put(msg.getGuid().getBytes());
                buf.put((byte) VentilatorFactory.getPlatform().getMac().length());
                buf.put(VentilatorFactory.getPlatform().getMac().getBytes());
                buf.put((byte) 1);
                buf.put((byte) 4);
                buf.put((byte) 1);
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getDeviceAttribute_Req:  //属性查询
                buf.put((byte) 0x00);
                break;
            case MsgKeys.GetFanStatus_Req: //烟机状态查询
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.GetFanStatus_Rep: //读取状态响应
                buf.put(HomeVentilator.getInstance().status);//状态
                buf.put(HomeVentilator.getInstance().gears); //挡位
                buf.put(HomeVentilator.getInstance().lightOn); //灯开关
                buf.put((byte) 0); //是否需要清洗
                buf.put((byte) 0); //定时时间
                buf.put((byte) (AccountInfo.getInstance().getConnect().getValue()?1:0));//联网状态
                buf.put((byte) 0); //参数个数
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
        }
    }

    @Override
    protected Map onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        switch (msgId) {
            case MsgKeys.GetFanStatus_Req: { //查询烟机
                //控制端类型
                short terminalType = ByteUtils.toShort(payload[offset]);
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetFanStatus_Rep)
                        .setGuid(VentilatorFactory.getPlatform().getDeviceOnlySign())
                        .setDt(VentilatorFactory.getPlatform().getDt())
                        .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, DeviceUtils.getDeviceTypeId(srcGuid), DeviceUtils.getDeviceNumber(srcGuid)))
                        .build();
                MqttManager.getInstance().publish(msg, VentilatorFactory.getProtocol());
            }
                break;
            case MsgKeys.SetFanStatus_Req: { //设置烟机
                //控制端类型
                short terminalType = ByteUtils.toShort(payload[offset++]);
                //userid
                ByteUtils.toString(payload, offset, 10);
                offset += 10;
                //工作状态
                short status = ByteUtils.toShort(payload[offset++]);

                VentilatorAbstractControl.getInstance().setFanStatus(status);
            }
                break;
            case MsgKeys.SetFanLevel_Req: {//设置烟机挡位
                //控制端类型
                short terminalType = ByteUtils.toShort(payload[offset++]);
                //
                //userid
                ByteUtils.toString(payload, offset++, 10);
                offset += 10;
                //挡位
                short gear = ByteUtils.toShort(payload[offset++]);

                VentilatorAbstractControl.getInstance().setFanGear(gear);
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
            }
                break;
            case MsgKeys.RestFanCleanTime_Req: {
                //控制端类型
                short terminalType = ByteUtils.toShort(payload[offset++]);
                //userid
                ByteUtils.toString(payload, offset++, 10);
                offset += 10;
                //参数个数
                short num = ByteUtils.toShort(payload[offset++]);
            }
                break;
        }
        return decodeMsg(msgId, srcGuid, payload, offset);
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        encodeMsg(buf, msg);
    }
}
