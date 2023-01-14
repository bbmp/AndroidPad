package com.robam.steamoven.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.utils.SkipUtil;
import com.robam.steamoven.utils.SteamDataUtil;

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
        //getContentView().postDelayed(()-> showRightCenter(), Constant.TIME_DELAYED);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
//                    if(!SteamCommandHelper.getInstance().isSafe()){
//                        return;
//                    }
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            SkipUtil.toWorkPage(steamOven,MainActivity.this);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }


                }
            }
        });
    }

    @Override
    protected void initData() {
        if (null != getIntent()){
            HomeSteamOven.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
            SteamAbstractControl.getInstance().queryAttribute(HomeSteamOven.getInstance().guid);
        }
        //状态检查
        if(StringUtils.isBlank(HomeSteamOven.getInstance().guid)){
            ToastUtils.showLong(this,R.string.steam_guid_prompt);
            finish();
            return;
        }
        SteamOven steamOven = getSteamOven();
        if(steamOven != null){//获取设备数据，主要用于展示菜谱
            String deviceTypeId = DeviceUtils.getDeviceTypeId(steamOven.guid);
            String steamContent = SteamDataUtil.getSteamContent(deviceTypeId);
            if(StringUtils.isBlank(steamContent)){
                SteamDataUtil.getSteamData(this,deviceTypeId);
            }else{
                RecipeManager.getInstance().setRecipeData(steamContent);
            }
        }
        SteamDataUtil.getDeviceErrorInfo(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecipeManager.getInstance().setRecipeData(null);
    }
}
