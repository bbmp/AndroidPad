package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.UiAutomation;
import android.os.Bundle;
import android.view.View;

import com.robam.common.ui.dialog.IDialog;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;

public class ResetActivity extends VentilatorBaseActivity {
    private IDialog resetDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_reset;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_reset);

        setOnClickListener(R.id.tv_reset);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_reset)
            resetDialog();
    }
    //恢复确认
    private void resetDialog() {
        if (null == resetDialog) {
            resetDialog = VentilatorDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
            resetDialog.setCancelable(false);
            resetDialog.setContentText(R.string.ventilator_reset_ok);
            resetDialog.setOKText(R.string.ventilator_reset);
            resetDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok)
                        ; //恢复中
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        resetDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != resetDialog && resetDialog.isShow())
            resetDialog.dismiss();
    }
}