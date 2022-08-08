package com.robam.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.robam.cabinet.R;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.CabinetFactory;
import com.robam.cabinet.device.CabinetMqttControl;
import com.robam.common.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_cabinet_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_cabinet_activity_main), navController);
    }

    @Override
    protected void initData() {
        //使用哪个平台
        CabinetFactory.initPlat(this, CabinetFactory.TUOBANG);
        //开启远程控制
        CabinetAbstractControl.getInstance().init(new CabinetMqttControl());
        //mqtt协议解析和打包
        CabinetFactory.initMqttProtocol();
    }
}