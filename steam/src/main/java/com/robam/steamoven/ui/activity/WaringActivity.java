package com.robam.steamoven.ui.activity;

import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.DeviceErrorInfo;
import com.robam.common.manager.DeviceWarnInfoManager;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
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
                    SteamOven steamOven = (SteamOven) device;
                    if(steamOven.faultCode == 0){
                        //finish();
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        int waringCode = getIntent().getIntExtra(SteamConstant.WARING_CODE,0);
        SteamOven steamOven = getSteamOven();
        if(waringCode == 0 || steamOven == null){
            return;
        }

        DeviceErrorInfo deviceErrorInfo = DeviceWarnInfoManager.getInstance().getDeviceErrorInfo(IDeviceType.RZKY, DeviceUtils.getDeviceTypeId(steamOven.guid), waringCode);
        if(deviceErrorInfo == null){
            return;
        }
        titleTv.setText(deviceErrorInfo.alertName+"");
        descTv.setText(deviceErrorInfo.alertDescr+"");
    }

}