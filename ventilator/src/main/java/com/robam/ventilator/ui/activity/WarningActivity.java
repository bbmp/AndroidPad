package com.robam.ventilator.ui.activity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import com.robam.common.bean.AccountInfo;
import com.robam.common.device.Plat;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.constant.VentilatorWarningEnum;
import com.robam.ventilator.device.HomeVentilator;

public class WarningActivity extends VentilatorBaseActivity {
    private TextView titleTv,descTv;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_warning;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            if (null != s && s.equals(Plat.getPlatform().getDeviceOnlySign())) {
                //判断烟机故障
                warningInfo();
            }

        });
    }

    @Override
    protected void initData() {
        warningInfo();

    }

    private void warningInfo() {
        int waringCode = -1;
        if ((HomeVentilator.getInstance().alarm & 0x01) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E1;
        else if (((HomeVentilator.getInstance().alarm & 0x02) >> 1) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E2;
        else if (((HomeVentilator.getInstance().alarm & 0x04) >> 2) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E3;
        else if (((HomeVentilator.getInstance().alarm & 0x08) >> 3) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E4;
        else if (((HomeVentilator.getInstance().alarm & 0x10) >> 4) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E5;
        else if (((HomeVentilator.getInstance().alarm & 0x20) >> 5) == 1)
            waringCode = VentilatorConstant.DEVICE_WARING_E6;
        VentilatorWarningEnum ventilatorWarningEnum = VentilatorWarningEnum.match(waringCode);
        if(ventilatorWarningEnum.getCode() == VentilatorWarningEnum.E0.getCode()){
            finish();
            return;
        }

        titleTv.setText(ventilatorWarningEnum.getPromptTitleRes());
        descTv.setText(ventilatorWarningEnum.getPromptContentRes());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
