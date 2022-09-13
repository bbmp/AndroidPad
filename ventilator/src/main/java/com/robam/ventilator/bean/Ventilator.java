package com.robam.ventilator.bean;

import android.os.PowerManager;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.device.VentilatorFactory;

public class Ventilator extends Device {
    public Ventilator(Device device) {
        this.ownerId = device.ownerId;
        this.mac = device.mac;
        this.guid = device.guid;
        this.bid = device.bid;
        this.dc = device.dc;
        this.dt = device.dt;
        this.displayType = device.displayType;
        this.categoryName = device.categoryName;
        this.deviceTypeIconUrl = device.deviceTypeIconUrl;
        this.subDevices = device.subDevices;
    }

    public Ventilator(String name, String displayType) {
        super(name, displayType);
    }

    /**
     * byte6 开机/关机
     */
    public byte startup = 0x00 ;
    public int startupIndex = 0 ;

    /**
     * byte7 开灯/关灯
     */
    public byte lightOn = (byte) 0xA0;

    /**
     * byte8 风机挡位
     */
    public byte gears = (byte) 0xA0;
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

    @Override
    public void onReceivedMsg(int msgId, String guid, byte[] payload, int offset) {
        if (!this.guid.equals(guid))
            return;

        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.setDeviceAttribute_Req:
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                break;
            case MsgKeys.GetFanStatus_Rep: //烟机查询返回
                status = Device.ONLINE;
                short fanStatus =
                        ByteUtils.toShort(payload[offset++]);
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
    }
}
