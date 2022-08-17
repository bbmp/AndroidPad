package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class PersonalCenterActivity extends VentilatorBaseActivity {
    private TextView tvLogin;

    private RecyclerView rvDevice, rvDot;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_personal_center;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvDevice = findViewById(R.id.rv_device);
        rvDot = findViewById(R.id.rv_dot);
        setOnClickListener(R.id.tv_login);

        rvDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login) {
            startActivity(LoginPhoneActivity.class);
        }
    }
}