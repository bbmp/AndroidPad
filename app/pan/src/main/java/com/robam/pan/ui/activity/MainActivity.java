package com.robam.pan.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.robam.common.bean.AccountInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.device.HomePan;
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

        if (null != getIntent()) {
            HomePan.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
            AccountInfo.getInstance().getGuid().setValue(HomePan.getInstance().guid);
        }
        LogUtils.e("Home pan " + HomePan.getInstance().guid);
    }
}