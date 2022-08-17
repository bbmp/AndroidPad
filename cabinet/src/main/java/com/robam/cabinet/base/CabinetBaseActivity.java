package com.robam.cabinet.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.robam.cabinet.R;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.common.ui.activity.BaseActivity;

public abstract class CabinetBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CabinetActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CabinetActivityManager.getInstance().removeActivity(this);
    }
}
