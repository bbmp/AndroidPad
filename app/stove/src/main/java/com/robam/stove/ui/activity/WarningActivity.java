package com.robam.stove.ui.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.stove.R;
import com.robam.stove.constant.StoveWarningEnum;
import com.robam.stove.device.HomeStove;
import com.robam.stove.manager.StoveActivityManager;

public class WarningActivity extends BaseActivity { //锁屏不处理
    private TextView titleTv,descTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoveActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_warning;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.waring_title);
        descTv = findViewById(R.id.waring_desc);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (null != device.guid && device.guid.equals(s) && device instanceof Stove && IDeviceType.RRQZ.equals(device.dc)) {
                    Stove stove = (Stove) device;
                    if (stove.leftAlarm != 0xff || stove.rightAlarm != 0xff) {
                        warningInfo(stove);
                        break;
                    } else
                        finish();
                }
            }
        });
    }

    @Override
    protected void initData() {
        if (null != getIntent()) {
            HomeStove.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        }
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (null != device.guid && device.guid.equals(HomeStove.getInstance().guid) && device instanceof Stove && IDeviceType.RRQZ.equals(device.dc)) {
                Stove stove = (Stove) device;
                if (stove.leftAlarm != 0xff || stove.rightAlarm != 0xff) {
                    warningInfo(stove);
                    return;
                }
            }
        }
        finish();
    }

    private void warningInfo(Stove stove) {
        int waringCode = -1;
        if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E1 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E1)
            waringCode = StoveConstant.DEVICE_WARING_E1;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E2 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E2)
            waringCode = StoveConstant.DEVICE_WARING_E2;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E3 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E3)
            waringCode = StoveConstant.DEVICE_WARING_E3;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E4 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E4)
            waringCode = StoveConstant.DEVICE_WARING_E4;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E5 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E5)
            waringCode = StoveConstant.DEVICE_WARING_E5;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E6 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E6)
            waringCode = StoveConstant.DEVICE_WARING_E6;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E7 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E7)
            waringCode = StoveConstant.DEVICE_WARING_E7;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E8 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E8)
            waringCode = StoveConstant.DEVICE_WARING_E8;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E9 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E9)
            waringCode = StoveConstant.DEVICE_WARING_E9;
        else if (stove.leftAlarm == StoveConstant.DEVICE_WARING_E10 || stove.rightAlarm == StoveConstant.DEVICE_WARING_E10)
            waringCode = StoveConstant.DEVICE_WARING_E10;
        StoveWarningEnum stoveWarningEnum = StoveWarningEnum.match(waringCode);
        if(stoveWarningEnum.getCode() == StoveWarningEnum.E255.getCode()){
            finish();
            return;
        }

        titleTv.setText(stoveWarningEnum.getPromptTitleRes());
        descTv.setText(stoveWarningEnum.getPromptContentRes());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StoveActivityManager.getInstance().removeActivity(this);
    }
}
