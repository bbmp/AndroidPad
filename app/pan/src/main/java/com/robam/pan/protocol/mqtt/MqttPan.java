package com.robam.pan.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.pan.bean.Pan;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.constant.QualityKeys;
import com.robam.pan.device.PanFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//锅mqtt
public class MqttPan extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        //处理远程消息
        switch (msg.getID()) {
            case MsgKeys.SetPotTemp_Rep://查询锅回复
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(msg.getGuid()) && device instanceof Pan) { //当前锅
                        Pan pan = (Pan) device;
                        buf.putFloat((float) pan.panTemp); //温度
                        buf.put((byte) pan.status);//系统状态
                        buf.put((byte) 0x00); //参数个数
                        break;
                    }
                }
                break;
        }
    }

    private void decodeMsg(MqttMsg msg, byte[] payload, int offset) {
        //处理远程消息
        switch (msg.getID()) {
            case MsgKeys.GetPotTemp_Req: //查询锅
                //答复
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                MqttMsg newMsg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetPotTemp_Rep)
                        .setGuid(curGuid)
                        .setDt(Plat.getPlatform().getDt())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                        .build();
                MqttManager.getInstance().publish(newMsg, PanFactory.getProtocol());
                break;
        }
    }

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{
        //处理本机端消息
        switch (msg.getID()) {
            case MsgKeys.FanGetPanStatus_Res: {//查询返回
                //属性个数
                float temp = MsgUtils.bytes2FloatLittle(payload, offset);//锅温
                msg.putOpt(PanConstant.temp, temp);
                offset += 4;
                int status = MsgUtils.getByte(payload[offset++]);//状态
                msg.putOpt(PanConstant.status, status);
                int attributeNum = MsgUtils.getByte(payload[offset++]);//属性个数
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case QualityKeys.key1:
                            if (length == 1) {
                                MsgUtils.getByte(payload[offset]);
                            } else
                                MsgUtils.getString(payload, offset, length);
                            offset += length;
                            break;
                        case QualityKeys.key2:
                            int lidStatus = MsgUtils.getByte(payload[offset++]);//锅盖状态
                            msg.putOpt(PanConstant.lidStatus, lidStatus);
                            break;
                        case QualityKeys.key3:
                            int pValue = MsgUtils.getByte(payload[offset++]);//p档菜谱值
                            break;
                        case QualityKeys.key4:
                            int recipeid = MsgUtils.bytes2IntLittle(payload, offset);
                            offset += 4;
                            break;
                        case QualityKeys.key5:
                            int battery = MsgUtils.getByte(payload[offset++]);//电量
                            msg.putOpt(PanConstant.battery, battery);
                            break;
                        case QualityKeys.key6:
                            int mode = MsgUtils.getByte(payload[offset++]);//模式
                            break;
                        case QualityKeys.key7:
                            int localStatus = MsgUtils.getByte(payload[offset++]);//本地记录状态
                            break;
                        case QualityKeys.key8:
                            int runSeconds = MsgUtils.bytes2ShortLittle(payload, offset);//运行秒数
                            msg.putOpt(PanConstant.runTime, runSeconds);
                            offset += 2;
                            break;
                        case QualityKeys.key9:
                            int recipeStoveid = MsgUtils.getByte(payload[offset++]);//菜谱炉头id
                            break;
                        case QualityKeys.key10:
                            int setSeconds = MsgUtils.getByte(payload[offset++]);//设置秒数
                            msg.putOpt(PanConstant.setTime, setSeconds);
                            break;
                    }
                }
            }
                break;
            case MsgKeys.FanInteractPan_res: {
                int type = MsgUtils.getByte(payload[offset++]); //蓝牙品类
                int rc = MsgUtils.getByte(payload[offset++]);
                int temp = MsgUtils.bytes2ShortLittle(payload, offset); //锅温
                offset += 2;
                int attributeNum = MsgUtils.getByte(payload[offset++]);//参数个数
            }
                break;
        }

        decodeMsg(msg, payload, offset);
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //处理本机消息
        switch (msg.getID()) {
            case MsgKeys.FanGetPanStatus_Req: //属性查询
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.FanInteractPan_req:
                buf.put((byte) 0x00);// 蓝牙品类
                buf.put((byte) 0x01);//参数个数
                buf.put((byte) 0x01); //key
                buf.put((byte) 0x01);//
                buf.put((byte) msg.optInt(PanConstant.fryMode));
                break;
        }
        encodeMsg(buf, msg);
    }
}
