package com.robam.cabinet.ui.activity;

import android.view.View;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetWaringEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;

public class WaringActivity extends CabinetBaseActivity {

    private TextView titleTv,descTv,phoneTv;

    public int fromFlag = 0;

    public static final int FROM_VENTILATOR_FLAG = 1;//从烟机界面主动进入

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_waring;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        phoneTv = findViewById(R.id.waring_phone);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                    Cabinet cabinet = (Cabinet) device;
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_WARING:
                            break;
                        default:
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
        fromFlag = getIntent().getIntExtra(ComnConstant.WARING_FROM,0);
        if(fromFlag == FROM_VENTILATOR_FLAG){
            showLeft();
            setOnClickListener(R.id.ll_left);
        }
        int waringCode = getIntent().getIntExtra(Constant.WARING_CODE,-1);
        if(waringCode == -1){
            return;
        }
        CabinetWaringEnum washerWaringEnum = CabinetWaringEnum.match(waringCode);
        if(washerWaringEnum.getCode() == CabinetWaringEnum.E255.getCode()){
            return;
        }
        if(washerWaringEnum.getCode() == CabinetWaringEnum.E0.getCode()){
            phoneTv.setVisibility(View.INVISIBLE);
        }
        titleTv.setText(washerWaringEnum.getPromptTitleRes());
        descTv.setText(washerWaringEnum.getPromptContentRes());
    }

}