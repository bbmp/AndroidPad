package com.robam.ventilator.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.serialport.helper.SerialPortHelper;
import android.serialport.helper.SphResultCallback;
import android.view.View;

import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.AppActivityManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.NetworkUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.common.utils.StorageUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.common.utils.WindowsUtils;
import com.robam.steamoven.utils.SteamDataUtil;
import com.robam.ventilator.R;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.manager.VenWifiManager;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;
import com.robam.ventilator.ui.service.AlarmBleService;
import com.robam.ventilator.ui.service.AlarmMqttService;
import com.robam.ventilator.ui.service.AlarmVentilatorService;

import java.io.File;
import java.lang.reflect.Field;

//主页
public class HomeActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
    //上电跳转
    public static void powerStart(Activity activity) {
        Intent intent = new Intent();
        intent.putExtra(VentilatorConstant.EXTRA_POWERON, true);
        intent.setClass(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private BluetoothAdapter bluetoothAdapter;

    private static final String PASSWORD_LOGIN = "mobilePassword";
    private static final String WAITING_AC_NAME = "WaringActivity";
    private static final String WAITING_AC_FILE = "fromFlag";//告警页面属性；是否从烟机直接进入告警页面（1 - 是；0 -否）

    //是否上电
    private boolean powerOn = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_home;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            powerOn = bundle.getBoolean(VentilatorConstant.EXTRA_POWERON, false);
        }
        //注册wifi广播
        HomeVentilator.getInstance().registerWifiReceiver(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {

            } else {
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                startActivityForResult(intent, 100);
            }
        }
        WindowsUtils.initPopupWindow(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = AppActivityManager.getInstance().getCurrentActivity();
                //浮窗点击 快捷入口
                String acName = activity.getClass().getName();
                LogUtils.i("acName "+acName);
                Intent intent = new Intent();
                intent.setClass(activity, ShortcutActivity.class);
                startActivity(intent);

                //判断当前是否为WarningActivity,若是,则调用其Activity的finish方法
                if(acName.contains(WAITING_AC_NAME)){
                    //获取其 WarringActivity 中 fromFlag 属性是否等于1
                    Class<? extends Activity> aClass = activity.getClass();
                    try {
                        Field fromFlag = aClass.getDeclaredField(WAITING_AC_FILE);
                        fromFlag.setAccessible(true);
                        Object fromFlagValue = fromFlag.get(activity);
                        if(fromFlagValue instanceof Integer && ((Integer) fromFlagValue).intValue() == 1){
                            activity.finish();
                        }
                    } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {
            int wifiSleepValue = Settings.System.getInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
            Settings.Global.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //关机
        if (powerOn) {
            HomeVentilator.getInstance().closeVentilator(false); //上电不需要回首页
            //开始慢闪
            HomeVentilator.getInstance().startFlash();
            powerOn = false;
        }

        WindowsUtils.hidePopupWindow();
        Runtime r = Runtime.getRuntime();

        LogUtils.e("最大可用内存:" + r.maxMemory() / (1024*1024) + "M");
        LogUtils.e("当前可用内存:" + r.totalMemory()/ (1024*1024) + "M");
        LogUtils.e("当前空闲内存:" + r.freeMemory() / (1024*1024) + "M");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        WindowsUtils.showPopupWindow();
        LogUtils.e("onPause");
    }

    @Override
    protected void initData() {
        //打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled())
            checkPermissions();
        //打开wifi
        checkWifiPermmissions();

        //监听网络状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    //启动mqtt
                    MqttManager.getInstance().start(HomeActivity.this, Plat.getPlatform(), VentilatorFactory.getTransmitApi());
                    //未登录
                    if (null == AccountInfo.getInstance().getUser().getValue()) {
                        String json = MMKVUtils.getUser();
                        try {
                            //密码登录 自动登录
                            UserInfo info = new Gson().fromJson(json, UserInfo.class);
                            getToken(info.phone, info.password);
                        } catch (Exception e) {

                        }
                    }
                    HomeVentilator.getInstance().autoSetTime(HomeActivity.this);
                }
                else {
                    //断网
                    MqttManager.getInstance().stop();
                    for (Device device: AccountInfo.getInstance().deviceList) {
                        if (IDeviceType.RRQZ.equals(device.dc) || IDeviceType.RZNG.equals(device.dc))
                            continue;
                        device.status = Device.OFFLINE;
                    }
                }
            }
        });
        new Thread(()->{
            try {
                Thread.sleep(4000);//延迟4秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            //更新服务
            if (!DeviceUtils.isAppInstalled(getContext(), "com.robam.update")) {
                DeviceUtils.copyFileIfNeed(getContext(), "UpdateService.apk", StorageUtils.getDataDir() + File.separator + "UpdateService.apk");
                DeviceUtils.installSilent(StorageUtils.getDataDir() + File.separator + "UpdateService.apk");
            }
            SteamDataUtil.getSteamData(HomeActivity.this,IDeviceType.SERIES_STEAM);//获取一体机数据
            SteamDataUtil.getDeviceErrorInfo(HomeActivity.this);//获取告警信息数据
        }).start();
        LogUtils.i("Thread HomeActivity " + Thread.currentThread().getId());
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
                WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
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
        stopService(new Intent(this.getApplicationContext(), AlarmMqttService.class));
        stopService(new Intent(this.getApplicationContext(), AlarmBleService.class));
        stopService(new Intent(this.getApplicationContext(), AlarmVentilatorService.class)); //停止串口查询
        //取消注册广播
        HomeVentilator.getInstance().unregisterWifiReceiver(this.getApplicationContext());
    }

    //获取token
    private void getToken(String phone, String pwd) {
        CloudHelper.getToken(this, PASSWORD_LOGIN, phone, "", pwd, "roki_client", "test", "RKDRD",
                GetTokenRes.class, new RetrofitCallback<GetTokenRes>() {
                    @Override
                    public void onSuccess(GetTokenRes getTokenRes) {
                        if (null != getTokenRes) {
                            getUserInfo(getTokenRes.getAccess_token());
                        } else {
                            ToastUtils.showShort(getContext(), R.string.ventilator_request_failed);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("getToken" + err);
                    }
                });
    }
    //获取用户信息
    private void getUserInfo(String access_token) {
        CloudHelper.getUserInfo(this, access_token, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    UserInfo info = getUserInfoRes.getUser();
                    info.loginType = PASSWORD_LOGIN;
                    //保存用户信息及登录状态
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(getContext(), R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("getUserInfo" + err);
            }
        });
    }
}