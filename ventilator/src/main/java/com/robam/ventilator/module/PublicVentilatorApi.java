package com.robam.ventilator.module;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.serialport.helper.SerialPortHelper;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.activity.LoginPhoneActivity;
import com.robam.ventilator.ui.activity.MatchNetworkActivity;
import com.robam.ventilator.ui.service.AlarmBleService;
import com.robam.ventilator.ui.service.AlarmMqttService;
import com.robam.ventilator.ui.service.AlarmVentilatorService;

import org.json.JSONArray;

public class PublicVentilatorApi implements IPublicVentilatorApi {
    //给外部模块调用
    @Override
    public void setFanGear(int gear) {
        VentilatorAbstractControl.getInstance().setFanGear(gear);
    }

    @Override
    public int getFanGear() {
        return HomeVentilator.getInstance().gear & 0xFF;
    }

    @Override
    public void setFanLight(int lightOn) {
        VentilatorAbstractControl.getInstance().setFanLight(lightOn);
    }

    @Override
    public int getFanLight() {
        if (HomeVentilator.getInstance().lightOn == (byte) 0xA1)
            return VentilatorConstant.FAN_LIGHT_OPEN;
        else
            return VentilatorConstant.FAN_LIGHT_CLOSE;
    }

    @Override
    public void startMatchNetwork(Context context, String model) {
        Intent intent = new Intent();
        intent.putExtra(VentilatorConstant.EXTRA_MODEL, model);
        intent.setClass(context, MatchNetworkActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void startLogin(Context context) {
        //调用手机登录
        context.startActivity(new Intent(context, LoginPhoneActivity.class));
    }

    @Override
    public void queryAttribute() {
        VentilatorAbstractControl.getInstance().queryAttribute(); //查询状态
    }

    @Override
    public boolean isStartUp() {
        return (HomeVentilator.getInstance().startup == (byte) 0x01) ? true:false;
    }

    @Override
    public void shutDown() {
        VentilatorAbstractControl.getInstance().shutDown();
    }

    @Override
    public void closeService(Context context) {
        //关闭定时任务
        context.stopService(new Intent(context, AlarmMqttService.class));
        context.stopService(new Intent(context, AlarmBleService.class));
        //关闭串口查询
        context.stopService(new Intent(context, AlarmVentilatorService.class));
    }

    @Override
    public void openVentilator() {
//        VentilatorAbstractControl.getInstance().powerOn();
        HomeVentilator.getInstance().openVentilator();
    }

    @Override
    public void startService(Context context) {
        //启动定时服务
        Intent intent = new Intent(context, AlarmMqttService.class);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
        //蓝牙查询
        Intent bleIntent = new Intent(context, AlarmBleService.class);
        bleIntent.setPackage(context.getPackageName());
        context.startService(bleIntent);
        //串口查询
        Intent venIntent = new Intent(context, AlarmVentilatorService.class);
        venIntent.setPackage(context.getPackageName());
        context.startService(venIntent);
    }

    @Override
    public void setColorLamp() {
        VentilatorAbstractControl.getInstance().setColorLamp();
    }

    @Override
    public void stoveLevelChanged(String stoveGuid, int leftLevel, int rightLevel) {
        //这里要判断烟灶联动开关
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (null != stoveGuid && stoveGuid.equals(device.guid) && device instanceof Stove) {
                Stove stove = (Stove) device; //当前灶具
                LogUtils.e("stove.leftLevel= " + stove.leftLevel + " stove.rightLevel=" + stove.rightLevel);
                LogUtils.e("leftLevel= " + leftLevel + " rightLevel=" + rightLevel);
                if (stove.leftLevel == 0 && stove.rightLevel == 0 && (leftLevel != 0 || rightLevel != 0)) { //刚开火
                    //关闭延时关机
                    HomeVentilator.getInstance().cancleDelayShutDown();
                    //烟机没有启动打开烟机
                    if (MMKVUtils.getFanStove() && !HomeVentilator.getInstance().isLock()) {//烟灶联动开启且不锁屏
                        if (!isStartUp()) {

                            HomeVentilator.getInstance().openVentilatorGear(VentilatorConstant.FAN_GEAR_MID); //开机并设置挡位
                        } else if (HomeVentilator.getInstance().gear != (byte) 0x06) //非爆操档
                            VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                    }
                    if (stove.leftStatus == 0 && leftLevel != 0) //如果不是工作状态，改成工作状态
                        stove.leftStatus = 2;
                    if (stove.rightStatus == 0 && rightLevel != 0)
                        stove.rightStatus = 2;
                } else if ((stove.leftLevel != 0 || stove.rightLevel != 0) && (leftLevel == 0 && rightLevel == 0)) {//刚关火
                    if (MMKVUtils.getFanStove() && !HomeVentilator.getInstance().isLock()) //烟灶联动开启非锁屏状态
                        HomeVentilator.getInstance().delayShutDown(true); //延迟关机提示
                    stove.leftStatus = 0;
                    stove.rightStatus = 0;
                }
                //火力最小档计时
                if (MMKVUtils.getFanStove() && MMKVUtils.getFanStoveGear() && !HomeVentilator.getInstance().isLock()) {//烟灶联动开启 自动匹配风量开启 且非锁屏状态
                    if ((leftLevel == 1 && rightLevel <= 1) || (rightLevel == 1 && leftLevel <= 1)) {
                        HomeVentilator.getInstance().startLevelCountDown();
                    } else
                        HomeVentilator.getInstance().stopLevelCountDown();
                }
                break;
            }
        }
    }

    @Override
    public void delayShutDown() {
        HomeVentilator.getInstance().delayShutDown(false); //主动关机
    }

    @Override
    public void closeDelayDialog() {
        HomeVentilator.getInstance().cancleDelayShutDown();
    }

    @Override
    public void updateOperationTime() {
        HomeVentilator.getInstance().updateOperationTime();
    }

    @Override
    public void setSubDevices(MqttMsg msg) throws Exception {
        HomeVentilator.getInstance().setSubDevices(msg);
    }

}
