package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherAuxEnum;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.factory.DishWasherDialogFactory;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;
import com.robam.dishwasher.util.TimeDisplayUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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
    private TextView tvModeAux;//辅助模式
    //当前模式
    private DishWasherModeBean modeBean = null;

    private StringBuffer buffer = new StringBuffer();

    public int directive_offset = 30000;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_appointing;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        setLock(HomeDishWasher.getInstance().lock);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAppointmentHint = findViewById(R.id.tv_start_work_hint);
        tvMode = findViewById(R.id.tv_mode);
        tvModeAux = findViewById(R.id.tv_mode_aux);
        tvWorkHours = findViewById(R.id.tv_time);

        setOnClickListener(R.id.ll_left, R.id.iv_start);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) { //当前锅
                    DishWasher dishWasher = (DishWasher) device;
                    if(toWaringPage(dishWasher.abnormalAlarmStatus)){
                        return;
                    }
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WAIT:
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                            if(DishWasherCommandHelper.getInstance().isSafe()){
                                dealWasherWorkingState(dishWasher);
                            }
                            break;
                        case DishWasherState.OFF:
                            if(DishWasherCommandHelper.getInstance().isSafe()){
                                finish();
                            }
                            break;
                    }
                    break;
                }
            }
        });
        MqttDirective.getInstance().getDirective().observe(this,s->{
            switch (s.intValue() - directive_offset){
                case MsgKeys.setDishWasherPower:
                    startActivity(MainActivity.class);
                    finish();
                    break;
                case MsgKeys.setDishWasherWorkMode:
                    List<DishWasherModeBean> modeBeanList = FunctionManager.getFuntionList(getContext(), DishWasherModeBean.class,R.raw.dishwahser);
                    Intent intent = new Intent();
                    DishWasherModeBean dishWasherModeBean = DishWasherModelUtil.getDishWasher(modeBeanList,modeBean.code);
                    intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, dishWasherModeBean);
                    intent.setClass(AppointingActivity.this, WorkActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

        });
    }

    private void dealWasherWorkingState(DishWasher dishWasher){
        switch (dishWasher.AppointmentSwitchStatus){
            case DishWasherState.APPOINTMENT_OFF:
                List<DishWasherModeBean> modeBeanList = FunctionManager.getFuntionList(getContext(), DishWasherModeBean.class,R.raw.dishwahser);
                Intent intent = new Intent();
                DishWasherModeBean dishWasherModeBean = DishWasherModelUtil.getDishWasher(modeBeanList,dishWasher.workMode);
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, dishWasherModeBean);
                intent.setClass(this, WorkActivity.class);
                startActivity(intent);
                finish();
                break;
            case DishWasherState.APPOINTMENT_ON:
                setViewsContent(dishWasher);
                break;
        }
    }

    private void setViewsContent(DishWasher dishWasher){
        //工作剩余时间 dishWasher.DishWasherRemainingWorkingTime
        //工作时长 dishWasher.SetWorkTimeValue
        //剩余预约时间 AppointmentRemainingTime
        int totalTime = dishWasher.AppointmentRemainingTime * 60;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.setText(getTimeStr(dishWasher.AppointmentRemainingTime));
        tvAppointmentHint.setText(startTimePoint(dishWasher.AppointmentRemainingTime));
        tvModeAux.setText(DishWasherAuxEnum.match(dishWasher.auxMode));


    }

    private String  getTimeStr(int remainingTime){
        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        return (aHour <= 9 ? ("0"+aHour) : aHour) + ":" + (aHour_surplus <= 9 ? ("0"+aHour_surplus) : aHour_surplus);
    }

    private String startTimePoint(int remainingTime){
        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        int addHour = (min + aHour_surplus) / 60;
        int addHour_surplus = (min + aHour_surplus) % 60;

        int totalHour = hour + aHour + addHour;
        int totalMin = addHour_surplus;

        return "将在" + (totalHour <= 9 ? ("0"+totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0"+totalMin) : totalMin) +"启动工作";
    }


    @Override
    protected void initData() {
        if (null != getIntent())
            modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);
        if (null != modeBean) {
            setCountDownTime();
            //工作时长
            //tvWorkHours.setText(HomeDishWasher.getInstance().workHours + "min");
            //工作模式
            tvMode.setText(DishWasherEnum.match(modeBean.code));

            tvWorkHours.setText(getSpan( modeBean.time/60));
            int totalTime =HomeDishWasher.getInstance().orderWorkTime * 60;
            tvCountdown.setTotalTime(totalTime);
            tvCountdown.setText(getTimeStr(HomeDishWasher.getInstance().orderWorkTime));
            tvAppointmentHint.setText(startTimePoint(HomeDishWasher.getInstance().orderWorkTime));
        }

    }

    private void setCountDownTime(String appointTime) {
        int totalTime = Integer.parseInt(appointTime) * 60;
        tvCountdown.setTotalTime(totalTime);
    }

    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeDishWasher.getInstance().orderTime;
        if(orderTime == null || orderTime.trim().length() == 0){
            return;
        }
        int housGap = DateUtil.getHousGap(orderTime);
        int minGap = DateUtil.getMinGap(orderTime);
        int totalTime = housGap * 60 * 60 + minGap * 60;
        //SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(currentSecond -> {
            // SteamOven.getInstance().orderLeftTime = currentSecond;
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

    //开始工作
    private void toStartWork() {
//        CabinetAbstractControl.getInstance().startWork();
        Intent intent = new Intent();
        if (null != modeBean)
            intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, modeBean);
        intent.setClass(this, WorkActivity.class);
        startActivity(intent);
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
            //tvCountdown.stop();
            Map params = DishWasherCommandHelper.getModelMap(MsgKeys.setDishWasherWorkMode, modeBean.code,(short) 0,0);
            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(params,directive_offset+MsgKeys.setDishWasherWorkMode);//立即开始
        }
    }
    //取消预约
    private void cancelAppointment() {
        IDialog iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.dishwasher_cancel_appointment_hint);
        iDialog.setCancelText(R.string.dishwasher_cancel);
        iDialog.setOKText(R.string.dishwasher_cancel_appointment);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    //结束倒计时
                    /*tvCountdown.stop();
                    finish();*/
                    Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherPower);
                    map.put(DishWasherConstant.PowerMode,DishWasherState.OFF);
                    DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,MsgKeys.setDishWasherPower+directive_offset);                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

   /* private void cancelAppointment(){
        DiashWasherCommonDialog washerCommonDialog = new DiashWasherCommonDialog(this);
        washerCommonDialog.setContentText(R.string.dishwasher_cancel_appointment_hint);
        washerCommonDialog.setCancelText(R.string.dishwasher_cancel);
        washerCommonDialog.setOKText(R.string.dishwasher_cancel_appointment);
        washerCommonDialog.setListeners(v -> {
            washerCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                Map map = DishWasherCommonHelper.getCommonMap(MsgKeys.setDishWasherPower);
                map.put(DishWasherConstant.PowerMode,DishWasherState.OFF);
                DishWasherCommonHelper.sendCommonMsgForLiveData(map,MsgKeys.setDishWasherPower+directive_offset);
            }
        },R.id.tv_cancel, R.id.tv_ok);
        washerCommonDialog.show();

    }*/

    private SpannableString getSpan(int remainTime){
        String time = TimeUtils.secToHourMinUp(remainTime);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}