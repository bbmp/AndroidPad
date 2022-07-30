package com.robam.steam.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.activity.BaseActivity;
import com.robam.steam.R;
import com.robam.steam.device.SteamAbstractControl;
import com.robam.steam.device.SteamFactory;
import com.robam.steam.device.SteamMqttControl;

//非主入口调用入口
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_stream_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_stream_activity_main), navController);

    }

    @Override
    protected void initData() {
        //使用哪个平台
        SteamFactory.initPlat(this, SteamFactory.TUOBANG);
        //开启远程控制
        SteamAbstractControl.getInstance().init(new SteamMqttControl());
        //协议解析和打包
        SteamFactory.initMqttProtocol(SteamFactory.CQ926);
    }
}
