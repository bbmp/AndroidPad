package com.robam.ventilator.protocol.mqtt;

import com.robam.common.IDeviceType;
import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.steamoven.bean.SteamOven;
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
                if (/*!MMKVUtils.getFanPan() ||*/ HomeVentilator.getInstance().isLock()) //烟锅联动未开 或锁屏状态
                    return;
                if (HomeVentilator.getInstance().gear == (byte) 0xA0) { //不工作
                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机

                        HomeVentilator.getInstance().openVentilatorGear(VentilatorConstant.FAN_GEAR_FRY);
                    } else
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_FRY);
                }
                break;
            case BleDecoder.EVENT_POT_TEMPERATURE_OV: //防干烧预警 锅温280以上且烟锅联动开启
                if (/*!MMKVUtils.getFanPan() || */HomeVentilator.getInstance().isLock()) //烟锅联动未开 或锁屏状态
                    return;
                //关闭灶具
                StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, IPublicStoveApi.STOVE_LEFT, 0x00, StoveConstant.STOVE_CLOSE);
                StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, IPublicStoveApi.STOVE_RIGHT, 0x00, StoveConstant.STOVE_CLOSE);
                break;
            case BleDecoder.EVENT_POT_LINK_2_RH://烟锅联动锅温50以上，烟机未开且烟锅联动开启
                if (/*!MMKVUtils.getFanPan() ||*/ HomeVentilator.getInstance().isLock()) //烟锅联动未开 或锁屏状态
                    return;
                //烟机开2挡
                if (HomeVentilator.getInstance().gear == (byte) 0xA0) { //不工作
                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机

                        HomeVentilator.getInstance().openVentilatorGear(VentilatorConstant.FAN_GEAR_MID);
                    } else
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                }
                break;
            case BleDecoder.CMD_RH_SET_INT: //内部远程烟机交互
                short type = ByteUtils.toShort(payload[offset++]); //蓝牙品类
                //属性个数
                short attributeNum = ByteUtils.toShort(payload[offset++]);
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case 1:
                            int gear = MsgUtils.getByte(payload[offset++]); //请求联动挡位
                            msg.putOpt(VentilatorConstant.FanGear, gear);
                            for (Device device: AccountInfo.getInstance().deviceList) {
                                if (msg.getGuid().equals(device.guid) && device instanceof Pan) {
                                    if (/*MMKVUtils.getFanPan() && MMKVUtils.getFanPanGear() && */HomeVentilator.getInstance().isLock()) {//烟锅联动打开和自动匹配风量打开 非锁屏
                                        if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机
                                            HomeVentilator.getInstance().openVentilatorGear(gear);
                                        } else {
                                            //请求联动，只升不降
                                            int curGear = VentilatorConstant.FAN_GEAR_CLOSE; //当前挡位
                                            if (HomeVentilator.getInstance().gear == (byte) 0xA1)
                                                curGear = VentilatorConstant.FAN_GEAR_WEAK;
                                            else if (HomeVentilator.getInstance().gear == (byte) 0xA3)
                                                curGear = VentilatorConstant.FAN_GEAR_MID;
                                            else if (HomeVentilator.getInstance().gear == (byte) 0xA6)
                                                curGear = VentilatorConstant.FAN_GEAR_FRY;
                                            if (gear > curGear)
                                                VentilatorAbstractControl.getInstance().setFanGear(gear);
                                        }
                                    }
                                } else if (msg.getGuid().equals(device.guid) && device instanceof Stove && IDeviceType.RRQZ.equals(device.dc)) {
                                    if (MMKVUtils.getFanStove() && MMKVUtils.getFanStoveGear() && HomeVentilator.getInstance().isLock()) { //烟灶联动打开 非锁屏
                                        if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机
                                            HomeVentilator.getInstance().openVentilatorGear(gear);
                                        } else {
                                            //请求联动，只升不降
                                            int curGear = VentilatorConstant.FAN_GEAR_CLOSE; //当前挡位
                                            if (HomeVentilator.getInstance().gear == (byte) 0xA1)
                                                curGear = VentilatorConstant.FAN_GEAR_WEAK;
                                            else if (HomeVentilator.getInstance().gear == (byte) 0xA3)
                                                curGear = VentilatorConstant.FAN_GEAR_MID;
                                            else if (HomeVentilator.getInstance().gear == (byte) 0xA6)
                                                curGear = VentilatorConstant.FAN_GEAR_FRY;
                                            if (gear > curGear)
                                                VentilatorAbstractControl.getInstance().setFanGear(gear);
                                        }
                                    }
                                }
                            }
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
                            if (min >=1 && min <= 5) //设置延时关机时间
                                MMKVUtils.setDelayShutdownTime(min + "");
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
            case MsgKeys.GetSmartConfig_Rep: { //读取智能互动参数回复
                buf.put((byte) (MMKVUtils.getFanStove() ? 0x01: 0x00)); //烟灶联动开关
                buf.put((byte) (MMKVUtils.getFanStoveGear() ? 0x01:0x00)); //挡位开关
                buf.put((byte) (MMKVUtils.getDelayShutdown() ? 0x01:0x00));//延时关机开关
                buf.put((byte) Integer.parseInt(MMKVUtils.getDelayShutdownTime())); //时间
                buf.put((byte) (MMKVUtils.getOilClean() ? 0x01:0x00));//油网清洗开关
                buf.put((byte) (MMKVUtils.getHoliday() ? 0x01:0x00));//假日模式开关
                buf.put((byte) Integer.parseInt(MMKVUtils.getHolidayDay())); //间隔天数
                buf.put((byte) (MMKVUtils.getHoliday() ? 0x01:0x00));//每周通风
                String setTime = MMKVUtils.getHolidayWeekTime();
                buf.put((byte) DateUtil.getWeek(setTime)); //通风时间点
                buf.put((byte) Integer.parseInt(setTime.substring(2, 4)));
                buf.put((byte) Integer.parseInt(setTime.substring(5, 7)));
                buf.put((byte) 0x02);//参数个数
                buf.put((byte) 0x03); //key
                buf.put((byte) 0x01);
                buf.put(HomeVentilator.getInstance().param7);//智感恒吸
                buf.put((byte) 0x07); //key
                buf.put((byte) 0x01);
                buf.put((byte) (MMKVUtils.getFanStoveGear() ? 0x01:0x00));//灶具最小火力
            }
            break;
            case MsgKeys.GetFanStatus_Rep: //属性查询响应
                buf.put(HomeVentilator.getInstance().status);//状态
                if (HomeVentilator.getInstance().gear == (byte) 0xA6)
                    buf.put((byte) 0x06);
                else if (HomeVentilator.getInstance().gear == (byte) 0xA3)
                    buf.put((byte) 0x03);
                else if (HomeVentilator.getInstance().gear == (byte) 0xA1)
                    buf.put((byte) 0x01);
                else
                    buf.put((byte) 0x00); //挡位

                byte byteLight = (byte) (HomeVentilator.getInstance().lightOn == (byte) 0xA0 ? 0x00:0x01);
                buf.put(byteLight); //灯开关

                long runTime = MMKVUtils.getFanRuntime();
                if (runTime >= 60 * 60 * 60 * 1000)
                    buf.put((byte) 1); //是否需要清洗
                else
                    buf.put((byte) 0);
                //定时时间
                buf.put((byte) 0);

                buf.put((byte) (AccountInfo.getInstance().getConnect().getValue()?1:0));//联网状态
                buf.put((byte) 2);//参数个数
                buf.put((byte) 15); //key
                buf.put((byte) 0x01);
                buf.put(HomeVentilator.getInstance().param7);//智感恒吸
                buf.put((byte) 8);
                buf.put((byte) 2);
                buf.putShort((short) HomeVentilator.getInstance().remainTime);

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
            case MsgKeys.SetFanStatusCompose_Req: //设置烟机状态组合回复
                if (null != msg.optJSONArray(VentilatorConstant.KEYS)) {
                    JSONArray jsonArray = msg.optJSONArray(VentilatorConstant.KEYS);
                    buf.put((byte) jsonArray.length());//参数个数
                    for (int i = 0; i<jsonArray.length(); i++) {
                        buf.put((byte)jsonArray.optInt(i)); //key
                        buf.put((byte) 0x00);
                    }
                }
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
                case MsgKeys.DeviceConnected_Noti: { //子设备更新
                    short deviceNum = ByteUtils.toShort(payload[offset++]); //子设备数量
                    //userid
                    ByteUtils.toString(payload, offset, 10);
                    offset += 10;
                    //mac
                    ByteUtils.toString(payload, offset, 12);
                    offset += 12;
                    for (int i = 0; i<deviceNum; i++) {
                        //guid
                        String guid = ByteUtils.toString(payload, offset, 17); //子设备
                        AccountInfo.getInstance().getGuid().setValue(guid);
                        offset += 17;
                        //bizlength
                        short bizLength = ByteUtils.toShort(payload[offset++]);
                        //biz
                        ByteUtils.toString(payload, offset, bizLength);
                        offset += bizLength;
                        //固件版本
                        ByteUtils.toShort(payload[offset++]);
                        //设备架构类型
                        ByteUtils.toShort(payload[offset++]);
                        //是否在线
                        ByteUtils.toShort(payload[offset++]);
                    }
                }
                break;
                case MsgKeys.GetSmartConfig_Req: { //读取智能互动模式
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.GetSmartConfig_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                break;
                case MsgKeys.GetFanStatus_Req: { //查询烟机
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
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
                            HomeVentilator.getInstance().cancleDelayShutDown();
                            if (HomeVentilator.getInstance().startup == 0x01)
                                HomeVentilator.getInstance().closeVentilator();
                            break;
                        case 1: //开机
                            if (HomeVentilator.getInstance().startup == 0x00)
                                HomeVentilator.getInstance().openVentilator();
                            break;
                        case 4: //清洗锁定
                            if (HomeVentilator.getInstance().startup == 0x01) {
                                HomeVentilator.getInstance().screenLock();
                                //打开油网清洗
                                VentilatorAbstractControl.getInstance().openOilClean();
                                //开灯
                                Plat.getPlatform().openWaterLamp();
                                //重新计算
                                MMKVUtils.setFanRuntime(0);
                            }
                            break;
                    }
//                    short attributeNum = ByteUtils.toShort(payload[offset++]);
//                    while (attributeNum > 0) {
//                        attributeNum--;
//                    }
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

                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机

                        HomeVentilator.getInstance().openVentilatorGear(gear);
                    } else
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
                    ByteUtils.toString(payload, offset, 10);
                    offset += 10;
                    //灯开关
                    short light = ByteUtils.toShort(payload[offset++]);

                    if (light == VentilatorConstant.FAN_LIGHT_OPEN) {
                        VentilatorAbstractControl.getInstance().setFanLight(light);
                        Plat.getPlatform().openWaterLamp();
                    } else {
                        VentilatorAbstractControl.getInstance().setFanLight(light);
                        Plat.getPlatform().closeWaterLamp();
                    }
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
                    ByteUtils.toString(payload, offset, 10);
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
                    //重置烟机清洗响应
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.RestFanCleanTime_Rep)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                    //重新计算
                    MMKVUtils.setFanRuntime(0);
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
//                    short attributeNum = ByteUtils.toShort(payload[offset++]);

                    if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机

                        HomeVentilator.getInstance().openVentilatorGear(VentilatorConstant.FAN_GEAR_WEAK);
                    } else
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_WEAK);
                    //定时时间
                    MMKVUtils.setTimingTime(time);
                    //延时关机倒计时
                    HomeVentilator.getInstance().timeShutdown(time);
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
                case MsgKeys.SetSmartConfig_Req: { //设置智能互动

                }
                    break;
                case MsgKeys.SetFanStatusCompose_Rep: { //设置烟机状态组合
                    //控制端类型
                    short terminalType = ByteUtils.toShort(payload[offset++]);
                    //属性个数
                    short attributeNum = ByteUtils.toShort(payload[offset++]);
                    //响应
                    MqttMsg newMsg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetFanStatusCompose_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign())
                            .setDt(Plat.getPlatform().getDt())
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
                            .build();
                    JSONArray jsonArray = new JSONArray();
                    while (attributeNum > 0) {
                        attributeNum--;
                        int key = MsgUtils.getByte(payload[offset++]);
                        int length = MsgUtils.getByte(payload[offset++]);
                        switch (key) {
                            case 1: {//烟灶联动开关
                                boolean onOff = (MsgUtils.getByte(payload[offset++]) == 1 ? true : false); //开关
                                MMKVUtils.setFanStove(onOff);
                                if (!onOff) { //烟灶联动关闭
                                    HomeVentilator.getInstance().stopLevelCountDown();
                                    HomeVentilator.getInstance().stopA6CountDown();
                                }
                                jsonArray.put(key);
                            }
                                break;
                            case 2: {  //挡位联动开关
                                boolean onOff = (MsgUtils.getByte(payload[offset++]) == 1 ? true : false); //开关
                                MMKVUtils.setFanStoveGear(onOff);
                                jsonArray.put(key);
                            }
                            break;
                            case 3: { //延时关机开关
                                boolean onOff = (MsgUtils.getByte(payload[offset++]) == 1 ? true : false); //开关
                                MMKVUtils.setDelayShutdown(onOff);
                                jsonArray.put(key);
                            }
                            break;
                            case 4: {  //延时关机时间
                                int minute = MsgUtils.getByte(payload[offset++]);
                                if (minute >= 1 && minute <= 5)
                                    MMKVUtils.setDelayShutdownTime(minute + "");
                                jsonArray.put(key);
                            }
                            break;
                            case 5: { //油网提醒开关
                                boolean onOff = (MsgUtils.getByte(payload[offset++]) == 1 ? true : false); //开关
                                MMKVUtils.setOilClean(onOff);
                                jsonArray.put(key);
                            }
                            break;
                            case 6: {
                                int isOpen = MsgUtils.getByte(payload[offset++]); //定时通风开关
                                int holiday = MsgUtils.getByte(payload[offset++]); //定时天数
                                MMKVUtils.setHoliday(isOpen == 1?true:false);
                                MMKVUtils.setHolidayDay(holiday+"");
                                jsonArray.put(key);
                            }
                            break;
                            case 7: {
                                int isOpen = MsgUtils.getByte(payload[offset++]); //定时通风开关
                                int week = MsgUtils.getByte(payload[offset++]); //week
                                int hour = MsgUtils.getByte(payload[offset++]); //hour
                                int minute = MsgUtils.getByte(payload[offset++]); //minute
                                MMKVUtils.setHoliday(isOpen == 1?true:false);
                                MMKVUtils.setHolidayWeekTime(DateUtil.getWeekTime(week, hour, minute));
                                jsonArray.put(key);
                            }
                            break;
                            case 17: { //智感恒吸
                                int smart = MsgUtils.getByte(payload[offset++]);
                                if (HomeVentilator.getInstance().isStartUp()) {//开机状态
                                    VentilatorAbstractControl.getInstance().setSmart(smart);
                                    jsonArray.put(key);
                                }
                            }
                                break;
                            case 18: {//烟灶挡位联动开关
                                boolean onOff = (MsgUtils.getByte(payload[offset++]) == 1 ? true : false); //开关
                                MMKVUtils.setFanStoveGear(onOff);
                                jsonArray.put(key);
                            }
                            break;
                        }
                    }
                    newMsg.putOpt(VentilatorConstant.KEYS, jsonArray);
                    MqttManager.getInstance().publish(newMsg, VentilatorFactory.getProtocol());
                }
                    break;
                case MsgKeys.setFanInteraction_Req: {//外部命令请求烟机互动 烟蒸烤联动
                    //属性个数
                    short attributeNum = ByteUtils.toShort(payload[offset++]);
                    while (attributeNum > 0) {
                        attributeNum--;
                        int key = MsgUtils.getByte(payload[offset++]);
                        int length = MsgUtils.getByte(payload[offset++]);
                        switch (key) {
                            case 1:
                                int gear = MsgUtils.getByte(payload[offset++]); //请求联动挡位
                                for (Device device: AccountInfo.getInstance().deviceList) {
                                    if (msg.getGuid().equals(device.guid) && device instanceof SteamOven) {
                                        if (MMKVUtils.getFanSteam() && MMKVUtils.getFanSteamGear() && HomeVentilator.getInstance().isLock()) {//烟蒸烤联动打开和自动匹配风量打开 非锁屏
                                            if (HomeVentilator.getInstance().startup == (byte) 0x00) { //先开机

                                                HomeVentilator.getInstance().openVentilatorGear(gear);
                                            } else {
                                                //请求联动，只升不降
                                                int curGear = VentilatorConstant.FAN_GEAR_CLOSE; //当前挡位
                                                if (HomeVentilator.getInstance().gear == (byte) 0xA1)
                                                    curGear = VentilatorConstant.FAN_GEAR_WEAK;
                                                else if (HomeVentilator.getInstance().gear == (byte) 0xA3)
                                                    curGear = VentilatorConstant.FAN_GEAR_MID;
                                                else if (HomeVentilator.getInstance().gear == (byte) 0xA6)
                                                    curGear = VentilatorConstant.FAN_GEAR_FRY;
                                                if (gear > curGear)
                                                    VentilatorAbstractControl.getInstance().setFanGear(gear);
                                            }
                                        }
                                    }
                                }
                                    break;
                            case 2:
                                int min = 0;
                                min = MsgUtils.getByte(payload[offset++]); //请求定时关机时间

                                if (min >=1 && min <= 5) //设置延时关机时间
                                    MMKVUtils.setDelayShutdownTime(min + "");
                                break;
                        }
                    }
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
