package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.utils.TextSpanUtil;

//辅助模式
public class AuxModelWorkActivity extends SteamBaseActivity {

    private ViewGroup promptParentView;
    private ViewGroup progressParentView;
    private TextView promptTvInd1,promptTvInd2,promptTvInd3;
    private TextView promptText;
    private TextView sureTv;
    private TextView mPauseTv;
    private TextView progressPromptTv;
    private ProgressBar progressBar1,progressBar2;
    private int curTemp = 0;
    private boolean isProgress = false;
    private  TextView modelNameTv,modelTimeTv,modelTempTv;
    private MultiSegment segment;
    private long workTimeMS;
    private ImageView pauseIv,reStartIv;
    public static final int EDN_FLAG = 33;
    public static final int LAST_EDN_FLAG = 10;
    private int curModeCode;
    private boolean isWorkFinish = false;
    private boolean isEnd = false;//是否主动结束

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_aux_model_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        promptParentView = findViewById(R.id.descaling_prompt_parent);
        promptTvInd1 = findViewById(R.id.descaling_index_1);
        promptTvInd2 = findViewById(R.id.descaling_index_2);
        //promptTvInd3 = findViewById(R.id.descaling_index_3);
        promptText = findViewById(R.id.descaling_text_prompt);
        sureTv = findViewById(R.id.btn_start);

        progressParentView = findViewById(R.id.descaling_progress_parent);
        progressPromptTv = findViewById(R.id.descaling_segment_prompt);
        progressBar1 = findViewById(R.id.descaling_progress_1);
        progressBar2 = findViewById(R.id.descaling_progress_2);
        //progressBar3 = findViewById(R.id.descaling_progress_3);

        modelNameTv = findViewById(R.id.aux_work_model_name);
        modelTimeTv = findViewById(R.id.aux_work_model_time);
        modelTempTv = findViewById(R.id.aux_work_model_temp);

        pauseIv = findViewById(R.id.aux_work_model_pause);
        reStartIv = findViewById(R.id.aux_work_model_restart);
        mPauseTv = findViewById(R.id.aux_work_model_pause_tv);

