package com.robam.dishwasher.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.CircleProgressView;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.factory.DishWasherDialogFactory;

public class WorkActivity extends DishWasherBaseActivity {
    /**
     * 进度条
     */
    private CircleProgressView cpgBar;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_work;
    }

    @Override
    protected void initView() {
        //显示快捷图标
        showFloat();
        showLeft();
        showCenter();
        cpgBar = findViewById(R.id.progress);
        cpgBar.setProgress(0);
handler.sendEmptyMessageDelayed(0, 1000);
        setOnClickListener(R.id.ll_left, R.id.iv_float);
    }
    private int sum = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            sum += 1;
            cpgBar.setProgress(sum);
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            //工作结束提示
            stopWork();
        } else if (id == R.id.iv_start) {
            //暂停，开始
        }

    }
    //停止工作提示
    private void stopWork() {
        IDialog iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}