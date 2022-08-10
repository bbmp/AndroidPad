package com.robam.cabinet.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;

/**
 *  工作界面
 */
public class WorkActivity extends CabinetBaseActivity {
    /**
     * 倒计时
     */
    private MCountdownView tvCountdown;

    private TextView tvMode;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        tvMode = findViewById(R.id.tv_mode);
        tvCountdown = findViewById(R.id.tv_countdown);
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {
        //工作模式
        tvMode.setText(CabinetEnum.match(Cabinet.getInstance().workMode));
        //工作时长
        setCountDownTime();
    }
    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        int workHours = Cabinet.getInstance().workHours;

        int totalTime = workHours * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
            @Override
            public void onCountDown(int currentSecond) {
//                SteamOven.getInstance().orderLeftTime = currentSecond;
                String time = DateUtil.secForMatTime3(currentSecond);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCountdown.setText(time);
                        //工作完成
                        if (currentSecond <= 0)
                            workComplete();
                    }
                });
             }
        });
        tvCountdown.start();
    }

    private void workComplete() {
        //工作完成提示
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setContentText(CabinetEnum.match(Cabinet.getInstance().workMode) + "完成");
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束工作
                if (v.getId() == R.id.tv_ok) {
                    startActivity(MainActivity.class);
                }
            }
        }, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            workStop();
        }
    }

    private void workStop() {
        //工作结束提示
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_STOP);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束工作
                if (v.getId() == R.id.tv_ok) {
                    tvCountdown.stop();
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}