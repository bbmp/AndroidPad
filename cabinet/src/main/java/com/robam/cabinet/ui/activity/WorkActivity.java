package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.cabinet.util.MqttSignal;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import java.util.Map;

/**
 *  工作界面
 */
public class WorkActivity extends CabinetBaseActivity {
    /**
     * 倒计时
     */
    private MCountdownView tvCountdown;

    private TextView tvMode;

    private ImageView ivStart;

    public int directive_offset = 1500000;

    /**
     * 是否工作结束
     */
    private boolean workFinish = false;

    private WorkModeBean workModeBean;
    private int workModeCode;
    private static int FINISH_DIALOG_MIN_TIME = 3 * 60 * 1000;
    private MqttSignal mqttSignal;



    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        tvMode = findViewById(R.id.tv_mode);
        tvCountdown = findViewById(R.id.tv_countdown);
        ivStart = findViewById(R.id.iv_start);
        setOnClickListener(R.id.ll_left, R.id.iv_start);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(!CabinetCommonHelper.isSafe()){
                        return;
                    }
                    if(cabinet.isChildLock == 1){
                        if(finishDialog != null && finishDialog.isShow()){
                            finishDialog.dismiss();
                        }
                    }
                    if(toWaringPage(cabinet.faultId)){
                        return;
                    }
                    if(toOffLinePage(cabinet)){
                        return;
                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                        case CabinetConstant.FUN_WARING:
                            if(finishDialog != null && finishDialog.isShow() &&
                                    System.currentTimeMillis() - showFinishTimeMil >= FINISH_DIALOG_MIN_TIME){
                                dismissFinishDialog();
                            }
                            if(cabinet.remainingModeWorkTime > 0){
                                if(cabinet.workMode != 0){
                                    workModeCode = cabinet.workMode;
                                }
                                tvMode.setText(CabinetEnum.match(cabinet.workMode));
                                updateWorkTime(cabinet.remainingModeWorkTime);
                            }else{
                                toComplete(workModeCode);
                            }
                            break;
                        default:
                            goHome();
                            /*if(!workFinish){
                                goHome();
                            }*/
                            break;
                    }

                    break;
                }
            }
        });
    }




    @Override
    protected void initData() {
        //工作模式
        workModeBean = (WorkModeBean) getIntent().getSerializableExtra(Constant.EXTRA_MODE_BEAN);
        tvMode.setText(CabinetEnum.match(workModeBean.code));
        //工作时长
        updateWorkTime(workModeBean.modelSurplusTime);
        mqttSignal = new MqttSignal();
        mqttSignal.startLoop();
        //setCountDownTime();
    }

    private void toComplete(int modeCode){
        Intent intent = new Intent(this,CompleteActivity.class);
        intent.putExtra(Constant.MODE_CODE,modeCode);
        startActivity(intent);
    }

    /**
     * 更新界面剩余工作时长
     * @param remainingTime
     */
    private void updateWorkTime(int remainingTime){
        //String time = TimeUtils.secToHourMinUp(remainingTime);
        String time =secToMinUp(remainingTime);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCountdown.setText(spannableString);
    }

    /**
     * 秒转分
     * @param seconds
     * @return
     */
    private  String secToMinUp(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        if(sec != 0){
            min += 1;
        }
        if (min > 0)
            return   min + "min";
        else
            return "0min";
    }
    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        int workHours = HomeCabinet.getInstance().workHours;

        int totalTime = workHours * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
            @Override
            public void onCountDown(int currentSecond) {
//                SteamOven.getInstance().orderLeftTime = currentSecond;
                String time = DateUtil.secForMatTime3(currentSecond);

                tvCountdown.setText(time);
                //工作完成
                if (currentSecond <= 0)
                    workComplete();

             }
        });
        tvCountdown.start();
    }

    IDialog iDialog;
    private void workComplete() {
        //工作完成提示
        iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setContentText(CabinetEnum.match(workModeBean.code) + "完成");
        iDialog.setListeners(v -> {
            //结束工作
            if (v.getId() == R.id.tv_ok) {
                Cabinet cabinet = getCabinet();
                if(cabinet != null){
                    boolean isSmart = false;
                    //MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(cabinet.guid);
                    if(cabinet.smartCruising == 1 || cabinet.pureCruising == 1){
                        isSmart = true;
                    }
                   /* else if(workState != null && workState.isFinish() && workState.workModel == CabinetEnum.SMART.getCode()){
                        isSmart = true;
                    }*/
                    if(isSmart){
                        Intent intent = new Intent(WorkActivity.this,CruiseActivity.class);
                        int model = cabinet.smartCruising == 1 ? CabinetEnum.SMART.getCode() : CabinetEnum.FLUSH.getCode();
                        intent.putExtra(Constant.SMART_MODEL,model);
                        startActivity(intent);
                        return;
                    }
                }
                goHome();
            }
        }, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            workStop();
        } else if (id == R.id.iv_start) {
            workStop();
            //showEndDialog();
        }
    }

    private IDialog finishDialog;
    private long showFinishTimeMil;
    /**
     * 展示主动结束弹窗
     */
    private void workStop() {
        //工作结束提示
        showFinishTimeMil = System.currentTimeMillis();
        finishDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_STOP);
        finishDialog.setCancelable(false);
        finishDialog.setListeners(v -> {
            //结束工作
            if (v.getId() == R.id.tv_ok) {
                Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
                map.put(CabinetConstant.CABINET_STATUS, 0);
                map.put(CabinetConstant.CABINET_TIME, 0);
                map.put(CabinetConstant.ArgumentNumber,0);
                //CabinetCommonHelper.sendCommonMsg(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
                CabinetCommonHelper.sendCommonMsg(map);
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        finishDialog.show();
    }


    /**
     * 工作结束弹窗
     */
    private void workFinish(){
        IDialog iDialog =  CabinetDialogFactory.createDialogByType(this,DialogConstant.DIALOG_TYPE_WORK_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setListeners(v -> {
            Cabinet cabinet = getCabinet();
            if(cabinet != null){
                MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(cabinet.guid);
                if(workState != null && workState.isFinish() && workState.workModel == CabinetEnum.SMART.getCode()){
                    Intent intent = new Intent(WorkActivity.this,CruiseActivity.class);
                    startActivity(intent);
                    return;
                }
            }
            goHome();
        }, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqttSignal.pageShow();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mqttSignal.pageHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       this.dismissFinishDialog();
       mqttSignal.clear();
    }


    private void dismissFinishDialog(){
        if(finishDialog != null && finishDialog.isShow()){
            finishDialog.dismiss();
            finishDialog = null;
        }
    }





}