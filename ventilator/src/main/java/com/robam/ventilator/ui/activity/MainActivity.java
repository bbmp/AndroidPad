package com.robam.ventilator.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.activity.BaseActivity;
import com.robam.ventilator.R;

//非主入口调用入口
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_ventilator_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_ventilator_activity_main), navController);
    }

    @Override
    protected void initData() {

    }
}
