package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class SaleServiceActivity extends VentilatorBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_sale_service;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_saleservice);
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}