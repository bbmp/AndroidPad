package com.robam.ventilator.device;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.serialport.helper.SerialPortHelper;
import android.view.View;

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
    //风机启动时间
    public long fanStartTime = 0;

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

    //串口查询
    private ThreadPoolExecutor serialThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交
    private Runnable runSerial = new Runnable() {
        @Override
        public void run() {
            //
            isStopSerial = false;

            while (!isStopSerial) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {}
                byte data[] = SerialVentilator.packQueryCmd();
                SerialPortHelper.getInstance().addCommands(data);
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
    //开始串口查询
    public void startSerialQuery() {
        serialThread.execute(runSerial);
    }
    //停止串口查询
    private boolean isStopSerial;
    public void stopSerialQuery() {
        isStopSerial = true;
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
    public void delayShutDown() {
        if (!MMKVUtils.getDelayShutdown()) //延时关机关闭
            return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shutDownHint();
            }
        }, 2000); //延时，防止自动跳转时覆盖
    }

    private DelayCloseDialog delayCloseDialog;
    public void shutDownHint() {
        Activity activity = AppActivityManager.getInstance().getCurrentActivity();
        if (null == delayCloseDialog && null != activity) {
            delayCloseDialog = new DelayCloseDialog(activity);
            delayCloseDialog.setCancelable(false);
            delayCloseDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) { //立即关机
                        //关机
                        Plat.getPlatform().screenOff(); //熄灭ping
                        Plat.getPlatform().closePowerLamp();//关灯
                        Plat.getPlatform().closeWaterLamp(); //关左灯
                        VentilatorAbstractControl.getInstance().shutDown();
                    }
                    cancleDelayShutDown();
                }
            }, R.id.tv_cancel, R.id.tv_ok);

            int delayTime = Integer.parseInt(MMKVUtils.getDelayShutdownTime()); //延时时间
            delayCloseDialog.tvCountdown.setTotalTime(delayTime * 60);

            delayCloseDialog.tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
                @Override
                public void onCountDown(int currentSecond) {

                    delayCloseDialog.tvCountdown.setText(currentSecond + "s");
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
        if (gear == (byte) 0xA1) //已经是弱档
            return;
        levelCountDown.execute(runLevelCountDown);
    }
    //停止计时
    public void stopLevelCountDown() {
        isLevelCountDown = true;
    }

    //假日模式设置
    public MutableLiveData<Boolean> holiday = new MutableLiveData<>(false);
    //延时关机设置
    public MutableLiveData<Boolean> shutdown = new MutableLiveData<>(false);
    //记录风机运行时间
    public void fanRunTime(int gear) {
        if (gear == VentilatorConstant.FAN_GEAR_CLOSE) {//关挡位
            if (fanStartTime != 0) {
                long runtime = MMKVUtils.getFanRuntime();
                runtime = runtime + Math.abs(System.currentTimeMillis() - fanStartTime); //增加时间
                MMKVUtils.setFanRuntime(runtime);
                fanStartTime = 0;
            }
        } else {
            if (fanStartTime == 0)
                fanStartTime = System.currentTimeMillis();
        }
    }
}
