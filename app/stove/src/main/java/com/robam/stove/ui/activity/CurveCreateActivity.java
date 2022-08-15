package com.robam.stove.ui.activity;

import android.view.View;

import com.robam.common.ui.dialog.IDialog;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;

//曲线创作
public class CurveCreateActivity extends StoveBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        setOnClickListener(R.id.ll_left, R.id.btn_ok);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.btn_ok) {
            stopCook();
        }
    }

    //创作结束提示
    private void stopCook() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.stove_stop_creation_hint);
        iDialog.setCancelText(R.string.stove_cancel);
        iDialog.setOKText(R.string.stove_stop_cook);
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
