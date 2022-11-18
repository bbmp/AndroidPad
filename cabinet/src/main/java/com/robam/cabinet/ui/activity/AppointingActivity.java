package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.constant.EventConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.cabinet.util.CabinetAppointmentUtil;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.TimeUtils;

import java.util.Map;

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

    WorkModeBean workModeBean = null;

    //指令标记偏移量
    public int directive_offset = 1400000;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_appointing;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAppointmentHint = findViewById(R.id.tv_appointment_hint);
        tvMode = findViewById(R.id.tv_mode);
        tvWorkHours = findViewById(R.id.tv_time);

        setOnClickListener(R.id.ll_left, R.id.iv_start);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
//                    if(toWaringPage(cabinet.alarmStatus)){
//                        return;
//                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                            updateAppointingView(cabinet);
                            break;
                        //case CabinetConstant.APPOINTMENT:
                        case CabinetConstant.OFF:
                        case CabinetConstant.ON:
                            goHome();
                            break;
                    }

                }
            }
        });
        MqttDirective.getInstance().getDirective().observe(this, s->{
            if(s != EventConstant.WARING_CODE_NONE){
                showWaring(s);
            }
        });
    }


    @Override
    protected void initData() {
       // setCountDownTime();
        workModeBean = (WorkModeBean) getIntent().getSerializableExtra(Constant.EXTRA_MODE_BEAN);
        //工作模式
        tvMode.setText(CabinetEnum.match(workModeBean.code));
        int orderTime= workModeBean.orderSurplusTime;
        //工作时长
        tvWorkHours.setText(getSpan(workModeBean.modelSurplusTime*60));
        int totalTime =orderTime * 60;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.setText(CabinetAppointmentUtil.getTimeStr(orderTime));
        tvAppointmentHint.setText(CabinetAppointmentUtil.startTimePoint(orderTime));
    }

    private void updateAppointingView(Cabinet cabinet){
        if(cabinet.remainingAppointTime <= 0){
            WorkModeBean workModeBean = new WorkModeBean(cabinet.workMode,0,cabinet.remainingModeWorkTime);
            Intent intent = new Intent(this,WorkActivity.class);
            intent.putExtra(Constant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
        }else{
            int totalTime = cabinet.remainingAppointTime * 60;
            tvCountdown.setTotalTime(totalTime);
            tvCountdown.setText(CabinetAppointmentUtil.getTimeStr(cabinet.remainingAppointTime));
            tvAppointmentHint.setText(CabinetAppointmentUtil.startTimePoint(cabinet.remainingAppointTime));
        }

    }


    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeCabinet.getInstance().orderTime + "";
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
            showEndDialog();
        } else if (id == R.id.iv_start) {
            showEndDialog();
        }
    }

//    private void startWork(){
//        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
//        map.put(CabinetConstant.SteriStatus, cabModeBean.code);
//        map.put(CabinetConstant.SteriTime, cabModeBean.defTime);
//        //CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
//    }

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

    /**
     * 展示结束预约弹窗
     */
    private void showEndDialog(){
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_STOP);
        iDialog.setCancelable(false);
        iDialog.setListeners(v -> {
            //结束工作
            if (v.getId() == R.id.tv_ok) {
                Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
                map.put(CabinetConstant.CABINET_STATUS, 0);
                map.put(CabinetConstant.CABINET_TIME, 0);
                map.put(CabinetConstant.ArgumentNumber,0);
                CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}