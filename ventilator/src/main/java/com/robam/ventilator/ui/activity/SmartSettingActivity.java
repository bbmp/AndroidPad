package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.SwitchButton;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;

public class SmartSettingActivity extends VentilatorBaseActivity {
    private Button btnReset;

    private SwitchButton sbAir, sbOil;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_smart_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_smart_setting);

        btnReset = findViewById(R.id.bt_reset);
        //自动换气
        sbAir = findViewById(R.id.sb_auto_air);
        //油网提醒
        sbOil = findViewById(R.id.sb_auto_oil);
        setOnClickListener(R.id.ll_left, R.id.bt_reset);
        sbAir.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton button, boolean checked) {

            }
        });
        sbOil.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton button, boolean checked) {

            }
        });
    }

    @Override
    protected void initData() {

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
        else if (id == R.id.bt_reset) {
            //恢复初始提示
            resetDialog();
        }
    }
    //恢复初始
    private void resetDialog() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_smart_reset_hint);
        iDialog.setOKText(R.string.ventilator_reset_start);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok)
                    ;
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}