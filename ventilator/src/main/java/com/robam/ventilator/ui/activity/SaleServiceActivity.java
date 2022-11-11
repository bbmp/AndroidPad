package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.BuildConfig;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.AppTypeRes;

public class SaleServiceActivity extends VentilatorBaseActivity {
    private TextView tvSysV;
    private TextView tvModelV;
    private TextView tvNewVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_sale_service;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        tvSysV = findViewById(R.id.tv_sys_v);
        tvModelV = findViewById(R.id.tv_model_v);
        tvNewVersion = findViewById(R.id.tv_newVersion);
        tvNewVersion.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvNewVersion.getPaint().setAntiAlias(true);//抗锯齿
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_saleservice);
        setOnClickListener(R.id.ll_left, R.id.tv_newVersion);
    }

    @Override
    protected void initData() {
        tvSysV.setText(BuildConfig.VERSION_NAME);
        tvModelV.setText(BuildConfig.MODEL);
        CloudHelper.checkAppVersion(this, "RKPAD", Plat.getPlatform().getDt(), AppTypeRes.class, new RetrofitCallback<AppTypeRes>() {
            @Override
            public void onSuccess(AppTypeRes appTypeRes) {
                if (null != appTypeRes && null != appTypeRes.ver.url) {
                    LogUtils.e(appTypeRes.ver.url);
                    String version = String.format(getString(R.string.ventilator_new_version), appTypeRes.ver.code + "");
                    tvNewVersion.setVisibility(View.VISIBLE);
                    tvNewVersion.setText(version);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
        else if (id == R.id.tv_newVersion) {
            //下载
        }
    }
}