package com.robam.pan.protocol.mqtt;

import android.text.TextUtils;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;
import com.robam.common.manager.LiveDataBus;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
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
import java.nio.charset.StandardCharsets;

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
                        buf.putFloat(pan.panTemp); //温度
                        buf.put((byte) pan.workStatus);//系统状态
                        JSONArray jsonArray = new JSONArray();
                        try {
                            if (pan.fryMode != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key1);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.fryMode);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.lidStatus != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key2);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.lidStatus);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.orderNo != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key3);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.orderNo);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.recipeId != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key4);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.recipeId);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.battery != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key5);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.battery);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.mode != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key6);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.mode);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.localStatus != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key7);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.localStatus);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.runTime != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key8);
                                jsonObject.put(PanConstant.length, 2);
                                jsonObject.put(PanConstant.value, pan.runTime);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.bindStoveId != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key9);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.bindStoveId);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.setTime != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key10);
                                jsonObject.put(PanConstant.length, 2);
                                jsonObject.put(PanConstant.value, pan.setTime);
                                jsonArray.put(jsonObject);
                            }
                            if (!TextUtils.isEmpty(pan.electricParams)) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key11);
                                jsonObject.put(PanConstant.length, 6);
                                jsonObject.put(PanConstant.value, pan.electricParams);
                                jsonArray.put(jsonObject);
                            }
                            if (pan.runStoveId != -1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(PanConstant.key, QualityKeys.key12);
                                jsonObject.put(PanConstant.length, 1);
                                jsonObject.put(PanConstant.value, pan.runStoveId);
                                jsonArray.put(jsonObject);
                            }
                        } catch (Exception e) {}
                        buf.put((byte) jsonArray.length()); //参数个数
                        for (int i = 0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            buf.put((byte) jsonObject.optInt(PanConstant.key));
                            int len = jsonObject.optInt(PanConstant.length);
                            buf.put((byte) len);
                            if (len == 1)
                                buf.put((byte) jsonObject.optInt(PanConstant.value));
                            else if (len == 2)
                                buf.putShort((short) jsonObject.optInt(PanConstant.value));
                            else if (len == 4) {
                                buf.putInt(jsonObject.optInt(PanConstant.value));
                            } else
                                buf.put(jsonObject.optString(PanConstant.value).getBytes(StandardCharsets.UTF_8));
                        }
                        break;
                    }
                }
                break;
            case MsgKeys.GetPotSwitch_Rep: {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(msg.getGuid()) && device instanceof Pan) { //当前锅
                        Pan pan = (Pan) device;
                        buf.put((byte) pan.fanPan);//烟锅联动
                        break;
                    }
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
            case MsgKeys.SetPotSwitch_Req: {//烟锅联动开关查询
                //答复
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                MqttMsg newMsg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetPotSwitch_Rep)
                        .setGuid(curGuid)
                        .setDt(Plat.getPlatform().getDt())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                        .build();
                MqttManager.getInstance().publish(newMsg, PanFactory.getProtocol());
            }
            break;
            case MsgKeys.SetPotCom_Req:  //烟锅联动开关
            case MsgKeys.POT_P_MENU_Req:  //P档菜谱下发
            case MsgKeys.POT_Electric_Req: //无人锅电机命令
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
            case BleDecoder.CMD_COOKER_CLOUD_INT: { //锅上报云端
                float temp = MsgUtils.bytes2FloatLittle(payload, offset);//锅温
                offset += 4;
                int attributeNum = MsgUtils.getByte(payload[offset++]);//属性个数
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case 1:
                        case 2:
                        case 4:
                            offset += length;
                            break;
                        case 3:
                            int step = MsgUtils.getByte(payload[offset]);
                            if (step == 1)
                                LiveDataBus.get().with(PanConstant.ADD_STEP, Boolean.class).setValue(true);
                            offset += length;
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
                            int orderNo = MsgUtils.getByte(payload[offset++]);//p档菜谱值
                            msg.putOpt(PanConstant.pno, orderNo);//p档序号
                            break;
                        case QualityKeys.key4:
                            int recipeid = MsgUtils.bytes2IntLittle(payload, offset);
                            msg.putOpt(PanConstant.recipeId, recipeid);//菜谱id
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
                            msg.putOpt(PanConstant.localStatus, localStatus);
                            break;
                        case QualityKeys.key8:
                            int runSeconds = MsgUtils.bytes2ShortLittle(payload, offset);//运行秒数
                            msg.putOpt(PanConstant.runTime, runSeconds);
                            offset += 2;
                            break;
                        case QualityKeys.key9:
                            int bindStoveId = MsgUtils.getByte(payload[offset++]);//菜谱炉头id
                            msg.putOpt(PanConstant.bindStoveId, bindStoveId);
                            break;
                        case QualityKeys.key10:
                            int setSeconds = MsgUtils.getByte(payload[offset++]);//设置秒数
                            msg.putOpt(PanConstant.setTime, setSeconds);
                            break;
                        case QualityKeys.key11:
                            String params = MsgUtils.getString(payload, offset, 6); //电机旋转参数
                            msg.putOpt(PanConstant.electricParams, params);
                            offset += 6;
                            break;
                        case QualityKeys.key12:
                            int runStoveId = MsgUtils.getByte(payload[offset++]);//正在使用的炉头
                            msg.putOpt(PanConstant.runStoveId, runStoveId);
                            break;
                    }
                }
            }
                break;
            case MsgKeys.GetPotCom_Rep: { //设置烟锅联动返回
                int rc = MsgUtils.getByte(payload[offset++]);
                if (rc == 0)
                    PanAbstractControl.getInstance().queryFanPan(); //重新查一遍
            }
            break;
            case MsgKeys.POT_INTERACTION_Rep: {  //设置锅智能互动参数返回
                int rc = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(PanConstant.interaction, rc);
                if (rc == 0 && !TextUtils.isEmpty(HomePan.getInstance().guid))
                    PanAbstractControl.getInstance().queryAttribute(HomePan.getInstance().guid);
            }
                break;
            case MsgKeys.GetPotSwitch_Rep: {//烟锅联动查询回复
                int rc = MsgUtils.getByte(payload[offset++]);

                msg.putOpt(PanConstant.fanpan, rc);
            }
                break;
            case MsgKeys.POT_P_MENU_Rep: //p档菜谱
            case MsgKeys.POT_CURVETEMP_Rep: {//设置灶参数返回
                int rc = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(PanConstant.stoveParams, rc);
            }
            break;
            case MsgKeys.POT_Electric_Rep: //p档菜谱
            case MsgKeys.POT_CURVEElectric_Rep: { //设置锅参数返回
                int rc = MsgUtils.getByte(payload[offset++]);

                msg.putOpt(PanConstant.panParams, rc);
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
            case MsgKeys.SetPotCom_Req: { //设置联动开关
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.optInt(PanConstant.fanpan)); //烟锅联动开关
            }
                break;
            case MsgKeys.SetPotSwitch_Req: //烟锅联动开关查询
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
                buf.putInt(msg.optInt(PanConstant.pno)); //p档菜谱序号
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
                buf.putInt(msg.optInt(PanConstant.recipeId)); //菜谱id ，0为曲线还原，4个字节
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
                buf.putInt(msg.optInt(PanConstant.pno)); //p档菜谱序号
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                int bleVer = msg.optInt(PanConstant.bleVer);
                buf.put((byte) jsonArray.length()); //参数个数
                if (bleVer <= 3) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                        buf.put((byte) 3);
                        buf.put((byte) jsonObject.optInt(PanConstant.fryMode)); //搅拌参数
                        buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //当前步骤持续时间
                    }
                } else {
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
                    buf.put((byte) 1);//其它参数个数
                    buf.put((byte) 'A'); //key
                    buf.put((byte) 4); //length
                    buf.putInt(msg.optInt(PanConstant.recipeId));//菜谱id
                }
            }
            break;
            case MsgKeys.POT_CURVEElectric_Req: {//曲线还原锅参数设置
                buf.putInt(msg.optInt(PanConstant.recipeId));// 菜谱id ，0为曲线还原，4个字节
                int bleVer = msg.optInt(PanConstant.bleVer);
                JSONArray jsonArray = msg.optJSONArray(PanConstant.steps);
                buf.put((byte) jsonArray.length()); //参数个数
                if (bleVer <= 3) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        buf.put((byte) jsonObject.optInt(PanConstant.key)); //步骤key
                        buf.put((byte) 3);
                        buf.put((byte) jsonObject.optInt(PanConstant.fryMode)); //搅拌参数
                        buf.putShort((short) jsonObject.optInt(PanConstant.stepTime)); //当前步骤持续时间
                    }
                } else {
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
            }
                break;
        }
        encodeMsg(buf, msg);
    }
}
