package com.robam.ventilator.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.BuildConfig;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutActivity extends VentilatorBaseActivity {
    private TextView tvSysV;
    private TextView tvModelV;
    private int COUNT = 5;
    private long DURATION = 3* 1000;
    private long[] mHits = new long[COUNT];
    private long[] mSerial = new long[COUNT];

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_about;
    }

    @Override
    protected void initView() {

        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        tvSysV = findViewById(R.id.tv_sys_v);
        tvModelV = findViewById(R.id.tv_model_v);
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_product);
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {
//        tvSysV.setText(BuildConfig.VERSION_NAME);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(Build.TIME);
        tvSysV.setText(simpleDateFormat.format(date));
        tvModelV.setText(BuildConfig.MODEL);
        tvModelV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mSerial, 1, mSerial, 0, mSerial.length - 1);
                mSerial[mSerial.length - 1] = System.currentTimeMillis();
                if (mSerial[0] >= (System.currentTimeMillis() - DURATION)) {
                    long[] hits = new long[COUNT];
                    System.arraycopy(hits, 0, mSerial, 0, mSerial.length);
                    ToastUtils.showLong(getApplicationContext(), DeviceUtils.getDeviceSerial());
                }
            }
        });
        tvSysV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = System.currentTimeMillis();
                if (mHits[0] >= (System.currentTimeMillis() - DURATION)) {
                    long[] hits = new long[COUNT];
                    System.arraycopy(hits, 0, mHits, 0, mHits.length);
                    goHome();
                }
            }
        });
    }

    private void goHome() {
        ComponentName componetName = new ComponentName(
//这个是另外一个应用程序的包名
                "com.android.launcher3",
//这个参数是要启动的Activity
                "com.android.launcher3.uioverrides.QuickstepLauncher");

        Intent intent = new Intent();
        intent.setComponent(componetName);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}