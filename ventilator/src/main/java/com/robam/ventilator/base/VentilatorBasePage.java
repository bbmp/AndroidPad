package com.robam.ventilator.base;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.HeadPage;
import com.robam.common.utils.ClickUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.ui.activity.DateSettingActivity;

public abstract class VentilatorBasePage extends HeadPage {

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
        ClickUtils.setLongClick(new Handler(), findViewById(R.id.ll_center), 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!AccountInfo.getInstance().getConnect().getValue()) { //未联网
                    //时间设置
                    Intent intent = new Intent();

                    intent.setClass(getContext(), DateSettingActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
