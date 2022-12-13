package com.robam.ventilator.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.serialport.helper.SerialPortHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.constant.ComnConstant;
import com.robam.common.device.Plat;
import com.robam.common.manager.AppActivityManager;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.activity.HomeActivity;
import com.robam.ventilator.ui.dialog.DelayCloseDialog;
import com.robam.ventilator.ui.receiver.VentilatorReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeVentilator {
    //锁屏框
    public IDialog homeLock;
    //风机启动时间
    public long fanStartTime = 0;
    //假日模式
//    public boolean holiday = MMKVUtils.getHoliday(); //假日模式开关
    //假日模式每周通风时间
//    public String weekTime = MMKVUtils.getHolidayWeekTime(); //每周通风时间
    //假日模式天数
//    public String holidayDay = MMKVUtils.getHolidayDay(); //假日模式每隔几天通风
    //风机最后运行时间
    public long fanOffTime = MMKVUtils.getFanOffTime();
    //触发5分钟降弱档
    public boolean autoWeak;
    //烟机操作时间
    public long operationTime = 0;

    private ThreadPoolExecutor A6CountDown = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    //爆炒档倒计时
    private Runnable runA6CountDown = new Runnable() {
        @Override
        public void run() {
            //爆炒档倒计时
            int a6CountTime = 0;
            isStopA6CountDown = false;

            while (!isStopA6CountDown) {

                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                a6CountTime++;
                LogUtils.e("a6CountTime = " + a6CountTime);
                if (a6CountTime >= 1760) {
                    //切换到高档
                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                    return;
                }
            }
        }
    };

    //灶具最小火力倒计时
    private ThreadPoolExecutor levelCountDown = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    private Runnable runLevelCountDown = new Runnable() {
        @Override
        public void run() {
            //最小档计时计时
            int levelCountTime = 0;
            isLevelCountDown = false;

            while (!isLevelCountDown) {

                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                levelCountTime++;
                LogUtils.e("levelCountTime = " + levelCountTime);
                if (levelCountTime >= 3000) { //5分钟
                    //切换到弱档
                    autoWeak = true;

                    if (startup == (byte)0x00) { //先开机

                        openVentilatorGear(VentilatorConstant.FAN_GEAR_WEAK);
                    } else {
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_WEAK); //弱档

                    }
                    return;
                }
            }
        }
    };

    //自动通风
    private ThreadPoolExecutor autoCountDown = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    private Runnable runAutoCountDown = new Runnable() {
        @Override
        public void run() {
            //自动换气倒计时
            int autoCountTime = 0;
            isAutoCountDown = false;

            //开机切换到弱档
            openVentilatorGear(VentilatorConstant.FAN_GEAR_WEAK);

            while (!isAutoCountDown) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                autoCountTime++;
                LogUtils.e("autoCountTime " + autoCountTime + "startup=" + startup);

                if (autoCountTime >= 1800) { //3分钟
                    //关闭烟机
                    closeVentilator();
                    return;
                }
            }
        }
    };

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
    public byte param7 = (byte) (MMKVUtils.getSmartSet() ? 0x01:0x00);
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
            JSONArray jsonArray = new JSONArray();
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (guid.equals(device.guid) && ((device instanceof Pan) || (device instanceof Stove))) { //已经存在该子设备
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOpt(VentilatorConstant.DEVICE_GUID, guid);
                    jsonObject.putOpt(VentilatorConstant.DEVICE_BIZ, biz);
                    jsonObject.putOpt(VentilatorConstant.DEVICE_STATUS, status);
                    jsonArray.put(jsonObject);
                } else if ((device instanceof Pan) || (device instanceof Stove)) { //其他存在的子设备
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOpt(VentilatorConstant.DEVICE_GUID, device.guid);
                    jsonObject.putOpt(VentilatorConstant.DEVICE_BIZ, device.bid);
                    jsonObject.putOpt(VentilatorConstant.DEVICE_STATUS, device.status);
                    jsonArray.put(jsonObject);
                }
            }

            msg.putOpt(ComnConstant.DEVICE_NUM, jsonArray.length() + 1); //设备个数
            msg.putOpt(VentilatorConstant.SUB_DEVICES, jsonArray);

            MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取烟机子设备
    public void setSubDevices(MqttMsg msg) throws Exception{
        JSONArray jsonArray = new JSONArray();
        for (Device device: AccountInfo.getInstance().deviceList) {
            if ((device instanceof Pan) || (device instanceof Stove)) { //其他存在的子设备
                JSONObject jsonObject = new JSONObject();
                jsonObject.putOpt(VentilatorConstant.DEVICE_GUID, device.guid);
                jsonObject.putOpt(VentilatorConstant.DEVICE_BIZ, device.bid);
                jsonObject.putOpt(VentilatorConstant.DEVICE_STATUS, device.status);
                jsonArray.put(jsonObject);
            }
        }
        //读文件
        if (jsonArray.length() == 0) {
            Set<String> deviceSets = MMKVUtils.getSubDevice();
            if (null != deviceSets) {
                for (String json : deviceSets) {
                    Device subDevice = new Gson().fromJson(json, Device.class);
                    if (IDeviceType.RZNG.equals(subDevice.dc) || IDeviceType.RRQZ.equals(subDevice.dc)) {//锅
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.putOpt(VentilatorConstant.DEVICE_GUID, subDevice.guid);
                        jsonObject.putOpt(VentilatorConstant.DEVICE_BIZ, subDevice.bid);
                        jsonObject.putOpt(VentilatorConstant.DEVICE_STATUS, subDevice.status);
                        jsonArray.put(jsonObject);
                    }
                }
            }
        }
        msg.putOpt(ComnConstant.DEVICE_NUM, jsonArray.length() + 1); //设备个数
        msg.putOpt(VentilatorConstant.SUB_DEVICES, jsonArray);
    }

    //爆炒档开始倒计时
    private boolean isStopA6CountDown;
    public void startA6CountDown() {

        A6CountDown.execute(runA6CountDown);
    }
    //停止倒计时
    public void stopA6CountDown() {
        isStopA6CountDown = true;
    }
    //延时关机提示
    public void delayShutDown(boolean isLink) {  //是否联动关机
        if (isLock())  //锁屏状态，不响应
            return;
        if (gear == (byte) 0xA0 && isLink) //联动关机 挡位未开
            return; //不响应
        if (gear == (byte) 0xA0 || !MMKVUtils.getDelayShutdown()) { //挡位没开或者延时关机关闭 立即关机
            //关机
            closeVentilator();
            return;
        }

        if (isLink) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shutDownHint(isLink);
                }
            }, 2000); //延时，防止自动跳转时覆盖
        } else {


            shutDownHint(false);
        }
    }

    private DelayCloseDialog delayCloseDialog;
    public int remainTime; //通风剩余时间
    //定时关机
    public void timeShutdown(int timingTime) {
        Activity activity = AppActivityManager.getInstance().getCurrentActivity();
        if (null == delayCloseDialog && null != activity) {
            delayCloseDialog = new DelayCloseDialog(activity);
            delayCloseDialog.setCancelable(false);
            delayCloseDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) { //立即关机
                        closeVentilator();
                    }
                    cancleDelayShutDown();
                }
            }, R.id.tv_cancel, R.id.tv_ok);

            delayCloseDialog.tvCountdown.setTotalTime(timingTime * 60);

            delayCloseDialog.setmContentVisible(View.GONE);
            delayCloseDialog.tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
                @Override
                public void onCountDown(int currentSecond) {
                    remainTime = currentSecond;
                    delayCloseDialog.tvCountdown.setText(currentSecond + "s后关机");
                    delayCloseDialog.setContentText(currentSecond + "s");
                    if (currentSecond <= 0) {
                        cancleDelayShutDown();
                        //关机
                        closeVentilator();
                    }
                }
            });
            delayCloseDialog.tvCountdown.start();

            delayCloseDialog.show();
        }
    }
    public void shutDownHint(boolean isLink) { //是否联动关机
        Activity activity = AppActivityManager.getInstance().getCurrentActivity();
        if (null == delayCloseDialog && null != activity) {
            delayCloseDialog = new DelayCloseDialog(activity);
            delayCloseDialog.setCancelable(false);
            delayCloseDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) { //立即关机
                        closeVentilator();
                    }
                    cancleDelayShutDown();
                }
            }, R.id.tv_cancel, R.id.tv_ok);

            int delayTime = Integer.parseInt(MMKVUtils.getDelayShutdownTime()); //延时时间
            delayCloseDialog.tvCountdown.setTotalTime(delayTime * 60);

            if (!isLink) //主动关机
                delayCloseDialog.setmContentVisible(View.GONE);
            delayCloseDialog.tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
                @Override
                public void onCountDown(int currentSecond) {

                    delayCloseDialog.tvCountdown.setText(currentSecond + "s后关机");
                    delayCloseDialog.setContentText(currentSecond + "s");
                    if (currentSecond <= 0) {
                        cancleDelayShutDown();
                        //关机
                        closeVentilator();
                    }
                }
            });
            delayCloseDialog.tvCountdown.start();

            if (!isLink)
                VentilatorAbstractControl.getInstance().beep();
            delayCloseDialog.show();
        } else {
            if (!isLink) { //主动关机
                //关机
                closeVentilator();

                if (null != delayCloseDialog)
                    delayCloseDialog.tvCountdown.stop();
                cancleDelayShutDown();
            }
        }
    }
    //打开烟机并且开挡位 联动功能
    public void openVentilatorGear(int gear) {
        VentilatorAbstractControl.getInstance().powerOnGear(gear);
        Plat.getPlatform().screenOn();
        Plat.getPlatform().openPowerLamp();
        updateOperationTime(); //开机时间
    }
    //打开烟机
    public void openVentilator() {
        VentilatorAbstractControl.getInstance().powerOn();
        Plat.getPlatform().screenOn();
        Plat.getPlatform().openPowerLamp();
        updateOperationTime(); //开机时间

//        Activity activity = AppActivityManager.getInstance().getCurrentActivity();
//        if (null != activity)
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    //关闭烟机
    public void closeVentilator() {
        //关机
        Plat.getPlatform().screenOff(); //熄灭ping
        Plat.getPlatform().closePowerLamp();//关灯
        Plat.getPlatform().closeWaterLamp(); //关左灯
        VentilatorAbstractControl.getInstance().shutDown();

        Activity activity = AppActivityManager.getInstance().getCurrentActivity();
        if (null != activity) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(activity, HomeActivity.class); //回首页
            activity.startActivity(intent);
        }
//        activity = AppActivityManager.getInstance().getCurrentActivity();
//        if (null != activity)
//            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    //关闭延时关机
    public void cancleDelayShutDown() {
        if (null != delayCloseDialog && delayCloseDialog.isShow()) {
            delayCloseDialog.tvCountdown.stop();
            delayCloseDialog.dismiss();
            delayCloseDialog = null;
        }
        remainTime = 0;
    }

    //灶具火力最小档计时
    private boolean isLevelCountDown;
    public void startLevelCountDown() {
        if (gear == (byte) 0xA1 || gear == (byte) 0xA0) //已经是弱档 或未开风量
            return;
        if (startup == (byte) 0x00) //关机状态
            return;
        levelCountDown.execute(runLevelCountDown);
    }
    //停止计时
    public void stopLevelCountDown() {
        isLevelCountDown = true;
    }
    //自动通风倒计时
    private boolean isAutoCountDown;
    public void startAutoCountDown() {
        autoCountDown.execute(runAutoCountDown);
    }
    //停止自动通风
    public void stopAutoCountDown() {
        isAutoCountDown = true;
    }


    //记录风机运行时间
    public void fanRunTime(int gear) {
        long curTime = System.currentTimeMillis();
        if (gear == VentilatorConstant.FAN_GEAR_CLOSE) {//关挡位
            if (fanStartTime != 0) {
                long runtime = MMKVUtils.getFanRuntime();
                runtime = runtime + Math.abs(curTime - fanStartTime); //增加时间
                MMKVUtils.setFanRuntime(runtime);
                fanStartTime = 0;
            }
            //记录风机最后运行时间
            MMKVUtils.setFanOffTime(curTime);
            fanOffTime = curTime;
        } else {
            if (fanStartTime == 0)
                fanStartTime = curTime;
        }

    }
    //锁屏
    public void screenLock() {
        if (null == homeLock) {
            Activity activity = AppActivityManager.getInstance().getCurrentActivity();
            if (null != activity) {
                homeLock = VentilatorDialogFactory.createDialogByType(activity, DialogConstant.DIALOG_TYPE_LOCK);
                homeLock.setCancelable(false);
                //长按解锁
                ImageView imageView = homeLock.getRootView().findViewById(R.id.iv_lock);
                ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        homeLock.dismiss();
//                    if (null != rvFunctionAdapter)
//                        rvFunctionAdapter.setPickPosition(-1);
                        //关闭油网清洗
                        VentilatorAbstractControl.getInstance().closeOilClean();
                        //关灯
                        Plat.getPlatform().closeWaterLamp();
                        //取消油网清洗
                        status = startup;
                        return true;
                    }
                });
            }
        }
        homeLock.show();
    }
    //是否油网清洗状态
    public boolean isLock() {
        if (null != homeLock && homeLock.isShow())
            return true;
        return false;
    }
    //关闭油网清洗
    public void closeLock() {
        if (isLock())
            homeLock.dismiss();
    }
    //注册WiFi广播
    private VentilatorReceiver ventilatorReceiver;
    public void registerWifiReceiver(Context context) {
        if (null == ventilatorReceiver) {
            ventilatorReceiver = new VentilatorReceiver();
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
            filter.addAction(Intent.ACTION_TIME_CHANGED); //时间变化
            context.registerReceiver(ventilatorReceiver, filter);
        }
    }
    public void unregisterWifiReceiver(Context context) {
        if (null != ventilatorReceiver) {
            try {
                context.unregisterReceiver(ventilatorReceiver);
                ventilatorReceiver = null;
            } catch (Exception e) {}
        }
    }
    //是否开机状态
    public boolean isStartUp() {
        if (startup == (byte) 0x01)
            return true;
        return false;
    }
    //更新操作时间
    public void updateOperationTime() {
        operationTime = System.currentTimeMillis();
        LogUtils.i("operationTime = " + operationTime);
    }
}
