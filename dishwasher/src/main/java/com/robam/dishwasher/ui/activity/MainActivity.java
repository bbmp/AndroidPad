package com.robam.dishwasher.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.dishwasher.R;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.dishwasher.device.DishWasherMqttControl;

//远程入口，供烟机调用
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_dishwasher_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_dishwasher_activity_main), navController);
    }

    @Override
    protected void initData() {
        //使用哪个平台
        DishWasherFactory.initPlat(this, DishWasherFactory.TUOBANG);
        //开启远程控制
        DishWasherAbstractControl.getInstance().init(new DishWasherMqttControl());
        //mqtt协议解析和打包
        DishWasherFactory.initMqttProtocol();
    }
}
