package com.robam.pan.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.ui.HeadPage;
import com.robam.pan.R;

public abstract class PanBasePage extends HeadPage {

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }
    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        ImageView ivWifi = findViewById(R.id.iv_center);
        //监听网络连接状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ivWifi.setVisibility(View.VISIBLE);
                else
                    ivWifi.setVisibility(View.GONE);
            }
        });
    }

    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
        ImageView ivBattery = findViewById(R.id.iv_right_center);
        TextView tvBattery = findViewById(R.id.tv_right_center);
        tvBattery.setText(R.string.pan_battery);
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                Pan pan = (Pan) device;
                //电量
                if (pan.battery < 20)
                    ivBattery.setImageResource(R.drawable.pan_battery_20);
                else if (pan.battery < 40)
                    ivBattery.setImageResource(R.drawable.pan_battery_40);
                else if (pan.battery < 60)
                    ivBattery.setImageResource(R.drawable.pan_battery_60);
                else if (pan.battery < 80)
                    ivBattery.setImageResource(R.drawable.pan_battery_80);
                else
                    ivBattery.setImageResource(R.drawable.pan_battery_100);
                break;
            }
        }
    }
}
