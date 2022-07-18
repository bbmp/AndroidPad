package com.robam.steam.bean;

import android.serialport.helper.SerialPortHelper;

import androidx.lifecycle.LiveData;

import com.robam.steam.protocol.serial.ProtoParse;

import java.util.ArrayList;
import java.util.List;

public class SteamOven extends LiveData<SteamOven> {
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

    public static SteamOven getInstance() {
        return SteamOvenHolder.instance;
    }


    private static class SteamOvenHolder {
        private static final SteamOven instance = new SteamOven();
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
        settingMultiModeBeans.addAll(getInstance().multiMode);
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
        getInstance().multiMode.add(settingMultiModeBean);
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
        for (SettingMultiModeBean settingMultiModeBean : getInstance().multiMode) {
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


    public void marshaller(byte[] payload) {
        //命令打包
        if (payload == null || payload.length == 0) {
//            ToastUtils.show("payload数据异常");
            return;
        }
        byte[] bytes = ProtoParse.packCtrlCmd(payload);
        //发送数据
        SerialPortHelper.getInstance().addCommands(bytes);
    }

    public void unmarshaller(byte[] payload) {
        if (payload == null) {
            return;
        }
        switch (payload[0]) {
            case ProtoParse.MSG_TYPE_CMD://功能选择返回
                //系统状态
                sysState = payload[2];
                //--------------故障状态-------------
                fault_temp_up = (payload[3] & 0x01) != 0;
                fault_temp_down = (payload[3] & 0x02) != 0;
                fault_disp_comm = (payload[3] & 0x04) != 0;
                fault_fan_up = (payload[3] & 0x08) != 0;
                fault_heat = (payload[3] & 0x10) != 0;
                fault_water_level = (payload[3] & 0x20) != 0;
                fault_heater_fan = (payload[3] & 0x40) != 0;
                fault_steam_temp = (payload[3] & 0x80) != 0;
                fault_up_and_down_motor = (payload[4] & 0x01) != 0;
                //蜂鸣器
                beep_type = payload[5];
                //工作状态
                workState = payload[6];
                //下层工作状态
                workStateDown = payload[7];
                //---------------工作标志位---------------
                //上照明状态
                upLampState = (payload[8] & 0x01);
                //下照明状态
                downLampState = (payload[8] & 0x02);
                //水箱状态
                waterBoxState = (payload[8] & 0x04) == 0 ? 0 : 1;
                //门控状态
                doorState = (payload[8] & 0x08) == 0 ? 0 : 1;
                //门控状态（微波 锁）
                doorStateRippleLock = (payload[8] & 0x10);
                //升降电机状态
                uppdem = (payload[8] & 0x20) == 0 ? 0 : 1;
                //降电机状态
                downpdem = (payload[8] & 0x40) == 0 ? 0 : 1;
                //旋转烤盘
//                getInstance().rotatePan = (result[8] & 0x40) == 0 ? 0 : 1;
//                i = (result[8] & 0x80);
//                //门锁
//                getInstance().upLampState = (result[9] & 0x01);
                //废水箱标识位
                fWaterBoxState = (payload[9] & 0x02) == 0 ? 0 : 1;
                //水位状态
                waterLevelState = (payload[9] & 0x04) == 0 ? 0 : 1;

                //工作类型
                workType = payload[10];
                //工作模式
                workMode = payload[11];
                //设置上温度
                setTempUp = payload[12] & 0xff;
                //设置下温度
                setTempDown = payload[14];
                //当前上温度
                upTemp = payload[16] & 0xff;
                //当前下温度
                downTemp = payload[18];
                //除垢步骤
                descaleNum = payload[21];

                //蒸汽量大小
//                getInstance().descaleNum = result[22];
//                Thread th = Thread.currentThread();

                //除垢变化量
                descaleNumVariation = payload[24]& 0xff;

//                if (steamOvenChangeAction != null) {
//                    steamOvenChangeAction.onSteamOvenChange(getInstance());
//                }
                break;
            case ProtoParse.MSG_TYPE_POOL://功能查询返回
//                //系统状态
//                getInstance().sysState = result[2];
//                //--------------故障状态-------------
//                getInstance().fault_temp_up = (result[3] & 0x01) != 0;
//                getInstance().fault_temp_down = (result[3] & 0x02) != 0;
//                getInstance().fault_disp_comm = (result[3] & 0x04) != 0;
//                getInstance().fault_fan_up = (result[3] & 0x08) != 0;
//                getInstance().fault_heat = (result[3] & 0x10) != 0;
//                getInstance().fault_water_level = (result[3] & 0x20) != 0;
//                getInstance().fault_heater_fan = (result[3] & 0x40) != 0;
//                getInstance().fault_steam_temp = (result[3] & 0x80) != 0;
//                getInstance().fault_up_and_down_motor = (result[4] & 0x01) != 0;
//                //蜂鸣器
//                getInstance().beep_type = result[5];
//                //工作状态
//                getInstance().workState = result[6];
//                //下层工作状态
//                getInstance().workStateDown = result[7];
//                //---------------工作标志位---------------
//                //上照明状态
//                getInstance().upLampState = (result[8] & 0x01);
//                //下照明状态
//                getInstance().downLampState = (result[8] & 0x02);
//                //水箱状态
//                getInstance().waterBoxState = (result[8] & 0x04);
//                //门控状态（锁）
//                getInstance().doorStateLock = (result[8] & 0x08);
//                //门控状态（微波 锁）
//                getInstance().doorStateRippleLock = (result[8] & 0x10);
//                //升降电机状态
//                getInstance().uppdem = (result[8] & 0x20) == 0 ? 0 : 1;
//                //降电机状态
//                getInstance().downpdem = (result[8] & 0x40) == 0 ? 0 : 1;
//                //旋转烤盘
////                getInstance().rotatePan = (result[8] & 0x40) == 0 ? 0 : 1;
////                i = (result[8] & 0x80);
////                //门锁
////                getInstance().upLampState = (result[9] & 0x01);
////                //旋转烤架开关
////                getInstance().pdem = (result[9] & 0x02);
//                //水箱状态
//                getInstance().waterLevelState = (result[9] & 0x04) == 0 ? 0 : 1;
////                getInstance().waterLevelState = (result[9] & 0x08);
//                //门状态
//                getInstance().doorState = (result[9] & 0x10) == 0 ? 0 : 1;
//
//                //工作类型
//                getInstance().workType = result[10];
//                //工作模式
//                getInstance().workMode = result[11];
//                //设置上温度
//                getInstance().setTempUp = result[12] & 0xff;
//                //设置下温度
//                getInstance().setTempDown = result[14];
//                //当前上温度
//                getInstance().upTemp = result[16] & 0xff;
//                //当前下温度
//                getInstance().downTemp = result[18];
//                //除垢步骤
//                getInstance().descaleNum = result[21];
//
//                //蒸汽量大小
////                getInstance().descaleNum = result[22];
////                Thread th = Thread.currentThread();
//
//                //除垢变化量
//                getInstance().descaleNumVariation = result[24]& 0xff;
//
//                if (steamOvenChangeAction != null) {
//                    steamOvenChangeAction.onSteamOvenChange(getInstance());
//                }
                break;
        }
    }
}
