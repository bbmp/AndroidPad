package com.robam.steamoven.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.constant.ComnConstant;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamFactory;
import com.robam.steamoven.device.SteamMqttControl;

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
        if (null != getIntent())
            HomeSteamOven.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        LogUtils.e("HomeSteamOven guid " + HomeSteamOven.getInstance().guid);
        //开启远程控制
        SteamAbstractControl.getInstance().init(new SteamMqttControl());

    }
}
