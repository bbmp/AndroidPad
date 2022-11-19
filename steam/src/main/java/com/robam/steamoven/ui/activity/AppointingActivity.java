package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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

    private TextView defTemp;

    private int directive_offset = 15000000;
    private final static int DIRECTIVE_OFFSET_END = 40;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_appointing;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        //showRightCenter();
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAppointmentHint = findViewById(R.id.tv_appointment_hint);
        tvMode = findViewById(R.id.tv_mode);
        tvWorkHours = findViewById(R.id.tv_time);
        defTemp = findViewById(R.id.tv_temp);
        setOnClickListener(R.id.ll_left, R.id.iv_start);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            if(steamOven.workState ==  SteamStateConstant.WORK_STATE_APPOINTMENT){
                                updateViewInfo(steamOven);
                            }else if(steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT ||
                                    steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE  ||
                                    steamOven.workState == SteamStateConstant.WORK_STATE_WORKING ||
                                    steamOven.workState == SteamStateConstant.WORK_STATE_WORKING ){
                                toWorkPage();
                            }
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            finish();
                            break;
                    }
                }
            }
        });

//        MqttDirective.getInstance().getDirective().observe(this, s -> {
//            switch (s - directive_offset){
//                case MsgKeys.setDeviceAttribute_Req:
//                    toWorkPage();
//                    break;
//                case DIRECTIVE_OFFSET_END:
//                    goHome();
//                    break;
//            }
//        });
    }

    private void toWorkPage(){
        //立即开始
        Intent intent = new Intent(this,ModelWorkActivity.class);
        List<MultiSegment> list = new ArrayList<>();
        list.add(segment);
        list.get(0).setWorkModel(MultiSegment.WORK_MODEL_);
        list.get(0).setCookState(MultiSegment.COOK_STATE_START);
        intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
        startActivity(intent);
        finish();
    }

    private void updateViewInfo(SteamOven steamOven){
        int outTime = steamOven.orderLeftTime;
        tvCountdown.setTotalTime(outTime);
        tvCountdown.setText(getTimeStr(outTime));
        tvAppointmentHint.setText(startTimePoint(outTime));
    }

    @Override
    protected void initData() {
        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        //setCountDownTime();
        tvMode.setText(SteamModeEnum.match(segment.code));
        defTemp.setText(getSpanTemp(segment.defTemp+""));
        tvWorkHours.setText(getSpan(segment.duration*60));
        int totalTime =segment.workRemaining * 60;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.setText(getTimeStr(segment.workRemaining));
        tvAppointmentHint.setText(startTimePoint(segment.workRemaining));
    }

    /**
     * 结束工作
     */
    private void endWork(){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        //SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + DIRECTIVE_OFFSET_END);
        SteamCommandHelper.getInstance().sendCommonMsg(commonMap,directive_offset + DIRECTIVE_OFFSET_END);
    }


    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeSteamOven.getInstance().orderTime +"";

        tvAppointmentHint.setText(String.format(getString(R.string.steam_work_order_hint1), orderTime ));
        int housGap = DateUtil.getHousGap(orderTime);
        int minGap = DateUtil.getMinGap(orderTime);
        int totalTime = housGap * 60 * 60 + minGap * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(currentSecond -> {
            //SteamOven.getInstance().orderLeftTime = currentSecond;
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
            showFinishAppointDialog();
        } else if (id == R.id.iv_start) {
            if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),segment.code)){
                return;
            }
            startWork(segment.code,segment.defTemp,segment.duration,segment.steam);
        }
    }

    /**
     * 显示结束预约Dialog
     */
    private void showFinishAppointDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                //endWork();
                SteamCommandHelper.sendEndWorkCommand(77);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    /**
     *
     * @param mode  模式code
     * @param setTemp 运行温度
     * @param setTime  运行时间
     * @param steamFlow 蒸汽量code
     */
    private void startWork(int mode,int setTemp,int setTime,int steamFlow){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        if (steamFlow == 0){
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 7);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }
        }else {
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 9);
            }
        }
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_0) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);

        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, 1);

        //预约时间
        commonMap.put(SteamConstant.setOrderMinutesKey, 5);
        commonMap.put(SteamConstant.setOrderMinutesLength, 1);
        commonMap.put(SteamConstant.setOrderMinutes01, 0);


        //commonMap.put(SteamConstant.setOrderMinutes, orderTime);

        //段数
        commonMap.put(SteamConstant.sectionNumberKey, 100) ;
        commonMap.put(SteamConstant.sectionNumberLength, 1) ;
        commonMap.put(SteamConstant.sectionNumber, 1) ;

        commonMap.put(SteamConstant.rotateSwitchKey, 9) ;
        commonMap.put(SteamConstant.rotateSwitchLength, 1) ;
        commonMap.put(SteamConstant.rotateSwitch, 0) ;
        //模式
        commonMap.put(SteamConstant.modeKey, 101) ;
        commonMap.put(SteamConstant.modeLength, 1) ;
        commonMap.put(SteamConstant.mode, mode) ;
        //温度上温度

        if (setTemp!=0) {
            commonMap.put(SteamConstant.setUpTempKey, 102);
            commonMap.put(SteamConstant.setUpTempLength, 1);
            commonMap.put(SteamConstant.setUpTemp, setTemp);
        }
        //时间
        setTime*=60;
        commonMap.put(SteamConstant.setTimeKey, 104);
        commonMap.put(SteamConstant.setTimeLength, 1);

        final short lowTime = setTime > 255 ? (short) (setTime & 0Xff):(short)setTime;
        if (setTime<=255){
            commonMap.put(SteamConstant.setTime0b, lowTime);
        }else{
            commonMap.put(SteamConstant.setTimeKey, 104);
            commonMap.put(SteamConstant.setTimeLength, 2);
            short time = (short)(setTime & 0xff);
            commonMap.put(SteamConstant.setTime0b, time);
            short highTime = (short) ((setTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.setTime1b, highTime);
        }

        if (steamFlow!=0) {
            //蒸汽量
            commonMap.put(SteamConstant.steamKey, 106);
            commonMap.put(SteamConstant.steamLength, 1);
            commonMap.put(SteamConstant.steam, steamFlow);
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,MsgKeys.setDeviceAttribute_Req+directive_offset);
    }

    /**
     *
     * @param remainingTime 剩余预约时间，单位 - 秒
     * @return
     */
    private String  getTimeStr(int remainingTime){
        int hour = remainingTime / 3600;
        int min = (remainingTime - hour * 3600)/60;
        int second = remainingTime%60;
        if(second != 0){//在remainingTime代表的时间满足xx:59:yy秒的情况时，second是yy不等于0，min =59;在此时对min进行 + 1;将导致 min = 60
            min += 1;
        }
        if(min == 60){
            hour +=1;
            min = 0;
        }
        return (hour <= 9 ? ("0"+hour) : hour) + ":" + (min <= 9 ? ("0"+min) : min);
    }

    /**
     *
     * @param remainingAppointTime  剩余预约时间，单位 - 秒
     * @return
     */
    private String startTimePoint(int remainingAppointTime){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.SECOND,remainingAppointTime);
        int totalHour = calendar.get(Calendar.HOUR_OF_DAY);
        int totalMin = calendar.get(Calendar.MINUTE);
        int totalDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (day != totalDay) {
            return "将在次日" + (totalHour <= 9 ? ("0" + totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0" + totalMin) : totalMin) + "启动工作";
        } else {
            return "将在" + (totalHour <= 9 ? ("0" + totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0" + totalMin) : totalMin) + "启动工作";
        }
    }

    private SpannableString getSpanTemp(String temp){
        SpannableString spannableString = new SpannableString(temp+Constant.UNIT_TEMP);
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        spannableString.setSpan(new RelativeSizeSpan(0.5f), temp.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(superscriptSpan, temp.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

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