package com.robam.cabinet.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;

/**
 * 预约中
 */
public class AppointingActivity extends CabinetBaseActivity {
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
        return R.layout.cabinet_activity_layout_appointing;
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
        tvWorkHours.setText(HomeCabinet.getInstance().workHours + "min");
        //工作模式
        tvMode.setText(CabinetEnum.match(HomeCabinet.getInstance().workMode));
    }

    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeCabinet.getInstance().orderTime;

        tvAppointmentHint.setText(String.format(getString(R.string.cabinet_work_order_hint1), orderTime ));
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

    private void toStartWork() {
//        CabinetAbstractControl.getInstance().startWork();
        startActivity(WorkActivity.class);
        finish();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            //结束倒计时
            tvCountdown.stop();
            finish();
        } else if (id == R.id.iv_start) {
            //立即开始
            tvCountdown.stop();
            finish();
            startActivity(WorkActivity.class);
        }
    }
}