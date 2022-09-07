package com.robam.stove.ui.activity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DateUtil;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.Iterator;
import java.util.Map;

//曲线创作
public class CurveCreateActivity extends StoveBaseActivity {
    private ImageView ivStop;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private TextView tvFire, tvTemp, tvTime;
    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        setOnClickListener(R.id.ll_left, R.id.iv_stop_create);
    }

    @Override
    protected void initData() {
        startCreate();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        //停止创建曲线
        if (id == R.id.ll_left || id == R.id.iv_stop_create) {
            stopCook();
        }
    }

    //开始创建
    private void startCreate() {

        runnable = new Runnable() {

            @Override

            public void run() {

                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime));

                mHandler.postDelayed(runnable, 2000L);

            }

        };
        mHandler.post(runnable);
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
                if (v.getId() == R.id.tv_ok) {
                    //保存曲线
                    startActivity(CurveSaveActivity.class);
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}
