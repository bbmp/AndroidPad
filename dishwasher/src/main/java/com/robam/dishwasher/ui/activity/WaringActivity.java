package com.robam.dishwasher.ui.activity;

import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherWaringEnum;
import com.robam.dishwasher.device.HomeDishWasher;


public class WaringActivity extends DishWasherBaseActivity {

    private TextView titleTv,descTv;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_waring;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                    DishWasher dishWasher = (DishWasher) device;
                    if(dishWasher.abnormalAlarmStatus == DishWasherWaringEnum.E0.getCode()){
                        startActivity(MainActivity.class);
                        finish();
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        int waringCode = getIntent().getIntExtra(DishWasherConstant.WARING_CODE,0);
        if(waringCode == 0){
            return;
        }
        DishWasherWaringEnum washerWaringEnum = DishWasherWaringEnum.match(waringCode);
        if(washerWaringEnum.getCode() == 0){
            return;
        }
        titleTv.setText(washerWaringEnum.getPromptTitleRes());
        descTv.setText(washerWaringEnum.getPromptContentRes());
    }

}