package com.robam.ventilator.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;

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
    }

    public void setCenter(int res) {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_center).setVisibility(View.GONE);
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        TextView textView = findViewById(R.id.tv_center);
        textView.setVisibility(View.VISIBLE);
        textView.setText(res);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}
