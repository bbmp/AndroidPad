package com.robam.ventilator.device;

import com.robam.common.mqtt.MqttMsg;

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

}
