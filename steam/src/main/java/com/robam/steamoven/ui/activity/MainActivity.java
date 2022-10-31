package com.robam.steamoven.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.constant.ComnConstant;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.device.HomeSteamOven;

//非主入口调用入口
public class MainActivity extends SteamBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_stream_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_stream_activity_main), navController);
        getContentView().postDelayed(()->{
            showRightCenter();
        }, Constant.TIME_DELAYED);
    }

    @Override
    protected void initData() {
        if (null != getIntent())
            HomeSteamOven.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        LogUtils.e("HomeSteamOven guid " + HomeSteamOven.getInstance().guid);

    }
}
