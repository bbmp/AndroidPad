package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;
import com.robam.dishwasher.util.SkipUtils;

public class CompleteActivity extends DishWasherBaseActivity {
    CountDownTimer countDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_complete;
    }

    @Override
    protected void initView() {
        View completeView = findViewById(R.id.complete_finish);
        setOnClickListener(completeView);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.complete_finish) {
            goHome();
        }
    }

    @Override
    protected void initData() {
        countDownTimer = new CountDownTimer(1000 * 15, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dealResult();
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

    private void dealResult(){
        DishWasher curDevice = getCurDevice();
        boolean goOtherPage = false;
        if(curDevice != null){
            if(curDevice.workMode != 0){
                switch (curDevice.AppointmentSwitchStatus){
                    case DishWasherState.APPOINTMENT_OFF:
                        if(curDevice.powerStatus == DishWasherState.WAIT){//待机状态下，无工作模式
                            break;
                        }
                        if(curDevice.remainingWorkingTime == 0){//无剩余工作时间
                            break;
                        }
                        goOtherPage = true;
                        Intent intent = new Intent();
                        DishWasherModeBean newMode  = new DishWasherModeBean();
                        DishWasherModelUtil.initWorkingInfo(newMode,curDevice);
                        intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                        intent.setClass(this, WorkActivity.class);
                        startActivity(intent);
                        break;
                    case DishWasherState.APPOINTMENT_ON:
                        if(curDevice.AppointmentRemainingTime == 0){
                            break;
                        }
                        goOtherPage = true;
                        Intent appointingIntent = new Intent();
                        DishWasherModeBean curMode  = new DishWasherModeBean();
                        DishWasherModelUtil.initWorkingInfo(curMode,curDevice);
                        appointingIntent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, curMode);
                        appointingIntent.setClass(this, AppointingActivity.class);
                        startActivity(appointingIntent);
                        HomeDishWasher.getInstance().workHours = curMode.time;
                        HomeDishWasher.getInstance().orderWorkTime = curDevice.AppointmentRemainingTime;
                        break;
                }
            }
        }
        if(!goOtherPage){
            goHome();
        }

    }

}