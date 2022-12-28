package com.robam.pan.ui.activity;

import android.view.MotionEvent;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanWarningEnum;
import com.robam.pan.device.HomePan;

public class WarningActivity extends PanBaseActivity {
    private TextView titleTv,descTv;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_warning;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (null != device.guid && device.guid.equals(s) && device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                    Pan pan = (Pan) device;
                    if (pan.sysytemStatus == PanConstant.WORK_4 || pan.sysytemStatus == PanConstant.WORK_5) {
                        warningInfo(pan);
                        break;
                    } else
                        finish();
                }
            }
        });
    }

    private void warningInfo(Pan pan) {
        int waringCode = -1;
        if (pan.sysytemStatus == PanConstant.WORK_4)
            waringCode = PanConstant.WORK_4;
        else if (pan.sysytemStatus == PanConstant.WORK_5)
            waringCode = PanConstant.WORK_5;
        PanWarningEnum panWarningEnum = PanWarningEnum.match(waringCode);
        if(panWarningEnum.getCode() == PanWarningEnum.E0.getCode()){
            finish();
            return;
        }

        titleTv.setText(panWarningEnum.getPromptTitleRes());
        descTv.setText(panWarningEnum.getPromptContentRes());
    }

    @Override
    protected void initData() {
        if (null != getIntent()) {
            HomePan.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        }
        for (Device device : AccountInfo.getInstance().deviceList) {
            if (null != device.guid && device.guid.equals(HomePan.getInstance().guid) && device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                Pan pan = (Pan) device;
                if (pan.sysytemStatus == PanConstant.WORK_4 || pan.sysytemStatus == PanConstant.WORK_5) {
                    warningInfo(pan);
                    return;
                }
            }
        }
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
