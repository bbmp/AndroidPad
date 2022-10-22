package com.robam.pan.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.constant.PanConstant;
import com.robam.pan.constant.QualityKeys;
import com.robam.pan.device.HomePan;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.device.PanFactory;

import org.json.JSONArray;
import org.json.JSONObject;

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
                        buf.put((byte) pan.workStatus);//系统状态
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
            case MsgKeys.GetPotTemp_Req: {//查询锅,避免蓝牙查询频繁，返回当前状态
                //答复
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                MqttMsg newMsg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetPotTemp_Rep)
                        .setGuid(curGuid)
                        .setDt(Plat.getPlatform().getDt())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                        .build();
                MqttManager.getInstance().publish(newMsg, PanFactory.getProtocol());
            }
                break;
            case MsgKeys.POT_INTERACTION_Req: //智能互动
            case MsgKeys.POT_CURVETEMP_Req:   //曲线还原灶参数下发
            case MsgKeys.POT_CURVEElectric_Req: {  //曲线还原锅参数下发
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                PanAbstractControl.getInstance().remoteControl(curGuid, payload);
            }
                break;
        }
    }

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{
        //处理本机端消息
        switch (msg.getID()) {
            case BleDecoder.CMD_COOKER_SET_INT: {
                int type = MsgUtils.getByte(payload[offset++]);//蓝牙品类
                int attributeNum = MsgUtils.getByte(payload[offset++]); //参数个数
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case 1:
                            float temp = MsgUtils.bytes2ShortLittle(payload, offset);
                            msg.putOpt(PanConstant.temp, temp);
                            offset += 2;
                            break;
                    }
                }
            }
            break;
            case MsgKeys.SetPotTemp_Rep: {//查询返回
                //属性个数
                float temp = MsgUtils.bytes2FloatLittle(payload, offset);//锅温
                msg.putOpt(PanConstant.temp, temp);
                offset += 4;
                int systemStatus = MsgUtils.getByte(payload[offset++]);//状态
                msg.putOpt(PanConstant.systemStatus, systemStatus);
                int attributeNum = MsgUtils.getByte(payload[offset++]);//属性个数
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case QualityKeys.key1:
                            if (length == 1) {
                                int mode = MsgUtils.getByte(payload[offset]);
                                msg.putOpt(PanConstant.fryMode, mode); //搅拌模式
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
                            msg.putOpt(PanConstant.mode, mode);
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
            case MsgKeys.ActiveTemp_Rep: //锅温度上报
                break;
            case MsgKeys.POT_INTERACTION_Rep: {  //设置锅智能互动参数返回
                int rc = MsgUtils.getByte(payload[offset++]);
                if (rc == 0)
                    PanAbstractControl.getInstance().queryAttribute(HomePan.getInstance().guid);
            }
                break;
            case MsgKeys.POT_CURVETEMP_Rep: //设置灶参数返回
            case MsgKeys.POT_CURVEElectric_Rep: { //设置锅参数返回
                int rc = MsgUtils.getByte(payload[offset++]);
                if (rc == 0)
                    ;
            }
            break;
        }

        decodeMsg(msg, payload, offset);
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //处理本机消息
        switch (msg.getID()) {
            case MsgKeys.GetPotTemp_Req: //属性查询
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.POT_INTERACTION_Req: {
//                buf.put((byte) 0x00);// 蓝牙品类

                JSONArray jsonArray = msg.optJSONArray(PanConstant.interaction);
                buf.put((byte) jsonArray.length());//参数个数
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    buf.put((byte) jsonObject.optInt(PanConstant.key)); //key
                    byte[] value = (byte[]) jsonObject.opt(PanConstant.value);
                    buf.put((byte) value.length); //
                    buf.put(value);
                }
            }
                break;
            case MsgKeys.POT_P_MENU_Req: { //p档菜谱灶参数设置
                buf.putFloat(msg.optInt(PanConstant.pno)); //p档菜谱序号
                buf.put((byte) msg.optInt(PanConstant.stoveId)); //炉头id
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                buf.put((byte) jsonArray.length()); //参数个数
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                    buf.put((byte) 6); //length
                    buf.put((byte) jsonObject.optInt(PanConstant.control));//控制方式
                    buf.put((byte) jsonObject.optInt(PanConstant.level)); //灶具挡位
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTemp)); //温度
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //步骤时间
                }
            }
            break;
            case MsgKeys.POT_CURVETEMP_Req: { //曲线还原灶参数设置
                buf.putFloat(msg.optInt(PanConstant.recipeId)); //菜谱id ，0为曲线还原，4个字节
                buf.put((byte) msg.optInt(PanConstant.stoveId)); //炉头id
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                buf.put((byte) jsonArray.length()); //参数个数
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                    buf.put((byte) 6); //length
                    buf.put((byte) jsonObject.optInt(PanConstant.control));//控制方式
                    buf.put((byte) jsonObject.optInt(PanConstant.level)); //灶具挡位
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTemp)); //温度
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //步骤时间
                }
            }
            break;
            case MsgKeys.POT_Electric_Req: { //p档菜谱锅参数设置
                buf.putFloat(msg.optInt(PanConstant.pno)); //p档菜谱序号
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                buf.put((byte) jsonArray.length()); //参数个数
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                    buf.put((byte) 7);
                    buf.put((byte) jsonObject.optInt(PanConstant.fryMode)); //搅拌参数
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //当前步骤持续时间
                    buf.put((byte) 0);   //正转转速
                    buf.put((byte) 0);   //反转转速
                    buf.put((byte) 0); //正转时间
                    buf.put((byte) 0); //反转时间
                }
            }
            break;
            case MsgKeys.POT_CURVEElectric_Req: {//曲线还原锅参数设置
                buf.putFloat(msg.optInt(PanConstant.recipeId));// 菜谱id ，0为曲线还原，4个字节
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                buf.put((byte) jsonArray.length()); //参数个数
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                    buf.put((byte) 7);
                    buf.put((byte) jsonObject.optInt(PanConstant.fryMode)); //搅拌参数
                    buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //当前步骤持续时间
                    buf.put((byte) 0);   //正转转速
                    buf.put((byte) 0);   //反转转速
                    buf.put((byte) 0); //正转时间
                    buf.put((byte) 0); //反转时间
                }
            }
                break;
        }
        encodeMsg(buf, msg);
    }
}
