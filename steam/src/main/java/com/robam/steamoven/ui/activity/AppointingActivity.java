package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.device.HomeSteamOven;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约中
 */
public class AppointingActivity extends SteamBaseActivity {
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

    private MultiSegment segment;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_appointing;
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
        tvWorkHours.setText(HomeSteamOven.getInstance().workHours + "min");
        //工作模式
        tvMode.setText(SteamModeEnum.match(HomeSteamOven.getInstance().workMode));

        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
    }


    private void initModelView(){

    }

    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeSteamOven.getInstance().orderTime;

        tvAppointmentHint.setText(String.format(getString(R.string.steam_work_order_hint1), orderTime ));
        int housGap = DateUtil.getHousGap(orderTime);
        int minGap = DateUtil.getMinGap(orderTime);
        int totalTime = housGap * 60 * 60 + minGap * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(currentSecond -> {
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
            Intent intent = new Intent(this,ModelWorkActivity.class);
            List<MultiSegment> list = new ArrayList<>();
            list.add(segment);
            list.get(0).setWorkModel(1);
            intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
            startActivity(intent);
        }
    }
}