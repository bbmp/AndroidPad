package com.robam.steamoven.ui.activity;

import android.view.View;
import android.widget.TextView;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.DeviceErrorInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.manager.DeviceWarnInfoManager;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.device.HomeSteamOven;

public class WaringActivity extends SteamBaseActivity {

    private TextView titleTv,descTv;
    public int fromFlag = 0;
    private String deviceGuid = "";

    public static final int FROM_VENTILATOR_FLAG = 1;//从烟机界面主动进入

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
                    if(steamOven.faultId == 0){
                        finish();
                    }else{
                        //缺水需要处理
                        if(steamOven.faultId == Constant.WARING_CODE_11 && steamOven.getResidueTotalTime() <= 0){
                            finish();
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        int waringCode = getIntent().getIntExtra(ComnConstant.WARING_CODE,0);
        fromFlag = getIntent().getIntExtra(ComnConstant.WARING_FROM,0);
        deviceGuid = getIntent().getStringExtra(ComnConstant.WARING_GUID);
        if(fromFlag == FROM_VENTILATOR_FLAG){
            showLeft();
            setOnClickListener(R.id.ll_left);
        }
        if(waringCode == 0){
            return;
        }
        DeviceErrorInfo deviceErrorInfo;
        if(StringUtils.isNotBlank(deviceGuid)){
             deviceErrorInfo = DeviceWarnInfoManager.getInstance().getDeviceErrorInfo(IDeviceType.RZKY, DeviceUtils.getDeviceTypeId(deviceGuid), waringCode);
        }else{
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                return;
            }
            deviceErrorInfo = DeviceWarnInfoManager.getInstance().getDeviceErrorInfo(IDeviceType.RZKY, DeviceUtils.getDeviceTypeId(steamOven.guid), waringCode);
        }
        if(deviceErrorInfo == null){
            return;
        }
        titleTv.setText(deviceErrorInfo.alertName+"");
        descTv.setText(deviceErrorInfo.alertDescr+"");
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ll_left){
            if(fromFlag == FROM_VENTILATOR_FLAG){
                finish();
            }
        }
    }
}