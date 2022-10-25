package com.robam.cabinet.protocol.mqtt;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.MsgUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//消毒柜mqtt
public class MqttCabinet extends MqttPublic {

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
//        switch (msg.getID()) {
//            case MsgKeys.GetSteriStatus_Rep: //消毒柜状态响应
//                short steriStatus =
//                        ByteUtils.toShort(payload[offset++]);
//                msg.putOpt(CabinetConstant.SteriStatus, steriStatus);
//                short steriLock =
//                        ByteUtils.toShort(payload[offset++]);
//                short steriWorkLeftTimeL =
//                        ByteUtils.toShort(payload[offset++]);
//                short steriWorkLeftTimeH =
//                        ByteUtils.toShort(payload[offset++]);
//                short ateriAlarmStatus =
//                        ByteUtils.toShort(payload[offset++]);
//                break;
//        }

        switch (msg.getID()) {

            case MsgKeys.SetSteriPowerOnOff_Rep:
                msg.putOpt(CabinetConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.GetSteriStatus_Rep:
                msg.putOpt(CabinetConstant.SteriStatus, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriLock, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriWorkLeftTimeL, ByteUtils.toInt32(payload, offset++, ByteOrder.LITTLE_ENDIAN));
                offset++;
                offset++;
                offset++;
                msg.putOpt(CabinetConstant.SteriDoorLock, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriAlarmStatus, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaTem, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaHum, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaGerm, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaOzone, ByteUtils.toShort(payload[offset++]));
                short argument = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.ArgumentNumber, argument);

                while (argument > 0) {
                    short argument_key = ByteUtils.toShort(payload[offset++]);
                    switch (argument_key) {
                        case 1:
                            msg.putOpt(CabinetConstant.Key, argument_key);
                            short aShort = ByteUtils.toShort(payload[offset++]);
                            msg.putOpt(CabinetConstant.Length,aShort);
                            int anInt = ByteUtils.toInt16(payload, offset++,ByteOrder.LITTLE_ENDIAN);
                            msg.putOpt(CabinetConstant.REMAINING_APPOINT_TIME,anInt);
                            offset++;
                            break;
                        // TODO: 2019/12/13 新增安全锁定
                        case 2:
                            msg.putOpt(CabinetConstant.Key, argument_key);
                            msg.putOpt(CabinetConstant.Length,ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(CabinetConstant.SteriSecurityLock,ByteUtils.toShort(payload[offset++]));
                            break;
                        default:
                            break;

                    }
                    argument--;
                }


                break;

            case MsgKeys.GetSteriPVConfig_Rep:
                msg.putOpt(CabinetConstant.SteriSwitchDisinfect, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriInternalDisinfect, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriSwitchWeekDisinfect, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriWeekInternalDisinfect, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriPVDisinfectTime, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.ArgumentNumber, ByteUtils.toShort(payload[offset++]));
                break;

            case MsgKeys.SetSteriPVConfig_Rep:
                msg.putOpt(CabinetConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;

            // 通知类
            case MsgKeys.SteriAlarm_Noti:
                msg.putOpt(CabinetConstant.AlarmId, ByteUtils.toShort(payload[offset++]));
                break;

            case MsgKeys.SteriEvent_Noti:
                msg.putOpt(CabinetConstant.EventId, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.EventParam, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.ArgumentNumber, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.UserId, MsgUtils.getString(payload, offset++, 10));
                break;
            case MsgKeys.SetSteriLock_Rep:
                msg.putOpt(CabinetConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
        }

        //LogUtils.i("20171207", "829key:" + key);

    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
//        switch (msg.getID()) {
//            case MsgKeys.GetSteriStatus_Req: //消毒柜状态查询
//                buf.put((byte) ITerminalType.PAD);
//                break;
//        }

        String str;
        boolean bool;
        byte b;
        short s;
        switch (msg.getID()) {
            case MsgKeys.SetSteriPowerOnOff_Req:
                str = msg.optString(CabinetConstant.UserId);
                buf.put(str.getBytes());
                //
                b = (byte) msg.optInt(CabinetConstant.SteriStatus);
                buf.put(b);
                short setTime = (short) msg.optInt(CabinetConstant.SteriTime);
                s = (short) (setTime & 0xFF);
                buf.putShort(s);
                   /* s = (short) ((setTime >> 8) & 0xFF);
                    LogUtils.i("20171206","time1:"+s);
                    buf.putShort(s);*/
                b = (byte) msg.optInt(CabinetConstant.ArgumentNumber);
                buf.put(b);
                //LogUtils.i("20171206", "Argument:" + msg.optInt(CabinetConstant.ArgumentNumber));

                if (msg.optInt(CabinetConstant.ArgumentNumber) > 0) {
                    if (msg.optInt(CabinetConstant.Key) == 1) {
                        b = (byte) msg.optInt(CabinetConstant.Key);
                        buf.put(b);
                        b = (byte) msg.optInt(CabinetConstant.Length);
                        buf.put(b);
                        b = (byte) msg.optInt(CabinetConstant.warmDishTempValue);
                        buf.put(b);
                    }

                    if (msg.optInt(CabinetConstant.Key) == 2) {
                        b = (byte) msg.optInt(CabinetConstant.Key);
                        buf.put(b);
                        b = (byte) msg.optInt(CabinetConstant.Length);
                        buf.put(b);

                        int SteriReserveTime = msg.optInt(CabinetConstant.SteriReserveTime);
                        b = (byte) (SteriReserveTime & 0xFF);
                        buf.put(b);
                        b = (byte) ((SteriReserveTime >> 8) & 0xFF);
                        buf.put(b);



                    }
                }
                break;

            case MsgKeys.GetSteriStatus_Req:
                str = msg.optString(CabinetConstant.UserId);
                buf.put(str.getBytes());
                break;


            case MsgKeys.GetSteriPVConfig_Req:
                str = msg.optString(CabinetConstant.UserId);
                buf.put(str.getBytes());
                break;


            case MsgKeys.SetSteriPVConfig_Req:
                str = msg.optString(CabinetConstant.UserId);
                buf.put(str.getBytes());

                bool = msg.optBoolean(CabinetConstant.SteriSwitchDisinfect);
                buf.put(bool ? (byte) 1 : (byte) 0);

                b = (byte) msg.optInt(CabinetConstant.SteriInternalDisinfect);
                buf.put(b);

                bool = msg.optBoolean(CabinetConstant.SteriSwitchWeekDisinfect);
                buf.put(bool ? (byte) 1 : (byte) 0);

                b = (byte) msg.optInt(CabinetConstant.SteriWeekInternalDisinfect);
                buf.put(b);
                b = (byte) msg.optInt(CabinetConstant.SteriPVDisinfectTime);
                buf.put(b);

                b = (byte) msg.optInt(CabinetConstant.ArgumentNumber);
                buf.put(b);
                break;
            case MsgKeys.SetSteriLock_Req:
                    /*str = msg.optString(CabinetConstant.UserId);
                    buf.put(str.getBytes());*/
                b = (byte) msg.optInt(CabinetConstant.SteriLock);
                buf.put(b);
                break;

        }
    }

}
