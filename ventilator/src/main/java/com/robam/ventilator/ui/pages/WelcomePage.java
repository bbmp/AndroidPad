package com.robam.ventilator.ui.pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.serialport.helper.SerialPortHelper;
import android.serialport.helper.SphResultCallback;

import com.robam.common.ui.HeadPage;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.common.utils.PreferenceUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.manager.VenWifiManager;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.activity.HomeActivity;
import com.robam.ventilator.ui.activity.WifiSettingActivity;
import com.robam.ventilator.ui.service.AlarmBleService;
import com.robam.ventilator.ui.service.AlarmMqttService;
import com.robam.ventilator.ui.service.AlarmVentilatorService;

//这里一般作为主入口进入
public class WelcomePage extends VentilatorBasePage {
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_welcome;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //初始化
        SerialVentilator.init_decoder();
        //打开串口
        SerialPortHelper.getInstance().openDevice(new SphResultCallback() {
            @Override
            public void onSendData(byte[] sendCom, int len) {
            }

            @Override
            public void onReceiveData(byte[] data, int len) {
                SerialVentilator.parseSerial(data, len);
            }

            @Override
            public void onOpenSuccess() {

                //开机
                if (HomeVentilator.getInstance().startup == 0x00) {
                    HomeVentilator.getInstance().openVentilator();
                }

            }

            @Override
            public void onOpenFailed() {
                LogUtils.i("serial open failed" + Thread.currentThread().getName());
            }
        });

        //打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled())
            checkPermissions();
        //打开wifi
        checkWifiPermmissions();

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(200);
                } catch (Exception e) {}
                //启动定时服务
                Intent intent = new Intent(getContext().getApplicationContext(), AlarmMqttService.class);
                intent.setPackage(getContext().getPackageName());
                getContext().startService(intent);
                Intent bleIntent = new Intent(getContext().getApplicationContext(), AlarmBleService.class);
                bleIntent.setPackage(getContext().getPackageName());
                getContext().startService(bleIntent);
                Intent venIntent = new Intent(getContext().getApplicationContext(), AlarmVentilatorService.class);
                venIntent.setPackage(getContext().getPackageName());
                getContext().startService(venIntent);
            }
        }.start();

        boolean first = PreferenceUtils.getBool(getActivity(), VentilatorConstant.EXTRA_FIRST, false);
        if (!first) {
            //首次进入
            Intent intent = new Intent(getActivity(), WifiSettingActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, true);
            startActivity(intent);
            PreferenceUtils.setBool(getActivity(), VentilatorConstant.EXTRA_FIRST, true);
//            getActivity().finish();
        } else {
            //跳转到首页
            HomeActivity.start(getActivity());
        }
    }

    //请求蓝牙权限
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
        }, Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    //请求wifi权限
    private void checkWifiPermmissions() {
        //
        PermissionUtils.requestPermission(this, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onSucceed() {
                //打开wifi
                WifiManager mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                VenWifiManager.openWifi(mWifiManager);
            }

            @Override
            public void onFailed() {

            }
        }, Manifest.permission.CHANGE_WIFI_STATE);
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
}
