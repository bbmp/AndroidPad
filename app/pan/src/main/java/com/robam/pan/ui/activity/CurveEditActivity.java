package com.robam.pan.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.view.ClearEditText;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;

public class CurveEditActivity extends PanBaseActivity {
    private TextView tvCancel, tvOk;
    private ClearEditText editText;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_dialog_layout_curve_edit;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvCancel = findViewById(R.id.tv_cancel);
        tvOk = findViewById(R.id.tv_ok);
        editText = findViewById(R.id.et_curve_name);
        setOnClickListener(R.id.tv_cancel, R.id.tv_ok);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_cancel)
            finish();
        else if (id == R.id.tv_ok) {
            //保存名称
            finish();
        }
    }
}