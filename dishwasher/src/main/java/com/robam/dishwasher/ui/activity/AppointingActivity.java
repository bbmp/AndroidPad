package com.robam.dishwasher.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherEnum;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.dishwasher.factory.DishWasherDialogFactory;

/**
 * 预约中
 */
public class AppointingActivity extends DishWasherBaseActivity {
    /**
     * 倒计时
     */
    private MCountdownView tvCountdown;
    //启动提示
    private TextView tvAppointmentHint;
    //工作模式
    private TextView tvMode;
    //工作时长
    private TextView tvWorkHours;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_appointing;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAppointmentHint = findViewById(R.id.tv_appointment_hint);
        tvMode = findViewById(R.id.tv_mode);
        tvWorkHours = findViewById(R.id.tv_time);

        setOnClickListener(R.id.ll_left, R.id.iv_start);
    }

    @Override
    protected void initData() {
        setCountDownTime();
        //工作时长
        tvWorkHours.setText(DishWasher.getInstance().workHours + "min");
        //工作模式
        tvMode.setText(DishWasherEnum.match(DishWasher.getInstance().workMode));

    }

    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = DishWasher.getInstance().orderTime;

        int housGap = DateUtil.getHousGap(orderTime);
        int minGap = DateUtil.getMinGap(orderTime);
        int totalTime = housGap * 60 * 60 + minGap * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
            @Override
            public void onCountDown(int currentSecond) {
//                SteamOven.getInstance().orderLeftTime = currentSecond;
                String time = DateUtil.secForMatTime2(currentSecond);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCountdown.setText(time);
                        if (currentSecond <= 0)
                            toStartWork();
                    }
                });
            }
        });
        tvCountdown.start();
    }

    //开始工作
    private void toStartWork() {
//        CabinetAbstractControl.getInstance().startWork();
        startActivity(WorkActivity.class);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            //取消预约提示
            cancelAppointment();
        } else if (id == R.id.iv_start) {
            //立即开始
            tvCountdown.stop();
            finish();
            startActivity(WorkActivity.class);
        }
    }
    //取消预约
    private void cancelAppointment() {
        IDialog iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.dishwasher_cancel_appointment_hint);
        iDialog.setCancelText(R.string.dishwasher_cancel);
        iDialog.setOKText(R.string.dishwasher_ok);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    //结束倒计时
                    tvCountdown.stop();
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}