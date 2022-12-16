package com.robam.dishwasher.ui.activity;

import android.os.CountDownTimer;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;

public class CompleteActivity extends DishWasherBaseActivity {
    CountDownTimer countDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_complete;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initData() {
        countDownTimer = new CountDownTimer(1000 * 15, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                goHome();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}