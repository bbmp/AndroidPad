package com.robam.dishwasher.ui.activity;

import android.view.View;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherWaringEnum;
import com.robam.dishwasher.device.HomeDishWasher;


public class WaringActivity extends DishWasherBaseActivity {

    private TextView titleTv,descTv;

    private int fromFlag = 0;

    public static final int FROM_VENTILATOR_FLAG = 1;//从烟机界面主动进入

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
                        if(fromFlag != 1){
                            startActivity(MainActivity.class);
                        }
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ll_left){
            if(fromFlag == FROM_VENTILATOR_FLAG){
                finish();
            }
        }
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
        fromFlag = getIntent().getIntExtra(ComnConstant.WARING_FROM,0);
        if(fromFlag == FROM_VENTILATOR_FLAG){
            showLeft();
            setOnClickListener(R.id.ll_left);
        }
        titleTv.setText(washerWaringEnum.getPromptTitleRes());
        descTv.setText(washerWaringEnum.getPromptContentRes());
    }

}