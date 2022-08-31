package com.robam.pan.ui.activity;

import android.view.View;

import com.robam.common.ui.dialog.IDialog;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.factory.PanDialogFactory;

//曲线创作中
public class CurveCreateActivity extends PanBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        setOnClickListener(R.id.ll_left, R.id.btn_stop_create);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.btn_stop_create) {
            stopCook();
        }
    }

    //创作结束提示
    private void stopCook() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_creation_hint);
        iDialog.setOKText(R.string.pan_stop_creation);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束创作
                if (v.getId() == R.id.tv_cancel) {
                    //保存曲线
                    startActivity(CurveSaveActivity.class);
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}