package com.robam.dishwasher.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.dishwasher.R;

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

    }
}
