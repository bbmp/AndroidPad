package com.robam.roki.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.robam.common.activity.BaseActivity;
import com.robam.roki.R;

public class SaleServiceActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_saleservice;
    }

    @Override
    protected void initView() {
        ImageView ivBack = findViewById(R.id.img_back);
        setOnClickListener(R.id.img_back);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}