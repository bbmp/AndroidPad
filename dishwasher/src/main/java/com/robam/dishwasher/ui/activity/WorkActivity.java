package com.robam.dishwasher.ui.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
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
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.factory.DishWasherDialogFactory;
import com.robam.dishwasher.ui.dialog.DiashWasherCommonDialog;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.TimeDisplayUtil;

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

    private View startIcon,pauseIcon;
    //当前模式 - 注意该对象可能未空（比如APP异常退出，在进入，比如在洗碗机中直接直接进行操作）
    private DishWasherModeBean modeBean = null;

    private int preRemainingTime;

    //是否已经提醒过
    private boolean isReminding = false;
    //是否不再提醒
    private boolean isNoLongerRemind = false;

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
        tvMode = findViewById(R.id.tv_mode);
        tvModeCur = findViewById(R.id.tv_mode_cur);
        tvAuxMode = findViewById(R.id.tv_aux_mode);
        startIcon = findViewById(R.id.iv_start);
        pauseIcon = findViewById(R.id.iv_pause);

        cpgBar.setProgress(85);
        setOnClickListener(R.id.ll_left, R.id.iv_float,R.id.iv_start,R.id.iv_pause);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) { //当前锅
                    DishWasher dishWasher = (DishWasher) device;
                    LogUtils.e("WorkActivity mqtt msg arrive isWorking "+dishWasher.powerStatus + " " +dishWasher.abnormalAlarmStatus);
                    if(toWaringPage(dishWasher.abnormalAlarmStatus)){
                        return;
                    }
                    if(!DishWasherCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(isWorkingFinish(dishWasher)){
                        toComplete();
                        return;
                    }
                    setLock(dishWasher.StoveLock == 1);
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                        case DishWasherState.END:
                            setWorkingState(dishWasher);
                            break;
                        case DishWasherState.OFF:
                            startActivity(MainActivity.class);
                            finish();
                            break;
                    }
                    break;
                }
            }
        });

        MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s.shortValue()){
                case  DishWasherState.OFF:
                    startActivity(MainActivity.class);
                    finish();
                    break;
                case DishWasherState.WORKING:
                    tvTime.setText(getSpan(preRemainingTime));
                    changeViewsState(DishWasherState.WORKING);
                    break;
                case DishWasherState.PAUSE:
                    changeViewsState(DishWasherState.PAUSE);
                    tvDuration.setText(getSpan(preRemainingTime));
                    break;
                case DishWasherState.END:
                    break;
            }
        });
    }

    private int sum = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            sum += 1;
            float progress = sum * 100f / 60;
            cpgBar.setProgress(progress);
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    @Override
    protected void initData() {
        //当前模式
        if (null != getIntent())
            modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);

        if (null != modeBean) {
            setData(modeBean);
            preRemainingTime = modeBean.time;
        }
    }

    //模式参数设置
    private void setData(DishWasherModeBean modeBean) {
        tvMode.setText(modeBean.name);
        tvTime.setText(getSpan(modeBean.time));
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
        sendCommand(isStart);
        //changeViewsState(isStart?DishWasherConstant.WORKING:DishWasherConstant.PAUSE);
    }

    private void sendCommand(boolean isStart){
        Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherPower);
        if(isStart){//回复运行
            map.put(DishWasherConstant.PowerMode,DishWasherState.WORKING);
            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,DishWasherState.WORKING);
        }else{//暂停
            map.put(DishWasherConstant.PowerMode,DishWasherState.PAUSE);
            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,DishWasherState.PAUSE);
        }
        //setWorkingState(dishWasher);

    }


    //停止工作提示
    private void stopWork() {
        IDialog iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
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
        //handler.removeCallbacksAndMessages(null);
    }

    private void work() {
        //1、检测洗碗机门 是否关闭 absDishWasher.DoorOpenState == (short) 1 若门没有关闭，则提示

        //2、先切换洗碗机状态至开机

        //3、切换成功后，在设置洗碗机工作模式
        //DishWasherAbstractControl.getInstance().sendCommonMsg(getModelWorkParamMsg(), HomeDishWasher.getInstance().guid);

    }


    //工作中 - 需更新时间
    private void setWorkingState(DishWasher dishWasher){
        changeViewsState(dishWasher.powerStatus);
        if(dishWasher.powerStatus == DishWasherState.WORKING){
            //工作剩余时间 dishWasher.DishWasherRemainingWorkingTime
            //工作时长 dishWasher.SetWorkTimeValue
            preRemainingTime = dishWasher.DishWasherRemainingWorkingTime *60;
            tvTime.setText(getSpan(dishWasher.DishWasherRemainingWorkingTime*60));
        }else if(dishWasher.powerStatus == DishWasherState.PAUSE){
            tvDuration.setText(getSpan(dishWasher.DishWasherRemainingWorkingTime*60));
        }
        tvAuxMode.setText(DishWasherAuxEnum.match(dishWasher.auxMode));//附加模式
        showRemindDialog(dishWasher);
    }



    private void changeViewsState(int state){
        if(state == DishWasherState.PAUSE){
            startIcon.setVisibility(View.INVISIBLE);
            pauseIcon.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.VISIBLE);
            tvTime.setText(R.string.dishwasher_pausing);
        }else if(state == DishWasherState.WORKING){
            startIcon.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.INVISIBLE);
            pauseIcon.setVisibility(View.INVISIBLE);
        }else if(state == DishWasherState.END){

        }
    }

    private SpannableString getSpan(int remainTimeSec){
        String time = TimeUtils.secToHourMinUp(remainTimeSec);
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
        if((dishWasher.LackRinseStatus == 0 && dishWasher.LackSaltStatus == 0 ) || (isReminding || isNoLongerRemind)){
            if(commonDialog != null &&  commonDialog.isShow()){
                commonDialog.dismiss();
                commonDialog = null;
            }
            return;
        }
        isReminding = true;
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("缺少");
        if(dishWasher.LackRinseStatus == 1){
            sBuffer.append("漂洗剂");
        }
        if(dishWasher.LackSaltStatus == 1){
            if(sBuffer.length() > "缺少".length()){
                sBuffer.append("、专用盐");
            }
        }
        commonDialog = new DiashWasherCommonDialog(this);
        commonDialog.setContentText(sBuffer.toString());
        commonDialog.setCancelText(R.string.dishwasher_no_longer_remind);
        commonDialog.setOKText(R.string.dishwasher_ok);
        commonDialog.setListeners(v->{
            isReminding = false;
            if(v.getId() == R.id.tv_cancel){
                isNoLongerRemind = true;
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
    }



}