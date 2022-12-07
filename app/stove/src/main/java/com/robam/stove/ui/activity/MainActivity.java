package com.robam.stove.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.bean.AccountInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.stove.R;
import com.robam.stove.device.HomeStove;
import com.robam.stove.manager.StoveActivityManager;

//非主入口
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoveActivityManager.getInstance().addActivity(this);
    }

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

        if (null != getIntent()) {
            HomeStove.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
            AccountInfo.getInstance().getGuid().setValue(HomeStove.getInstance().guid);
        }
        LogUtils.e("HomeStove guid " + HomeStove.getInstance().guid);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StoveActivityManager.getInstance().removeActivity(this);
    }
}