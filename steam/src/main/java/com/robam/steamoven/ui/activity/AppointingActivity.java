package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DensityUtil;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.utils.SkipUtil;

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
    //蒸汽模式
    private TextView tvSteam;
    //工作时长
    private TextView tvWorkHours;

    private MultiSegment segment;

    private TextView defTemp;

    private int directive_offset = 15000000;
    private final static int DIRECTIVE_OFFSET_END = 40;
    int tempPaddingTop;

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
        defTemp = findViewById(R.id.tv_temp);
        tvSteam = findViewById(R.id.tv_steam);
        setOnClickListener(R.id.ll_left, R.id.iv_start);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    if(toWaringPage(steamOven)){
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
                                //toWorkPage();
                                SkipUtil.toWorkPage(steamOven,AppointingActivity.this);
                            }else{
                                goHome();
                            }
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            goHome();
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
        tempPaddingTop = (int) getResources().getDimension(com.robam.common.R.dimen.dp_44);
        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        tvMode.setText(SteamModeEnum.match(segment.code));
        if(segment.code == SteamConstant.EXP){
            tvSteam.setVisibility(View.VISIBLE);
            tvSteam.setText(getSpanTemp(segment.defTemp+""));
            defTemp.setPadding(defTemp.getPaddingLeft(),tempPaddingTop,defTemp.getPaddingRight(),defTemp.getPaddingBottom());
            defTemp.setText(getSpanTemp(segment.downTemp+""));
        }else{
            tvSteam.setVisibility(segment.steam != 0 ? View.VISIBLE:View.GONE);
            if(segment.steam != 0){
                defTemp.setPadding(defTemp.getPaddingLeft(),tempPaddingTop,defTemp.getPaddingRight(),defTemp.getPaddingBottom());
                tvSteam.setText(SteamOvenSteamEnum.match(segment.steam)+"蒸汽");
            }
            defTemp.setText(getSpanTemp(segment.defTemp+""));
        }
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
        SteamCommandHelper.getInstance().sendCommonMsg(commonMap);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        LogUtils.i("AppointingActivity onClick ...");
        int id = view.getId();
        if (id == R.id.ll_left) {
            //结束倒计时
            showFinishAppointDialog();
        } else if (id == R.id.iv_start) {
            if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),segment.code)){
                return;
            }
            startWork();
        }
    }

    /**
     * 显示结束预约Dialog
     */
    private void showFinishAppointDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_end_appoint);
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


    private void startWork(){
        MultiSegment result = segment;
        if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),result.code)){
            return;
        }
        if(SteamModeEnum.EXP.getMode() == result.code){
            SteamCommandHelper.sendCommandForExp(result,null,0,MsgKeys.setDeviceAttribute_Req+directive_offset);
        }else{
            if(SteamModeEnum.isAuxModel(result.code)){
                SteamCommandHelper.startModelWork(result,null,0);
            }else{
                SteamCommandHelper.startModelWork(result,null,0);
            }
        }
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