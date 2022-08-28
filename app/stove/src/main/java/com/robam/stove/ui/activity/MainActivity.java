package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveBluetoothControl;
import com.robam.stove.device.StoveFactory;

//非主入口
public class MainActivity extends StoveBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_stove_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_stove_activity_main), navController);
    }

    @Override
    protected void initData() {
        //使用哪个平台
        StoveFactory.initPlat(this, StoveFactory.TUOBANG);
        //开启远程控制, 蓝牙控制
        StoveAbstractControl.getInstance().init(new StoveBluetoothControl());
        //mqtt协议解析和打包
        StoveFactory.initMqttProtocol();
    }
}