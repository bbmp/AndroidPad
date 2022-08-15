package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;

//曲线保存
public class CurveSaveActivity extends StoveBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();
        setOnClickListener(R.id.tv_back, R.id.tv_save, R.id.iv_edit_name);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_back) {
            //回到首页
            startActivity(MainActivity.class);
        } else if (id == R.id.tv_save) {
            //保存成功
            ToastUtils.showShort(this, R.string.stove_save_success);
        } else if (id == R.id.iv_edit_name) {
            //编辑曲线名字
            curveEidt();
        }
    }
    //曲线名称
    private void curveEidt() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, R.id.tv_cancel);
        iDialog.show();
        ClearEditText editText = iDialog.getRootView().findViewById(R.id.et_curve_name);
        //单独处理确认事件
        iDialog.getRootView().findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //校验输入是否为空
                if (TextUtils.isEmpty(editText.getText())) {
                    ToastUtils.showShort(CurveSaveActivity.this, R.string.stove_input_empty);
                    return;
                }
                iDialog.dismiss();
            }
        });
    }
}