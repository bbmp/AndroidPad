package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.serialport.helper.SerialPortHelper;
import android.serialport.helper.SphResultCallback;
import android.util.DisplayMetrics;
import android.view.View;

import com.clj.fastble.BleManager;
import com.robam.common.bean.AccountInfo;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.NetworkUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.WindowsUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.service.AlarmService;

//主页
public class HomeActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private BluetoothAdapter bluetoothAdapter;
    private Intent intent;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_home;
    }

    @Override
    protected void initView() {

        WindowsUtils.initPopupWindow(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //浮窗点击 快捷入口
                startActivity(ShortcutActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowsUtils.hidePopupWindow();
        Runtime r = Runtime.getRuntime();

        LogUtils.e("最大可用内存:" + r.maxMemory() / (1024*1024) + "M");
        LogUtils.e("当前可用内存:" + r.totalMemory()/ (1024*1024) + "M");
        LogUtils.e("当前空闲内存:" + r.freeMemory() / (1024*1024) + "M");
    }

    @Override
    protected void onPause() {
        super.onPause();
        WindowsUtils.showPopupWindow();
    }

    @Override
    protected void initData() {
        //打开串口
//        SerialPortHelper.getInstance().openDevice(new SphResultCallback() {
//            @Override
//            public void onSendData(byte[] sendCom) {
//
//            }
//
//            @Override
//            public void onReceiveData(byte[] data) {
//                LogUtils.e(StringUtils.bytes2Hex(data));
//                SerialVentilator.parseSerial(data);
//            }
//
//            @Override
//            public void onOpenSuccess() {
//                //开机
//                if (Ventilator.getInstance().startup == 0x00)
//                    SerialPortHelper.getInstance().addCommands(SerialVentilator.powerOn());
//                //循环查询
//                byte data[] = SerialVentilator.packQueryCmd();
//                while (true) {
//                    SerialPortHelper.getInstance().addCommands(data);
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onOpenFailed() {
//                LogUtils.i("serial open failed" + Thread.currentThread().getName());
//            }
//        });
        //打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled())
            checkPermissions();
        //启动定时服务
        intent = new Intent(this.getApplicationContext(), AlarmService.class);
        intent.setPackage(getPackageName());
        startService(intent);
//初始化主设备mqtt收发 烟机端只要网络连接上就需要启动mqtt服务，锅和灶不用登录
        //监听网络状态
        MqttManager.getInstance().start(this, VentilatorFactory.getPlatform(), VentilatorFactory.getProtocol());
        //初始网络状态
        if (NetworkUtils.isConnect(this))
            AccountInfo.getInstance().getConnect().setValue(true);
    }

    private void checkPermissions() {
        //请求权限
        PermissionUtils.requestPermission(this, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onSucceed() {
                onPermissionGranted();
            }

            @Override
            public void onFailed() {
                //权限未给
                LogUtils.e("requestPermission onFailed");
            }
        }, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    //已授权
    @SuppressLint("MissingPermission")
    private void onPermissionGranted() {
        try {
            bluetoothAdapter.enable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //解决内存泄漏
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WindowsUtils.closePopupWindow();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
        //关闭定时任务
        stopService(intent);
    }
}