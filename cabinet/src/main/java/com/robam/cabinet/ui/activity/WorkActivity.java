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
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.factory.CabinetDialogFactory;
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
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                }
            }
        });
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(cabinet.remainingModeWorkTime > 0){
                        updateWorkTime(cabinet.remainingModeWorkTime);
                    }else{
                        if(!workFinish){
                            workFinish = true;
                            workFinish();
                        }
                    }
                    break;
                }
            }
        });

        MqttDirective.getInstance().getDirective().observe(this, s->{

            if(s == (directive_offset + MsgKeys.SetSteriPowerOnOff_Req)){
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        //工作模式
        tvMode.setText(CabinetEnum.match(HomeCabinet.getInstance().workMode));
        //工作时长
        updateWorkTime(HomeCabinet.getInstance().workHours);
        //setCountDownTime();
    }

    /**
     * 更新界面剩余工作时长
     * @param remainingTime
     */
    private void updateWorkTime(int remainingTime){
        String time = TimeUtils.secToHourMinUp(remainingTime);
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

    private void workComplete() {
        //工作完成提示
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setContentText(CabinetEnum.match(HomeCabinet.getInstance().workMode) + "完成");
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束工作
                if (v.getId() == R.id.tv_ok) {
                    startActivity(MainActivity.class);
                }
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
//            startActivity(MainActivity.class);
//            finish();
            //showEndDialog();
        }
    }

    /**
     * 展示主动结束弹窗
     */
    private void workStop() {
        //工作结束提示
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WORK_STOP);
        iDialog.setCancelable(false);
        iDialog.setListeners(v -> {
            //结束工作
            if (v.getId() == R.id.tv_ok) {
                Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
                map.put(CabinetConstant.SteriStatus, 1);
                map.put(CabinetConstant.SteriTime, 0);
                map.put(CabinetConstant.ArgumentNumber,0);
                CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    /**
     * 工作结束弹窗
     */
    private void workFinish(){
        IDialog iDialog =  CabinetDialogFactory.createDialogByType(this,DialogConstant.DIALOG_TYPE_WORK_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setListeners(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //tvCountdown.stop();
    }


}