package com.robam.steamoven.bean;

import android.util.Log;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.steamoven.constant.SteamConstant;
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
        this.subDevices = device.subDevices;
    }

    public SteamOven(String name, String dc, String displayType) {
        super(name, dc, displayType);
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
    //public boolean steamState = false;
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
     * 除垢状态  (1、2、3 阶段一二三；4 - 除垢)
     */
    public short chugouType;



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
     * 2段设置的时间
     */
    public int setTimeH2;
    /**
     * 2段剩余的时间
     */
    public int restTime2;

    public int restTimeH2;
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
    public int setTimeH3;
    /**
     * 3段剩余的时间
     */
    public int restTime3;
    public int restTimeH3;
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
    public boolean onMsgReceived(MqttMsg msg) {
        if (null != msg && null != msg.opt(SteamConstant.SteameOvenStatus)) {
            queryNum = 0; //查询超过一次无响应离线
            status = Device.ONLINE;
            //workStatus = (int) msg.opt(SteamConstant.SteameOvenStatus);
            //totalRemainSeconds = (int) msg.opt(SteamConstant.SteameOvenLeftTime);
            parserMsg(msg);
            return true;
        }
        return super.onMsgReceived(msg);
    }


    public short orderLeftMinutes;
    public short orderMinutesLength;
    public short orderRightMinutes;
    public short orderLeftMinutes1;
    public short orderRightMinutes1;

    public short steamState;
    public short curTemp;
    public short curTemp2;

    public short totalRemainSecondsH;
    //剩余总时间和
    public int totalRemain;
    public int restTimeH;
    public short descaleFlag;

    /**
     * 多段 - 总段数
     */
    public short sectionNumber;
    /**
     * 当前段数/段序
     */
    public int curSectionNbr;

    public short setTimeH;


    private void parserMsg(MqttMsg msg){
        int key = msg.getID();
        if (key==MsgKeys.getDeviceAttribute_Rep){
            this.powerState = (short) msg.optInt(SteamConstant.powerState);
            this.workState = (short) msg.optInt(SteamConstant.workState);
            this.orderLeftMinutes = (short) msg.optInt(SteamConstant.orderLeftMinutes);
            this.orderRightMinutes = (short) msg.optInt(SteamConstant.orderRightMinutes);
            this.orderLeftMinutes1 = (short) msg.optInt(SteamConstant.orderLeftMinutes1);
            this.orderRightMinutes1 = (short) msg.optInt(SteamConstant.orderRightMinutes1);
            this.orderMinutesLength= (short) msg.optInt(SteamConstant.orderMinutesLength);
            this.orderLeftTime= msg.optInt(SteamConstant.orderLeftTime);

            this.faultCode = (short) msg.optInt(SteamConstant.faultCode);
            this.rotateSwitch = (short) msg.optInt(SteamConstant.rotateSwitch);
            this.waterBoxState = (short) msg.optInt(SteamConstant.waterBoxState);
            this.waterLevelState = (short) msg.optInt(SteamConstant.waterLevelState);
            this.doorState = (short) msg.optInt(SteamConstant.doorState);
            this.steamState = (short) msg.optInt(SteamConstant.steamState);
            this.recipeId = (short) msg.optInt(SteamConstant.recipeId);
            this.recipeSetMinutes = (short) msg.optInt(SteamConstant.recipeSetMinutes);
            this.curTemp = (short) msg.optInt(SteamConstant.curTemp);
            this.curTemp2 = (short) msg.optInt(SteamConstant.curTemp2);
            this.totalRemainSeconds = (short) msg.optInt(SteamConstant.totalRemainSeconds);
            this.totalRemainSecondsH = (short) msg.optInt(SteamConstant.totalRemainSeconds2);
            this.totalRemain = msg.optInt(SteamConstant.totalRemain);
            this.descaleFlag = (short) msg.optInt(SteamConstant.descaleFlag);
            this.curSteamTotalHours = (short) msg.optInt(SteamConstant.curSteamTotalHours);
            this.curSteamTotalNeedHours = (short) msg.optInt(SteamConstant.curSteamTotalNeedHours);
            this.cookedTime = (short) msg.optInt(SteamConstant.cookedTime);
            this.chugouType = (short) msg.optInt(SteamConstant.chugouType);
            this.curSectionNbr = (short) msg.optInt(SteamConstant.curSectionNbr);
            this.sectionNumber = (short) msg.optInt(SteamConstant.sectionNumber);
            Log.e("模式",msg.optInt(SteamConstant.mode)+"--");
//            if (curSectionNbr == 0 || curSectionNbr == 1 ) {
//                this.mode = (short) msg.optInt(SteamConstant.mode);
//                this.setUpTemp = (short) msg.optInt(SteamConstant.setUpTemp);
//                this.setDownTemp = (short) msg.optInt(SteamConstant.setDownTemp);
//                this.setTime = (short) msg.optInt(SteamConstant.setTime);
//                this.setTimeH = (short) msg.optInt(SteamConstant.setTimeH);
//                this.restTime = (short) msg.optInt(SteamConstant.restTime);
//                this.restTimeH = (short) msg.optInt(SteamConstant.restTimeH);
//                this.steam = (short) msg.optInt(SteamConstant.steam);
//            }else  {
//                this.mode = (short) msg.optInt(SteamConstant.mode + curSectionNbr);
//                this.setUpTemp = (short) msg.optInt(SteamConstant.setUpTemp + curSectionNbr );
//                this.setDownTemp = (short) msg.optInt(SteamConstant.setDownTemp + curSectionNbr );
//                this.setTime = (short) msg.optInt(SteamConstant.setTime + curSectionNbr );
//                this.setTimeH = (short) msg.optInt(SteamConstant.setTimeH + curSectionNbr );
//                this.restTime = (short) msg.optInt(SteamConstant.restTime + curSectionNbr);
//                this.restTimeH = (short) msg.optInt(SteamConstant.restTimeH +curSectionNbr);
//                this.steam = (short) msg.optInt(SteamConstant.steam + curSectionNbr);
//            }

            this.mode = (short) msg.optInt(SteamConstant.mode);
            this.setUpTemp = (short) msg.optInt(SteamConstant.setUpTemp);
            this.setDownTemp = (short) msg.optInt(SteamConstant.setDownTemp);
            this.setTime = (short) msg.optInt(SteamConstant.setTime);
            this.setTimeH = (short) msg.optInt(SteamConstant.setTimeH);
            this.restTime = (short) msg.optInt(SteamConstant.restTime);
            this.restTimeH = (short) msg.optInt(SteamConstant.restTimeH);
            this.steam = (short) msg.optInt(SteamConstant.steam);

            this.mode2 = (short) msg.optInt(SteamConstant.mode2);
            this.setUpTemp2 = (short) msg.optInt(SteamConstant.setUpTemp2 );
            this.setDownTemp2 = (short) msg.optInt(SteamConstant.setDownTemp2 );
            this.setTime2 = (short) msg.optInt(SteamConstant.setTime2);
            this.setTimeH2 = (short) msg.optInt(SteamConstant.setTime2 );
            this.restTime2 = (short) msg.optInt(SteamConstant.restTime2);
            this.restTimeH2 = (short) msg.optInt(SteamConstant.restTimeH2);
            this.steam2 = (short) msg.optInt(SteamConstant.steam2);

            this.mode3 = (short) msg.optInt(SteamConstant.mode3);
            this.setUpTemp3 = (short) msg.optInt(SteamConstant.setUpTemp3 );
            this.setDownTemp3 = (short) msg.optInt(SteamConstant.setDownTemp3 );
            this.setTime3 = (short) msg.optInt(SteamConstant.setTime3 );
            this.setTimeH3 = (short) msg.optInt(SteamConstant.setTimeH3 );
            this.restTime3 = (short) msg.optInt(SteamConstant.restTime3);
            this.restTimeH3 = (short) msg.optInt(SteamConstant.restTimeH3);
            this.steam3 = (short) msg.optInt(SteamConstant.steam3);
            //onStatusChanged();
        }else if (key==MsgKeys.getDeviceAlarmEventReport){
            this.faultCode = (short) msg.optInt(SteamConstant.faultCode);
            //postEvent(new NewSteamOvenOneAlarmEvent(AbsSteameOvenOneNew.this , faultCode));
        }
    }


}