        setOnClickListener(R.id.btn_start,R.id.aux_work_model_pause,R.id.aux_work_model_restart);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    if(toRemandPage(steamOven)){
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
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
                toWorkCompletePage();
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                if(steamOven.mode != segment.code){//工作模式已切换
                    toWorkCompletePage();
                    return;
                }
                if(steamOven.mode != 0){
                    curModeCode = steamOven.mode;
                }
                boolean isPause = (steamOven.workState == SteamStateConstant.WORK_STATE_WORKING_PAUSE);
                if(steamOven.mode != SteamModeEnum.CHUGOU.getMode()){
                    pauseIv.setVisibility(isPause?View.INVISIBLE:View.VISIBLE);
                    reStartIv.setVisibility(isPause?View.VISIBLE:View.INVISIBLE);
                    modelNameTv.setVisibility(View.VISIBLE);
                    modelTempTv.setVisibility(View.VISIBLE);
                    modelTimeTv.setVisibility(View.VISIBLE);
                    int restTime = steamOven.restTimeH * 256 + steamOven.restTime;
                    isWorkFinish = restTime < LAST_EDN_FLAG ? true:false;
                    modelTimeTv.setText(TextSpanUtil.getSpan(restTime,Constant.UNIT_TIME_MIN));
                    mPauseTv.setVisibility(View.INVISIBLE);
                    return;
                }
                pauseIv.setVisibility(View.INVISIBLE);
                reStartIv.setVisibility(isPause ? View.VISIBLE:View.INVISIBLE);
                progressPromptTv.setText(getProgressTitle(steamOven.chugouType,isPause));
                setProgressIndex(steamOven.chugouType,true);
                modelNameTv.setVisibility(View.INVISIBLE);
                modelTimeTv.setVisibility(View.INVISIBLE);
                modelTempTv.setVisibility(View.INVISIBLE);
                mPauseTv.setVisibility(isPause?View.VISIBLE:View.INVISIBLE);
                isWorkFinish = true;//除垢不能结束,默认均是工作完成（用于页面跳转）
                break;

        }
    }

    private String getProgressTitle(int index,boolean isPause){
        switch (index){
            case 1:
                return isPause ? "第一阶段 暂停中" : "第一阶段";
            case 2:
                return isPause ? "第二阶段 暂停中" :"第二阶段";
        }
        return isPause ? "第二阶段 暂停中" :"第二阶段";
    }

    private void toWorkCompletePage(){
        if(isWorkFinish && !isEnd){
            Intent intent = new Intent(this,AuxWorkCompleteActivity.class);
            intent.putExtra(Constant.AUX_MODEL,curModeCode);
            startActivity(intent);
        }else{
            goHome();
        }
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
                    progressBar1.setProgress(50);
                }else if(i == 2){
                    progressBar1.setProgress(100);
                    progressBar2.setProgress(50);
                }else{
                    progressBar1.setProgress(100);
                    progressBar2.setProgress(50);
                }
            }
        }else{
            promptParentView.setVisibility(View.VISIBLE);
            progressParentView.setVisibility(View.INVISIBLE);
            if(curTemp ==1){
                promptTvInd1.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                promptTvInd2.setBackground(null);
                //setBackground(null);
            }else if(curTemp ==2){
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                //promptTvInd3.setBackground(null);
            }else{
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                //promptTvInd3.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
            }
        }

    }


    @Override
    protected void initData() {
        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        modelNameTv.setText(SteamModeEnum.match(segment.code));
        modelTimeTv.setText(TextSpanUtil.getSpan(segment.duration*60,Constant.UNIT_TIME_MIN));
        if(SteamModeEnum.isFixedTemp(segment.code)){
            modelTempTv.setVisibility(View.GONE);
        }else{
            modelTempTv.setText(TextSpanUtil.getSpan(segment.defTemp,Constant.UNIT_TEMP));
        }

        if(segment.code == SteamModeEnum.CHUGOU.getMode()){
            pauseIv.setVisibility(View.INVISIBLE);
            reStartIv.setVisibility(View.INVISIBLE);
            hideLeft();
            SteamOven steamOven = getSteamOven();
            if(steamOven != null){
                updateViews(steamOven);
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            showEndDialog();
        }else if(id == R.id.btn_start){
            curTemp += 1;
            isProgress = true;
            setProgressIndex(curTemp,isProgress);
        }else if(id == R.id.aux_work_model_pause){
            SteamCommandHelper.sendWorkCtrCommand(false);
        }else if(id == R.id.aux_work_model_restart){
            if(toRemainPage(getSteamOven())){
                return;
            }
            SteamCommandHelper.sendWorkCtrCommand(true);
        }
    }

    public void showEndDialog(){
        SteamCommonDialog endDialog = new SteamCommonDialog(this);
        //endDialog.setContentText("是否结束"+SteamModeEnum.match(segment.code));
        endDialog.setContentText(R.string.steam_work_aux_back_message);
        endDialog.setOKText(R.string.steam_aux_model_finish_now);
        endDialog.setCancelText(R.string.steam_common_cancel);
        endDialog.setListeners(v -> {
            endDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                isEnd = true;
                SteamCommandHelper.sendEndWorkCommand(EDN_FLAG);
            }
        },R.id.tv_ok,R.id.tv_cancel);
        endDialog.show();
    }

    private  boolean toRemainPage(SteamOven steamOven){
        if(steamOven == null){
            showRemindPage(R.string.steam_offline,false,-1,false,false);
            return true;
        }
        boolean needWater = SteamModeEnum.needWater(steamOven.mode);
        int promptResId = SteamCommandHelper.getRunPromptResId(steamOven, steamOven.mode, needWater,false);
        if(promptResId != -1){
            showRemindPage(promptResId,needWater,steamOven.mode,false,false);
            return true;
        }
        return false;
    }

}
