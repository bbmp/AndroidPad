package com.robam.pan.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.device.PanBluetoothControl;
import com.robam.pan.device.PanFactory;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_pan_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_pan_activity_main), navController);
    }

    @Override
    protected void initData() {
        //开启远程控制, 蓝牙控制
        PanAbstractControl.getInstance().init(new PanBluetoothControl());

    }
}