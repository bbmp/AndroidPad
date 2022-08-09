package com.robam.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;

public class WorkActivity extends CabinetBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
    }

    @Override
    protected void initData() {

    }
}