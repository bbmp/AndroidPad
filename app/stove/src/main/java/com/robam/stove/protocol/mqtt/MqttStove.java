package com.robam.stove.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;

//灶具mqtt实现
public class MqttStove extends MqttPublic {

    private void decodeMsg(MqttMsg msg, byte[] payload, int offset) {
        //处理远程消息
        switch (msg.getID()) {
            case MsgKeys.GetStoveStatus_Req: { //远程查询灶状态,避免蓝牙查询频繁，返回当前状态
                //答复
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                MqttMsg newMsg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetStoveStatus_Rep)
                        .setGuid(curGuid)
                        .setDt(Plat.getPlatform().getDt())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                        .build();
                MqttManager.getInstance().publish(newMsg, StoveFactory.getProtocol());
            }
                break;
            case MsgKeys.SetStoveLock_Req: //设置童锁
            case MsgKeys.SetStoveStatus_Req: //设置灶具状态
            case MsgKeys.SetStoveLevel_Req: //设置挡位
            case MsgKeys.SetStoveShutdown_Req: //定时设置
            case MsgKeys.setStoveInteraction_Req: {  //设置灶具智能互动
                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
                StoveAbstractControl.getInstance().remoteControl(curGuid, payload);
            }
                break;
        }
    }
    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case BleDecoder.EVENT_IH_POWER_CHANGED_INT: { //灶具挡位变化
                int maxLevel = MsgUtils.getByte(payload[offset++]); //最大挡位值
                msg.putOpt(StoveConstant.stoveNum, 2); //固定写死
                short attributeNum = ByteUtils.toShort(payload[offset++]);
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    int leftLevel = MsgUtils.getByte(payload[offset++]);
                    msg.putOpt(StoveConstant.leftLevel, leftLevel); //左炉头
                    int rightLevel = MsgUtils.getByte(payload[offset++]);
                    msg.putOpt(StoveConstant.rightLevel, rightLevel); //右炉头
                    //通知烟机挡位变化
                    IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                    if (null != iPublicVentilatorApi) {
                        iPublicVentilatorApi.stoveLevelChanged(msg.getrTopic().getDeviceType()+msg.getrTopic().getSignNum(), leftLevel, rightLevel);
                    }
                }
            }
            break;
            case MsgKeys.GetStoveStatus_Rep: {//查询灶状态返回
                int stoveNum = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.stoveNum, stoveNum);
                int lockStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.lockStatus, lockStatus);
                //左灶
                int workStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.leftStatus, workStatus);
                int leftLevel = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.leftLevel, leftLevel);
                int time = MsgUtils.bytes2ShortLittle(payload, offset);
                msg.putOpt(StoveConstant.leftTime, time);
                offset += 2;
                int alarmStatus = MsgUtils.getByte(payload[offset++]);//报警状态
                msg.putOpt(StoveConstant.leftAlarm, alarmStatus);
                //右灶
                workStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.rightStatus, workStatus);
                int rightLevel = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.rightLevel, rightLevel);
                time = MsgUtils.bytes2ShortLittle(payload, offset);
                msg.putOpt(StoveConstant.rightTime, time);
                offset += 2;
                alarmStatus = MsgUtils.getByte(payload[offset++]);//报警状态
                msg.putOpt(StoveConstant.rightAlarm, alarmStatus);

                short attributeNum = ByteUtils.toShort(payload[offset++]);
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case 'A': //左灶菜谱
                            MsgUtils.getString(payload, offset, 3);
                            offset += 3;
                            break;
                        case 'B': //右灶菜谱
                            MsgUtils.getString(payload, offset, 3);
                            offset += 3;
                            break;
                        case 'E': {//左灶温度
                            float leftTemp = MsgUtils.bytes2FloatLittle(payload, offset);
                            msg.putOpt(StoveConstant.leftTemp, leftTemp);
                            offset += 4;
                        }
                            break;
                        case 'F': //右灶温度
                            float rightTemp = MsgUtils.bytes2FloatLittle(payload, offset);
                            msg.putOpt(StoveConstant.rightTemp, rightTemp);
                            offset += 4;
                            break;
                        case 'I': { //左灶秒数
                            short leftTime = MsgUtils.bytes2ShortLittle(payload, offset);
                            msg.putOpt(StoveConstant.leftSetTime, leftTime);
                            offset += 2;
                        }
                        break;
                        case 'J': {//右灶秒数
                            short rightTime = MsgUtils.bytes2ShortLittle(payload, offset);
                            msg.putOpt(StoveConstant.rightSetTime, rightTime);
                            offset += 2;
                        }
                        break;
                        case 'K': { //左灶模式
                            int leftMode = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(StoveConstant.leftMode, leftMode);
                        }
                        break;
                        case 'L': { //右灶模式
                            int rightMode = MsgUtils.getByte(payload[offset++]);
                            msg.putOpt(StoveConstant.rightMode, rightMode);
                        }
                        break;
                    }
                }
                //通知烟机挡位变化
                IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                if (null != iPublicVentilatorApi) {
                    iPublicVentilatorApi.stoveLevelChanged(msg.getrTopic().getDeviceType()+msg.getrTopic().getSignNum(), leftLevel, rightLevel);
                }
            }
                break;

        }
        decodeMsg(msg, payload, offset);
    }

    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        //处理远程消息
        switch (msg.getID()) {
            case MsgKeys.GetStoveStatus_Rep: //远程查询回复
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(msg.getGuid()) && device instanceof Stove) { //当前灶具
                        Stove stove = (Stove) device;
                        buf.put((byte) 0x02); //炉头个数
                        buf.put((byte) stove.lockStatus);//童锁
                        buf.put((byte) stove.leftStatus);// 工作状态
                        buf.put((byte) stove.leftLevel);// 功率等级
                        buf.putShort((short) stove.leftTimeHours); //定时剩余秒数
                        buf.put((byte) stove.leftAlarm);// 报警状态
                        buf.put((byte) stove.rightStatus);// 工作状态
                        buf.put((byte) stove.rightLevel);// 功率等级
                        buf.putShort((short) stove.rightTimeHours); //定时剩余秒数
                        buf.put((byte) stove.rightAlarm);// 报警状态
                        buf.put((byte) 0x00); //参数个数
                        break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //处理本机消息
        switch (msg.getID()) {
            case MsgKeys.GetStoveStatus_Req: //本机查询灶状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                break;

            case MsgKeys.SetStoveStatus_Req: //本地设置灶具状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.optInt(StoveConstant.isCook));
                buf.put((byte) msg.optInt(StoveConstant.stoveId));
                buf.put((byte) msg.optInt(StoveConstant.workStatus));
                buf.put((byte) 0x00); //参数个数
                break;
            case MsgKeys.SetStoveLevel_Req: //设置挡位
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.optInt(StoveConstant.isCook));
                buf.put((byte) msg.optInt(StoveConstant.stoveId));
                buf.put((byte) msg.optInt(StoveConstant.level));
                buf.putShort((short) msg.optInt(StoveConstant.recipeId));
                buf.put((byte) msg.optInt(StoveConstant.recipeStep));
                buf.put((byte) 0x00); //参数个数
                break;
            case MsgKeys.SetStoveShutdown_Req: //设置灶定时关火
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.optInt(StoveConstant.stoveId));
                buf.putShort((short) msg.optInt(StoveConstant.timingtime));
                buf.put((byte) 0x00);// 参数个数
                break;
            case MsgKeys.SetStoveLock_Req: //设置童锁
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.optInt(StoveConstant.lockStatus));
                buf.put((byte) 0x00);// 参数个数
                break;
            case MsgKeys.setStoveStep_Req: //灶自动温控步骤设置
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.optInt(StoveConstant.stoveId)); //炉头id
                if (msg.has(StoveConstant.attributeNum)) {
                    buf.put((byte) msg.optInt(StoveConstant.attributeNum)); //参数个数
                }
                if (msg.has(StoveConstant.steps)) {
                    JSONArray jsonArray = msg.optJSONArray(StoveConstant.steps);
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        buf.put((byte) jsonObject.optInt(StoveConstant.control));
                        buf.put((byte) jsonObject.optInt(StoveConstant.level));
                        buf.putShort((short) jsonObject.optInt(StoveConstant.stepTime));
                        buf.putShort((short) jsonObject.optInt(StoveConstant.stepTemp));
                    }
                }
                break;
            case MsgKeys.setStoveMode_Req: //设置灶具模式
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.optInt(StoveConstant.stoveId)); //炉头id
                if (msg.has(StoveConstant.timingtime)) {
                    buf.put((byte) 0x02); //参数个数
                    buf.put((byte) 0x01); //key
                    buf.put((byte) 1);// len
                    buf.put((byte) msg.optInt(StoveConstant.setMode));//设置模式
                    buf.put((byte) 0x02); //key
                    buf.put((byte) 2);
                    buf.putShort((short) msg.optInt(StoveConstant.timingtime)); //定时时间
                } else {
                    buf.put((byte) 0x01); //参数个数
                    buf.put((byte) 0x01); //key
                    buf.put((byte) 1);// len
                    buf.put((byte) msg.optInt(StoveConstant.setMode));//设置模式
                }
                break;
            case MsgKeys.setStoveInteraction_Req: //设置灶具智能互动
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.optInt(StoveConstant.stoveId)); //炉头id
                buf.put((byte) 0x01); //参数个数
                buf.put((byte) 0x01); //key 开启曲线创作
                buf.put((byte) 1);// len
                buf.put((byte) 0x01);//
                break;
        }
        encodeMsg(buf, msg);
    }
}
