package com.robam.stove.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//灶具mqtt实现
public class MqttStove extends MqttPublic {


    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case MsgKeys.GetStoveStatus_Req: //查询灶状态
                //答复
//                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
//                MqttMsg newMsg = new MqttMsg.Builder()
//                        .setMsgId(MsgKeys.GetStoveStatus_Rep)
//                        .setGuid(curGuid)
//                        .setDt(Plat.getPlatform().getDt())
//                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
//                        .build();


                break;
            case MsgKeys.GetStoveStatus_Rep: {//查询灶状态返回
                int stoveNum = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.stoveNum, stoveNum);
                int lockStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.lockStatus, lockStatus);
                int workStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.leftStatus, workStatus);
                int level = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.leftLevel, level);
                int time = MsgUtils.bytes2ShortLittle(payload, offset);
                msg.putOpt(StoveConstant.leftTime, time);
                offset += 2;
                int alarmStatus = MsgUtils.getByte(payload[offset++]);//报警状态
                msg.putOpt(StoveConstant.leftAlarm, alarmStatus);
                //右灶
                workStatus = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.rightStatus, workStatus);
                level = MsgUtils.getByte(payload[offset++]);
                msg.putOpt(StoveConstant.rightLevel, level);
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
                    }
                }
            }
                break;
            case MsgKeys.SetStoveStatus_Req: //设置灶状态
                short terminalType = ByteUtils.toShort(payload[offset++]); //控制端类型
                String user = MsgUtils.getString(payload, offset, 10); //user
                offset += 10;
                short isCook = ByteUtils.toShort(payload[offset++]); //是否菜谱做菜
                msg.putOpt(StoveConstant.isCook, isCook);
                short id = ByteUtils.toShort(payload[offset++]); //炉头id
                msg.putOpt(StoveConstant.stoveId, id);
                short workStatus = ByteUtils.toShort(payload[offset++]);//工作状态
                msg.putOpt(StoveConstant.workStatus, workStatus);

                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.GetStoveStatus_Req: //本机查询灶状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.GetStoveStatus_Rep:
                buf.put((byte) 0x02); //炉头个数
                buf.put((byte) 0x00);//童锁
                buf.put((byte) 0x00);// 工作状态
                buf.put((byte) 0x00);// 功率等级
                buf.putShort((short) 0); //定时剩余秒数
                buf.put((byte) 0x00);// 报警状态
                buf.put((byte) 0x00);// 功率等级
                buf.put((byte) 0x00); //参数个数

                break;
            case MsgKeys.SetStoveStatus_Req: //设置灶具状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.opt(StoveConstant.isCook));
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.put((byte) msg.opt(StoveConstant.workStatus));
                break;
            case MsgKeys.SetStoveLevel_Req: //设置挡位
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.opt(StoveConstant.isCook));
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.put((byte) msg.opt(StoveConstant.level));
                break;
            case MsgKeys.SetStoveShutdown_Req: //设置灶定时关火
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.putShort((short) msg.opt(StoveConstant.timingtime));
                buf.put((byte) 0x00);// 参数个数
                break;
            case MsgKeys.SetStoveLock_Req: //设置童锁
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.opt(StoveConstant.lockStatus));
                buf.put((byte) 0x00);// 参数个数
                break;
            case MsgKeys.setStoveStep_Req: //灶自动温控步骤设置
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.opt(StoveConstant.stoveId)); //炉头id
                if (msg.has(StoveConstant.attributeNum)) {
                    buf.put((byte) msg.opt(StoveConstant.attributeNum)); //参数个数
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
        }
    }
}
