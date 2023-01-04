package com.robam.steamoven.ui.activity;

import android.view.View;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;

public class RemindActivity extends SteamBaseActivity {

    private TextView titleTv;
    private int remainBusCode;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_remind;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.remind_title);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    int remindResId = SteamCommandHelper.getRemindResId(steamOven);
                    if(remindResId == 0){
                        finish();
                    }else{
                        if(remindResId != remainBusCode){
                            titleTv.setText(remainBusCode);
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        remainBusCode = getIntent().getIntExtra(Constant.REMIND_BUS_CODE,0);
        if(remainBusCode != 0){
            titleTv.setText(remainBusCode);
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ll_left){

        }
    }
}