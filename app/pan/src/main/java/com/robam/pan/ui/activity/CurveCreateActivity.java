package com.robam.pan.ui.activity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DateUtil;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.factory.PanDialogFactory;

//曲线创作中
public class CurveCreateActivity extends PanBaseActivity {
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private TextView tvFire, tvTemp, tvTime;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

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
        int id = view.getId();
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
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_creation_hint);
        iDialog.setOKText(R.string.pan_stop_creation);
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