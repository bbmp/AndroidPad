package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.CircleProgressView;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.constant.DishWasherAuxEnum;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DishWasherEvent;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.factory.DishWasherDialogFactory;
import com.robam.dishwasher.ui.dialog.DiashWasherCommonDialog;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;

import java.util.List;
import java.util.Map;

public class WorkActivity extends DishWasherBaseActivity {
    /**
     * 进度条
     */
    private CircleProgressView cpgBar;

    private TextView tvModeCur;//洗涤、漂洗、干燥、换气
    private TextView tvTime;//时间
    private TextView tvDuration;//时间
    private TextView tvMode;//模式
    private TextView tvAuxMode;//附加模式 - 锅具强洗、加强除菌、长效净存、下层洗

    private TextView tvAirMode;
    private TextView tvAriTime;
    private View progressBg;
    private ImageView ivPxj;// 缺漂洗剂
    private ImageView ivZyy;// 缺专用盐

    private View startIcon,pauseIcon;
    //当前模式 - 注意该对象可能未空
    private DishWasherModeBean modeBean = null;

    private int preRemainingTime;

    //是否已经提醒过
    private boolean isReminding = false;
    //是否不再提醒
    private boolean isNoLongerRemind = false;

    public static final int MAX_PROGRESS = 97;


    //private Group huanqiGroup,progressGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        setLock(HomeDishWasher.getInstance().lock);
        cpgBar = findViewById(R.id.progress);
        tvTime = findViewById(R.id.tv_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvModeCur = findViewById(R.id.tv_mode_cur);
        tvMode = findViewById(R.id.tv_mode);
        tvAuxMode = findViewById(R.id.tv_aux_mode);
        startIcon = findViewById(R.id.iv_start);
        pauseIcon = findViewById(R.id.iv_pause);
        tvAirMode = findViewById(R.id.tv_duration_air);
        tvAriTime = findViewById(R.id.tv_time_air);
        progressBg = findViewById(R.id.prgress_bg);
        cpgBar.setProgress(MAX_PROGRESS);
        ivPxj = findViewById(R.id.tv_pxj);
        ivZyy = findViewById(R.id.tv_zyy);
        setOnClickListener(R.id.ll_left, R.id.iv_float,R.id.iv_start,R.id.iv_pause);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            LogUtils.i("WorkActivity AccountInfo arrive");
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) { //当前锅
                    DishWasher dishWasher = (DishWasher) device;
                    if(toWaringPage(dishWasher.abnormalAlarmStatus)){
                        return;
                    }
                    if(toOffLinePage(dishWasher)){
                        return;
                    }
                    setLock(dishWasher.StoveLock == 1);
                    setState(dishWasher.LackSaltStatus == 1,dishWasher.LackRinseStatus == 1);
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                        case DishWasherState.END:
                        //case DishWasherState.WAIT:
                            setWorkingState(dishWasher);
                            break;
                        case DishWasherState.OFF:
                        case DishWasherState.WAIT:
                            goHome();
                            break;
                    }
                    break;
                }
            }
        });

        MqttDirective.getInstance().getStrLiveData().observe(this,s->{
            if(s == null || s.trim().length() == 0){
                return ;
            }
            String[] split = s.split(MqttDirective.STR_LIVE_DATA_FLAG);
            if(split == null || split.length != 2){
                return;
            }
            if (split[0].equals(HomeDishWasher.getInstance().guid)) {
                int code = Integer.parseInt(split[1]);
                if(code == DishWasherEvent.EVENT_WORK_COMPLETE_RESET){
                    DishWasher curDevice = getCurDevice();
                    if(curDevice.auxMode != 0 && curDevice.auxMode != DishWasherConstant.AUX_FLUSH){//附加模式是长效净存，不跳转到工作完成
                        toComplete();
                    }
                }
            }
        });

        /*MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s.shortValue()){
                case DishWasherEvent.EVENT_WORK_COMPLETE_RESET:
                    toComplete();
                    break;
                case DishWasherState.END:
                    break;
            }
        });*/
    }

    @Override
    protected void initData() {
        //当前模式
        if (null != getIntent())
            modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);

        if (null != modeBean) {
            setData(modeBean);
            setModelTextState(modeBean.code);
            DishWasher curDevice = getCurDevice();
            if(curDevice != null){
                changeViewsState(curDevice.powerStatus);
                setModelTextState(curDevice.workMode);
            }
            preRemainingTime = modeBean.time;
            float progress;
            if(modeBean.time == 0){
                progress = MAX_PROGRESS;
            }else{
                progress = modeBean.restTime * 1f / modeBean.time * 100;
            }
            if(progress > MAX_PROGRESS){
                progress = MAX_PROGRESS;
            }
            cpgBar.setProgress(progress);
        }
    }

    private void setModelTextState(int code){
        if(DishWasherEnum.FLUSH.getCode() == code ||
                DishWasherEnum.AUTO_AERATION.getCode() == code ||
                DishWasherEnum.LONG_STORAGE.getCode() == code){
            tvAirMode.setVisibility(View.INVISIBLE);
            tvAriTime.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            progressBg.setVisibility(View.VISIBLE);
            tvMode.setVisibility(View.VISIBLE);
            tvMode.setText(DishWasherEnum.match(code));
            cpgBar.setVisibility(View.VISIBLE);
            tvModeCur.setVisibility(View.VISIBLE);
            tvModeCur.setText(R.string.dishwasher_aeration);

        }else if(DishWasherEnum.AUTO_AERATION_AWAIT.getCode() == code ||
                DishWasherEnum.FLUSH_AWAIT.getCode() == code ||
                DishWasherEnum.LONG_STORAGE_AWAIT.getCode() == code){//换气等待
            tvAirMode.setText(DishWasherEnum.match(code));
            tvAirMode.setVisibility(View.VISIBLE);
            tvAriTime.setVisibility(View.VISIBLE);
            tvMode.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.INVISIBLE);
            progressBg.setVisibility(View.INVISIBLE);
            cpgBar.setVisibility(View.INVISIBLE);
            tvModeCur.setVisibility(View.INVISIBLE);
        }else{
            tvAirMode.setVisibility(View.INVISIBLE);
            tvAriTime.setVisibility(View.INVISIBLE);
            tvMode.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            progressBg.setVisibility(View.VISIBLE);
            tvMode.setText(DishWasherEnum.match(code));
            cpgBar.setVisibility(View.VISIBLE);
            tvModeCur.setVisibility(View.VISIBLE);
            tvModeCur.setText(R.string.dishwasher_working);
        }
    }


    //模式参数设置
    private void setData(DishWasherModeBean modeBean) {
        tvMode.setText(modeBean.name);
        tvAuxMode.setText(DishWasherAuxEnum.match(modeBean.auxCode));
        tvTime.setText(getSpan(modeBean.time));
        if(DishWasherConstant.MODE_FLUSH == modeBean.code){
            tvModeCur.setText(R.string.dishwasher_aeration);
        }else{
            tvModeCur.setText(R.string.dishwasher_washer);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            //工作结束提示
            stopWork();
        } else if (id == R.id.iv_start) {
            //进入暂停
            stateViewWasClick(false);
        }else if(id == R.id.iv_pause){
            //回复运行
            stateViewWasClick(true);
        }
    }

    private void stateViewWasClick(boolean isStart){
        DishWasher curDevice = getCurDevice();
        if(!DishWasherCommandHelper.checkDishWasherState(this,curDevice)){
            getLastState();
            return;
        }
        DishWasherCommandHelper.sendCtrlWorkCommand(isStart);
        //changeViewsState(isStart?DishWasherState.WORKING:DishWasherState.PAUSE);
    }



    IDialog iDialog;
    //停止工作提示
    private void stopWork() {
        if(iDialog != null && iDialog.isShow()){
            return;
        }
        iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
        iDialog.setCancelable(false);
        iDialog.setListeners(v -> {
            iDialog.dismiss();
            if (v.getId() == R.id.tv_ok) {
                Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherPower);
                map.put(DishWasherConstant.PowerMode,DishWasherState.OFF);
                DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,DishWasherState.OFF);
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(commonDialog != null && commonDialog.isShow()){
            commonDialog.dismiss();
        }
        if(iDialog != null && iDialog.isShow()){
            iDialog.dismiss();
        }
    }

    //工作中 - 需更新时间
    private void setWorkingState(DishWasher dishWasher){
        changeViewsState(dishWasher.powerStatus);
        setModelTextState(dishWasher.workMode);
        if(dishWasher.workMode >= DishWasherConstant.MODE_FLUSH){
            tvAuxMode.setVisibility(View.INVISIBLE);
        }else{
            tvAuxMode.setVisibility(View.VISIBLE);
            tvAuxMode.setText(DishWasherAuxEnum.match(dishWasher.auxMode));//附加模式
        }
        if(dishWasher.powerStatus == DishWasherState.WORKING){
            preRemainingTime = dishWasher.remainingWorkingTime *60;
            tvTime.setText(getSpan(preRemainingTime));
        }else if(dishWasher.powerStatus == DishWasherState.PAUSE){
            tvDuration.setText(getSpan(dishWasher.remainingWorkingTime*60));
        }
        if(DishWasherEnum.AUTO_AERATION_AWAIT.getCode() == dishWasher.workMode ||
                DishWasherEnum.FLUSH_AWAIT.getCode() == dishWasher.workMode ||
                DishWasherEnum.LONG_STORAGE_AWAIT.getCode() == dishWasher.workMode){//自动换气
            if(dishWasher.powerStatus == DishWasherState.WORKING){
                tvAriTime.setText(getSpan(preRemainingTime));
            }
            return;
        }
        float progress = MAX_PROGRESS;
        if(dishWasher.workMode == DishWasherConstant.MODE_LONG_STORAGE ||
                dishWasher.workMode == DishWasherConstant.MODE_AUTO_AERATION){//护婴净存/自动换气
            //获取分钟数
            int residueTimeM = dishWasher.remainingWorkingTime % 60;
            progress = residueTimeM / 60f * 100;
        }else if(dishWasher.workMode == DishWasherConstant.MODE_LONG_STORAGE_AWAIT){
            return;
        }else{
            if(dishWasher.SetWorkTimeValue != 0){
                progress =  (dishWasher.remainingWorkingTime*1f/dishWasher.SetWorkTimeValue) * 100;
            }
        }
        if(progress > MAX_PROGRESS){
            progress = MAX_PROGRESS;
        }
        cpgBar.setProgress(progress);
        if(dishWasher.workMode != DishWasherConstant.MODE_FLUSH){
            showRemindDialog(dishWasher);
        }
    }



    private void changeViewsState(int state){
        if(state == DishWasherState.PAUSE){
            startIcon.setVisibility(View.INVISIBLE);
            pauseIcon.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.VISIBLE);
            tvTime.setText(R.string.dishwasher_pausing);
            tvAriTime.setText(R.string.dishwasher_pausing);
        }else if(state == DishWasherState.WORKING){
            startIcon.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.INVISIBLE);
            pauseIcon.setVisibility(View.INVISIBLE);
        }else if(state == DishWasherState.END){

        }
    }

    private SpannableString getSpan(int remainTimeSec){
        String time = TimeUtils.secToHourMinUp(remainTimeSec);
        if(remainTimeSec >= 60*60*10){
            int minIndex = time.indexOf("min");
            int hourIndex = time.indexOf("h");
            if(minIndex != 0 && hourIndex != 0){
                try{
                    //time = (Integer.parseInt(time.substring(0,hourIndex)) + 1) +"h";//暂时不加1
                    time = time.substring(0,hourIndex)+"h";//暂时不加1
                }catch (NumberFormatException e){}
            }else{
                try{
                    time = time.substring(0,hourIndex) +"h";
                }catch (NumberFormatException e){}
            }
        }
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    private DiashWasherCommonDialog commonDialog = null;
    /**
     * 显示提醒弹窗（缺少洗涤剂与专用盐）
     */
    private void showRemindDialog(DishWasher dishWasher){
        if(dishWasher.LackRinseStatus == 0 && dishWasher.LackSaltStatus == 0){
            if(commonDialog != null &&  commonDialog.isShow()){
                commonDialog.dismiss();
                commonDialog = null;
            }
            return;
        }
        if((isReminding || isNoLongerRemind || HomeDishWasher.getInstance().isNoLongerRemind)){
            return;
        }
        isReminding = true;
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("缺少");
        if(dishWasher.LackRinseStatus == 1){
            sBuffer.append("漂洗剂");
            //ivPxj.setVisibility(View.VISIBLE);
        }
        if(dishWasher.LackSaltStatus == 1){
            if(sBuffer.length() > "缺少".length()){
                sBuffer.append("、专用盐");
                //ivZyy.setVisibility(View.VISIBLE);
            }
        }
        commonDialog = new DiashWasherCommonDialog(this);
        commonDialog.setContentText(sBuffer.toString());
        commonDialog.setCancelText(R.string.dishwasher_no_longer_remind);
        commonDialog.setOKText(R.string.dishwasher_ok);
        commonDialog.setListeners(v->{
            isReminding = false;
            isNoLongerRemind = true;
            if(v.getId() == R.id.tv_cancel){
                HomeDishWasher.getInstance().isNoLongerRemind = true;
            }
        },R.id.tv_cancel,R.id.tv_ok);
        commonDialog.show();
    }

    private boolean isWorkingFinish(DishWasher washer){
        return (washer.powerConsumption != DishWasherConstant.ZERO && washer.waterConsumption != DishWasherConstant.ZERO);
    }


    private void toComplete(){
        Intent intent = new Intent(this,CompleteActivity.class);
        intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN,modeBean);
        startActivity(intent);
        finish();
    }








}