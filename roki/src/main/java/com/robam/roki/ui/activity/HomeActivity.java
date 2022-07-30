package com.robam.roki.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.activity.BaseActivity;
import com.robam.roki.R;

public class HomeActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_home;
    }

    @Override
    protected void initView() {
        navController = Navigation.findNavController(this, R.id.nav_host_roki_activity_home);
        Navigation.setViewNavController(findViewById(R.id.nav_host_roki_activity_home), navController);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        if (null != navController) {
            //最后一个打开的片段后，将关闭应用程序
            if (navController.getCurrentBackStackEntry().getDestination().getId() == R.id.homepage) {
                moveTaskToBack(false);
                finishAfterTransition();
                return;
            }
        }
        super.onBackPressed();
    }

}