package com.robam.ventilator.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.serialport.helper.SerialPortHelper;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

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

import org.json.JSONArray;
import org.json.JSONObject;

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
    public boolean holiday = MMKVUtils.getHoliday(); //假日模式开关
    //假日模式每周通风时间
    public String weekTime = MMKVUtils.getHolidayWeekTime(); //每周通风时间
    //假日模式天数
    public String holidayDay = MMKVUtils.getHolidayDay(); //假日模式每隔几天通风
    //风机最后运行时间
    public long fanOffTime = MMKVUtils.getFanOffTime();

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
                if (a6CountTime >= 1800) {
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

                    if (startup == (byte)0x00) { //先开机
                        VentilatorAbstractControl.getInstance().powerOnGear(VentilatorConstant.FAN_GEAR_WEAK);
                        Plat.getPlatform().screenOn();
                        Plat.getPlatform().openPowerLamp();
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

            //切换到弱档
            VentilatorAbstractControl.getInstance().powerOnGear(VentilatorConstant.FAN_GEAR_WEAK);
            Plat.getPlatform().screenOn();
            Plat.getPlatform().openPowerLamp();

            while (!isAutoCountDown) {

                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                autoCountTime++;

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
        } else
            shutDownHint(false);
    }

    private DelayCloseDialog delayCloseDialog;
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
                        Plat.getPlatform().screenOff(); //熄灭ping
                        Plat.getPlatform().closePowerLamp();//关灯
                        Plat.getPlatform().closeWaterLamp(); //关左灯
                        VentilatorAbstractControl.getInstance().shutDown();
                    }
                }
            });
            delayCloseDialog.tvCountdown.start();

            delayCloseDialog.show();
        }
    }
    //打开烟机
    public void openVentilator() {
        VentilatorAbstractControl.getInstance().powerOn();
        Plat.getPlatform().screenOn();
        Plat.getPlatform().openPowerLamp();
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
    }

    //关闭延时关机
    public void cancleDelayShutDown() {
        if (null != delayCloseDialog && delayCloseDialog.isShow()) {
            delayCloseDialog.tvCountdown.stop();
            delayCloseDialog.dismiss();
            delayCloseDialog = null;
        }
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
    //停止自动同分
    public void stopAutoCountDown() {
        isLevelCountDown = true;
    }

    //智能设置
    public MutableLiveData<Boolean> smartSet = new MutableLiveData<>(false);
    //油网清洗自动提示
    public MutableLiveData<Boolean> oilClean = new MutableLiveData<>(false);

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
            HomeVentilator.getInstance().fanOffTime = curTime;
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
                        HomeVentilator.getInstance().status = HomeVentilator.getInstance().startup;
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
}
