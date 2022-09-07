package com.robam.pan.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.factory.PanDialogFactory;

//曲线保存
public class CurveSaveActivity extends PanBaseActivity {
    private TextView tvBack, tvSave;
    //曲线名字
    private TextView tvCurveName;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();

        tvCurveName = findViewById(R.id.tv_curve_name);
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
            ToastUtils.showShort(this, R.string.pan_save_success);
        } else if (id == R.id.iv_edit_name) {
            //编辑曲线名字
            curveEidt();
        }
    }

    private void curveEidt() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
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
                    ToastUtils.showShort(CurveSaveActivity.this, R.string.pan_input_empty);
                    return;
                }
                tvCurveName.setText(editText.getText());
                iDialog.dismiss();
            }
        });
    }
}