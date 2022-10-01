package com.robam.ventilator.device;

import com.robam.common.bean.RTopic;
import com.robam.common.constant.ComnConstant;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.ventilator.constant.VentilatorConstant;

public class HomeVentilator {
    //当前进入的烟机
    public static HomeVentilator getInstance() {
        return HomeVentilator.VentilatorHolder.instance;
    }
    private static class VentilatorHolder {
        private static final HomeVentilator instance = new HomeVentilator();
    }
    //工作状态
    public byte status = 0;

    /**
     * byte6 开机/关机
     */
    public byte startup = 0x00 ;

    /**
     * byte7 开灯/关灯
     */
    public byte lightOn = (byte) 0xA0;

    /**
     * byte8 风机挡位
     */
    public byte gear = (byte) 0xA0;
    /**
     * byte9 蜂鸣器
     */
    public byte beep = 0x00;
    /**
     * byte10 风门挡板
     */
    public byte baffle = (byte) 0xA0;
    /**
     * byte11 参数1 风门电流反馈值
     */
    public byte param1 = 0x00;
    /**
     * byte12 参数2 风门状态反馈值
     */
    public byte param2 = 0x00;
    /**
     * byte13 参数3 风机功率/2反馈值
     */
    public byte param3 = 0x00;
    /**
     * byte14 参数4（风机转速/10反馈值
     */
    public byte param4 = 0x00;
    /**
     * byte15 参数5预留（可以用来传递温度值
     */
    public byte param5 = 0x00;
    /**
     * byte16 风门挡板2
     */
    public byte baffle2 = (byte) 0x00;
    /**
     * byte17 参数6 设置可调色温灯颜色	0）-100，0表示全冷100全
     */
    public byte param6 = 0;
    /**
     * byte18  参数7 设置智感恒吸模式
     */
    public byte param7 = (byte) 0x00;
    /**
     * byte19  参数8 智感恒吸阻力值
     */
    public byte param8 = (byte) 0x00;
    /**
     * byte20  参数9 预留
     */
    public byte param9 = (byte) 0x00;

    //通知上线
    public void notifyOnline(String guid, String biz, int status) { //子设备guid

        //通知
        try {
            String srcGuid = Plat.getPlatform().getDeviceOnlySign(); //烟机guid
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.DeviceConnected_Noti)
                    .setGuid(srcGuid) //源guid
                    .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, DeviceUtils.getDeviceTypeId(srcGuid), DeviceUtils.getDeviceNumber(srcGuid)))
                    .build();
            msg.putOpt(ComnConstant.DEVICE_NUM, 2);
            msg.putOpt(VentilatorConstant.STOVE_GUID, guid);
            msg.putOpt(VentilatorConstant.STOVE_BIZ, biz);
            msg.putOpt(VentilatorConstant.STOVE_STATUS, status);
            MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
