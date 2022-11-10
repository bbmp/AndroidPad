package com.robam.steamoven.ui.activity;

import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;

public class WaringActivity extends SteamBaseActivity {

    private TextView titleTv,descTv;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_waring;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven dishWasher = (SteamOven) device;
//                    if(dishWasher.abnormalAlarmStatus == DishWasherWaringEnum.E0.getCode()){
//                        startActivity(MainActivity.class);
//                        finish();
//                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        int waringCode = getIntent().getIntExtra(SteamConstant.WARING_CODE,0);
        if(waringCode == 0){
            return;
        }
//        DishWasherWaringEnum washerWaringEnum = DishWasherWaringEnum.match(waringCode);
//        if(washerWaringEnum.getCode() == 0){
//            return;
//        }
//        titleTv.setText(washerWaringEnum.getPromptTitleRes());
//        descTv.setText(washerWaringEnum.getPromptContentRes());
    }

}