package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.factory.PanDialogFactory;

//曲线还原,
public class CurveRestoreActivity extends PanBaseActivity {
    private RecyclerView rvStep;
    private TextView tvStop;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvStep = findViewById(R.id.rv_step);
        tvStop = findViewById(R.id.tv_stop_cook);
        setOnClickListener(R.id.ll_left, R.id.tv_stop_cook);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.tv_stop_cook) {
            //停止烹饪
            stopCook();
        }
    }
    //停止烹饪提示
    private void stopCook() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_creation_hint);
        iDialog.setCancelText(R.string.pan_stop_creation);
        iDialog.setOKText(R.string.pan_continue_creation);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束创作
                if (v.getId() == R.id.tv_cancel) {
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}