package com.robam.ventilator.base;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.ClickUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.ui.activity.DateSettingActivity;

public abstract class VentilatorBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListener(R.id.ll_left);
    }

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

    public void setCenter(int res) {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_center).setVisibility(View.GONE);
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        TextView textView = findViewById(R.id.tv_center);
        textView.setVisibility(View.VISIBLE);
        textView.setText(res);
    }

    public void setRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right2).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right2).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(R.string.ventilator_skip);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}
