package com.robam.steamoven.bean;

import android.serialport.helper.SerialPortHelper;

import androidx.lifecycle.LiveData;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.steamoven.protocol.serial.ProtoParse;

import java.util.ArrayList;
import java.util.List;

public class SteamOven extends Device {
    public SteamOven(Device device) {
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

    public SteamOven(String name, String displayType) {
        super(name, displayType);
    }

    /**
     * 电源板状态
     */
    public short sysState;
    /**
     * 电源状态
     */
    public short powerState;
    /**
     * 电源控制
     */
    public short powerCtrl;
    /**
     * 工作状态
     */
    public int workState;
    public short workStateDown;

    /**
     * 工作类型 普通模式 多段 菜谱 蒸模式 烤模式
     */
    public short workType;
    /**
     * 工作模式
     */
    public short workMode;
    /**
     * 工作控制
     */
    public short workCtrl;
    /**
     * 设置预约时间
     */
    public int orderTime ;
    /**
     * 剩余预约时间
     */
    public int orderLeftTime;

    /**
     * 故障码
     */
    public short faultCode;
    /**
     * 旋转烤开关
     */
    public short rotateSwitch;
    /**
     * 上照明标志
     */
    public int upLampState;
    /**
     * 下照明标志 0 关 1  开
     */
    public int downLampState;
    /**
     * 水箱状态 0 开 1 关
     */
    public int waterBoxState;
    /**
     * 废水箱状态 0 开 1 关
     */
    public int fWaterBoxState;
    /**
     * 水位状态 0 正常 1 缺水
     */
    public int waterLevelState;
    /**
     * 门控状态
     */
    public int doorState;
    /**
     *  升降电机状态 代表电机开启
     */
    public int uppdem;
    /**
     * 降电机状态标志位 代表电机关
     */
    public int downpdem;

    /**
     * 旋转烤盘状态
     */
    public int rotatePan;
    /**
     * 微波门控状态（锁）
     */
    public int doorStateRippleLock;

//    /**
//     * 门开关状态
//     */
//    public int doorState;
    /**
     * 手动加蒸汽工作状态
     */
    public boolean steamState = false;
    /**
     * 菜谱编号
     */
    public int recipeId;
    /**
     * 菜谱设置总时间
     */
    public int recipeSetMinutes;
    /**
     * 设置温度 上温度
     */
    public int setTempUp;
    /**
     * 设置温度 下温度
     */
    public int setTempDown;
    /**
     * 当前温度 上温度
     */
    public int upTemp;
    /**
     * 当前温度 下温度
     */
    public short downTemp;
    /**
     * 除垢参数
     */
    public short descaleNum;

    public int descaleNumVariation;
    /**
     * 剩余总时间
     */
    public int totalRemainSeconds;

    /**
     * 当前蒸模式累计工作时间
     */
    public short curSteamTotalHours;
    /**
     * 蒸模式累计需除垢时间
     */
    public short curSteamTotalNeedHours;
    /**
     * 实际运行时间
     */
    public short cookedTime;
    /**
     * 除垢状态
     */
    public short chugouType;


    /**
     * 当前段数/段序
     */
    public int curSectionNbr;

    /**
     * 首段模式
     */
    public int mode;
    /**
     * 首段设置的上温度
     */
    public int setUpTemp;
    /**
     * 首段设置的下温度
     */
    public int setDownTemp;
    /**
     * 首段设置的时间
     */
    public int setTime;
    /**
     * 首段剩余的时间
     */
    public int restTime;
    /**
     * 首段蒸汽量
     */
    public int steam;
    /**
     * 第二段模式
     */
    public int mode2;
    /**
     * 2段设置的上温度
     */
    public int setUpTemp2;
    /**
     * 2段设置的下温度
     */
    public int setDownTemp2;
    /**
     * 2段设置的时间
     */
    public int setTime2;
    /**
     * 2段剩余的时间
     */
    public int restTime2;
    /**
     * 2段蒸汽量
     */
    public int steam2;

    public List<SettingMultiModeBean> multiMode  = new ArrayList<>();
    /**
     * 3段模式
     */
    public int mode3;
    /**
     * 3段设置的上温度
     */
    public int setUpTemp3;
    /**
     * 3段设置的下温度
     */
    public int setDownTemp3;
    /**
     * 3段设置的时间
     */
    public int setTime3;
    /**
     * 3段剩余的时间
     */
    public int restTime3;
    /**
     * 3段蒸汽量
     */
    public int steam3;

    /**
     * 预约开始时间
     */
    public String orderDate;


    /**
     * 蜂鸣器
     */
    public short beep_type;

    /**
     * 上温度故障
     */
    public boolean fault_temp_up = false;
    /**
     * 下温度故障
     */
    public boolean fault_temp_down = false;
    /**
     * 显示板通信故障
     */
    public boolean fault_disp_comm = false;
    /**
     * 上风机故障
     */
    public boolean fault_fan_up = false;
    /**
     * 加热故障
     */
    public boolean fault_heat = false;
    /**
     * 水位检测故障
     */
    public boolean fault_water_level = false;
    /**
     * 加热风机故障
     */
    public boolean fault_heater_fan = false;
    /**
     * 蒸发盘干烧 底部温度故障
     */
    public boolean fault_steam_temp = false;
    /**
     * 升降电机堵转
     */
    public boolean fault_up_and_down_motor = false;

//    /**
//     * 升降电机
//     */
//    public int pdem;
//
//    /**
//     * 旋转烤盘
//     */
//    public int rotatePan;
    /**
     * 当前工作曲线对应点温度时间
     */
    public ArrayList<CurveBean> curveBeans  ;
    /**
     * 每次开始工作生成唯一workGuid
     */
    public String workGuid ;

    private SteamOven() {

    }


    /**
     * 获取多段总段数
     * @return
     */
    public int getSectionNumber(){

        return multiMode.size() ;

    }

    /**
     * 获取多段展示模式 、获取三段
     *
     * @return
     */
    public List<SettingMultiModeBean> getMultiMode() {

        List<SettingMultiModeBean> settingMultiModeBeans = new ArrayList<>();
        settingMultiModeBeans.addAll(multiMode);
        while (settingMultiModeBeans.size() < 3){
            settingMultiModeBeans.add(new SettingMultiModeBean());
        }
        return settingMultiModeBeans;
    }

    /**
     * 获取多段展示模式
     *
     * @return
     */
    public List<SettingMultiModeBean> getMultiModeData() {
        return multiMode;
    }

    /**
     * 添加多段 单段
     *
     * @param settingMultiModeBean
     */
    public void addMultiMode(SettingMultiModeBean settingMultiModeBean) {
        multiMode.add(settingMultiModeBean);
        if (multiMode.size() == 1){
            this.mode = settingMultiModeBean.mode;
            this.setUpTemp = settingMultiModeBean.setTemp;
            this.setDownTemp = settingMultiModeBean.setDownTemp;
            this.setTime = settingMultiModeBean.setTime;
            this.steam = settingMultiModeBean.steam;

        }else if (multiMode.size() == 2){
            this.mode2 = settingMultiModeBean.mode;
            this.setUpTemp2 = settingMultiModeBean.setTemp;
            this.setDownTemp2 = settingMultiModeBean.setDownTemp;
            this.setTime2 = settingMultiModeBean.setTime;
            this.steam2 = settingMultiModeBean.steam;
        }else if (multiMode.size() == 3){
            this.mode3 = settingMultiModeBean.mode;
            this.setUpTemp3 = settingMultiModeBean.setTemp;
            this.setDownTemp3 = settingMultiModeBean.setDownTemp;
            this.setTime3 = settingMultiModeBean.setTime;
            this.steam3 = settingMultiModeBean.steam;
        }
    }

    /**
     * 移除多段
     *
     * @param position
     */
    public void removeMultiMode(int position) {
        multiMode.remove(position);
        this.mode = 0 ;
        this.setUpTemp = 0 ;
        this.setDownTemp = 0 ;
        this.setTime = 0 ;
        this.steam = 0 ;

        this.mode2 = 0 ;
        this.setUpTemp2 = 0 ;
        this.setDownTemp2= 0 ;
        this.setTime2 = 0 ;
        this.steam2 = 0 ;

        this.mode3 = 0 ;
        this.setUpTemp3 = 0 ;
        this.setDownTemp3 = 0 ;
        this.setTime3 = 0 ;
        this.steam3 = 0 ;
    }

    /**
     * 移除多段
     */
    public void removeAllMultiMode() {
        multiMode.clear();
    }
    /**
     * 获取总时间
     *
     * @return
     */
    public int getTotalTime() {
        int total = 0 ;
        for (SettingMultiModeBean settingMultiModeBean : multiMode) {
            total += settingMultiModeBean.setTime ;
        }
        return total;
    }

    /**
     * 移除预约状态
     */
    public void removeOrderStata(){
        orderLeftTime = 0 ;
        orderTime = 0 ;
    }



    @Override
    public void unmarshaller(int msgId, String guid, byte[] payload, int offset) {
        if (!this.guid.equals(guid)) //非当前设备
            return;
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:
                break;
            case MsgKeys.getDeviceAttribute_Rep: {
            }
                break;
            case MsgKeys.getSteameOvenStatus_Rep:
                //
                short status = ByteUtils.toShort(payload[offset++]);
                short powerOnStatus = ByteUtils.toShort(payload[offset++]);
                short workOnStatus = ByteUtils.toShort(payload[offset++]);
                short alarm = ByteUtils.toShort(payload[offset++]);
                short mode = ByteUtils.toShort(payload[offset++]);
                short temp = ByteUtils.toShort(payload[offset++]);
                short leftTime = ByteUtils.toShort(payload[offset++]);
                offset++;
                short light = ByteUtils.toShort(payload[offset++]);
                short waterStatus = ByteUtils.toShort(payload[offset++]);
                short setTemp = ByteUtils.toShort(payload[offset++]);
                short setTime = ByteUtils.toShort(payload[offset++]);
                short min = ByteUtils.toShort(payload[offset++]);
                short hour = ByteUtils.toShort(payload[offset++]);
                short recipeId = ByteUtils.toShort(payload[offset++]);
                offset++;
                short recipeSteps = ByteUtils.toShort(payload[offset++]);
                short setDownTemp = ByteUtils.toShort(payload[offset++]);
                short downTemp = ByteUtils.toShort(payload[offset++]);
                short steam = ByteUtils.toShort(payload[offset++]);
                short segments_Key = ByteUtils.toShort(payload[offset++]);
                short step_Key = ByteUtils.toShort(payload[offset++]);
                short preFalg = ByteUtils.toShort(payload[offset++]);
                short modelType = ByteUtils.toShort(payload[offset++]);

                short argument = ByteUtils.toShort(payload[offset++]);

                while (argument > 0) {
                    short argumentKey = ByteUtils.toShort(payload[offset++]);
                    switch (argumentKey) {
                        case 3:

                            short cpStepLength = ByteUtils.toShort(payload[offset++]);
                            short cpStepValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 4:
                            short steamLength = ByteUtils.toShort(payload[offset++]);
                            short steamValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 5:
                            short MultiStepCookingStepsLength = ByteUtils.toShort(payload[offset++]);
                            short MultiStepCookingStepsValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 6:
                            short SteamOvenAutoRecipeModeLength = ByteUtils.toShort(payload[offset++]);
                            short AutoRecipeModeValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 7:
                            short MultiStepCurrentStepsLength = ByteUtils.toShort(payload[offset++]);
                            short MultiStepCurrentStepsValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 8:
                            short SteameOvenPreFlagLength = ByteUtils.toShort(payload[offset++]);
                            short SteameOvenPreFlagValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 9:
                            short weatherDescalingLength = ByteUtils.toShort(payload[offset++]);
                            short weatherDescalingValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 10:
                            short doorStatusLength = ByteUtils.toShort(payload[offset++]);
                            short doorStatusValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 11:
                            short time_H_length = ByteUtils.toShort(payload[offset++]);
                            short time_H_Value = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 12:
                            offset++ ;
                            short SteameOvenLeftMin = ByteUtils.toShort(payload[offset++]);
                            short SteameOvenLeftHours = ByteUtils.toShort(payload[offset++]);
                            break;
                    }
                    argument--;
                }
                break;
            case MsgKeys.getDeviceEventReport: //事件上报
                //设备型号
                short categoryCodeEvent = ByteUtils.toShort(payload[offset++]);
                short event = ByteUtils.toShort(payload[offset++]);
                break;
        }
    }
}
