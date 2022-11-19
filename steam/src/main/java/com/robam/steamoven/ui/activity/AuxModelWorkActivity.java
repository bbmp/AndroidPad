package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamErrorDialog;
import com.robam.steamoven.utils.TextSpanUtil;

//辅助模式
public class AuxModelWorkActivity extends SteamBaseActivity {

    private ViewGroup promptParentView;
    private ViewGroup progressParentView;

    private TextView promptTvInd1,promptTvInd2,promptTvInd3;
    private TextView promptText;

    private TextView sureTv;
    private TextView progressPromptTv;
    private ProgressBar progressBar1,progressBar2,progressBar3;

    private int curTemp = 0;
    private boolean isProgress = false;


    private  TextView modelNameTv,modelTimeTv,modelTempTv;

    private MultiSegment segment;

    private long workTimeMS;

    private ImageView pauseIv,reStartIv;

    public static final int EDN_FLAG = 33;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_aux_model_work;
    }

    @Override
    protected void initView() {
        //showLeft();
        showCenter();
        showLight();
        promptParentView = findViewById(R.id.descaling_prompt_parent);
        promptTvInd1 = findViewById(R.id.descaling_index_1);
        promptTvInd2 = findViewById(R.id.descaling_index_2);
        promptTvInd3 = findViewById(R.id.descaling_index_3);
        promptText = findViewById(R.id.descaling_text_prompt);
        sureTv = findViewById(R.id.btn_start);

        progressParentView = findViewById(R.id.descaling_progress_parent);
        progressPromptTv = findViewById(R.id.descaling_segment_prompt);
        progressBar1 = findViewById(R.id.descaling_progress_1);
        progressBar2 = findViewById(R.id.descaling_progress_2);
        progressBar3 = findViewById(R.id.descaling_progress_3);

        modelNameTv = findViewById(R.id.aux_work_model_name);
        modelTimeTv = findViewById(R.id.aux_work_model_time);
        modelTempTv = findViewById(R.id.aux_work_model_temp);

        pauseIv = findViewById(R.id.aux_work_model_pause);
        reStartIv = findViewById(R.id.aux_work_model_restart);

        setOnClickListener(R.id.btn_start,R.id.aux_work_model_pause,R.id.aux_work_model_restart);

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
                            updateViews(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            goHome();
                            break;
                    }

                }
            }
        });
//        MqttDirective.getInstance().getDirective().observe(this, s -> {
//            switch (s){
//                case EDN_FLAG:
//                    goHome();
//                    break;
//            }
//        });
    }

    private void updateViews(SteamOven steamOven){
        switch (steamOven.workState){
            //case SteamStateConstant.WORK_STATE_APPOINTMENT:
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
                goHome();
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                if(steamOven.mode != segment.code){//工作模式已切换
                    goHome();
                    return;
                }
                if(steamOven.mode != SteamModeEnum.CHUGOU.getMode()){
                    modelNameTv.setVisibility(View.VISIBLE);
                    modelTempTv.setVisibility(View.VISIBLE);
                    modelTimeTv.setVisibility(View.VISIBLE);
                    int restTime = steamOven.restTimeH * 256 + steamOven.restTime;
                    modelTimeTv.setText(TextSpanUtil.getSpan(restTime,Constant.UNIT_TIME_MIN));
                    return;
                }
                //boolean isPause = (steamOven.workState == SteamStateConstant.WORK_STATE_WORKING_PAUSE)
                //pauseIv.setVisibility(isPause?View.INVISIBLE:View.VISIBLE);
                //reStartIv.setVisibility(isPause?View.VISIBLE:View.INVISIBLE);
                progressPromptTv.setText(getProgressTitle(steamOven.chugouType));
                setProgressIndex(steamOven.chugouType,true);
                modelNameTv.setVisibility(View.INVISIBLE);
                modelTimeTv.setVisibility(View.INVISIBLE);
                modelTempTv.setVisibility(View.INVISIBLE);
                break;

        }
    }

    private String getProgressTitle(int index){
        switch (index){
            case 1:
                return "第一阶段";
            case 2:
                return "第二阶段";
            case 3:
                return "第三阶段";
        }
        return null;
    }


    private void showLight(){
        showRightCenter();
        ((TextView)findViewById(R.id.tv_right_center)).setText(R.string.steam_light);
        ((ImageView)findViewById(R.id.iv_right_center)).setImageResource(R.drawable.steam_ic_light_max);
    }


    private void setProgressIndex(int curTemp,boolean isProgress){
        if(curTemp > 3){
            return;
        }
        if(isProgress){
            promptParentView.setVisibility(View.INVISIBLE);
            progressParentView.setVisibility(View.VISIBLE);
            for(int i = 1;i <= curTemp ;i++){
                if(i == 1){
                    progressBar1.setProgress(100);
                }else if(i == 2){
                    progressBar2.setProgress(100);
                }else if(i == 3){
                    progressBar3.setProgress(100);
                }
            }
        }else{
            promptParentView.setVisibility(View.VISIBLE);
            progressParentView.setVisibility(View.INVISIBLE);
            if(curTemp ==1){
                promptTvInd1.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                promptTvInd2.setBackground(null);
                promptTvInd3.setBackground(null);
            }else if(curTemp ==2){
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                promptTvInd3.setBackground(null);
            }else if(curTemp == 3){
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackground(null);
                promptTvInd3.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
            }
        }

    }




    @Override
    protected void initData() {
        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        modelNameTv.setText(SteamModeEnum.match(segment.code));
        modelTimeTv.setText(TextSpanUtil.getSpan(segment.duration*60,Constant.UNIT_TIME_MIN));
        modelTempTv.setText(TextSpanUtil.getSpan(segment.defTemp,Constant.UNIT_TEMP));

        if(segment.code == SteamModeEnum.CHUGOU.getMode()){
            pauseIv.setVisibility(View.INVISIBLE);
            reStartIv.setVisibility(View.INVISIBLE);
        }
    }






    @Override
    public void onClick(View view) {
        //super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            descallingFinish();
        }else if(id == R.id.btn_start){
            curTemp += 1;
            isProgress = true;
            setProgressIndex(curTemp,isProgress);
        }else if(id == R.id.aux_work_model_pause){
            //SteamCommandHelper.sendWorkCtrCommand(true,0);
            showEndDialog();
        }else if(id == R.id.aux_work_model_pause){
            //SteamCommandHelper.sendWorkCtrCommand(false,0);
        }
    }

    public void showEndDialog(){
        SteamCommonDialog endDialog = new SteamCommonDialog(this);
        endDialog.setContentText("是否结束"+SteamModeEnum.match(segment.code));
        endDialog.setOKText(R.string.steam_common_confirm);
        endDialog.setCancelText(R.string.steam_common_cancel);
        endDialog.setListeners(v -> {
            endDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
               //TODO(发送结束指令)
                SteamCommandHelper.sendEndWorkCommand(EDN_FLAG);
            }
        },R.id.tv_ok);
        endDialog.show();
    }




    public void descallingFinish(){
        SteamErrorDialog steamCommonDialog = new SteamErrorDialog(this);
        steamCommonDialog.setContentText(R.string.steam_descaling_complete);
        steamCommonDialog.setOKText(R.string.steam_common_step_complete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                Intent intent = new Intent(AuxModelWorkActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },R.id.tv_ok);
        steamCommonDialog.show();
    }



}
