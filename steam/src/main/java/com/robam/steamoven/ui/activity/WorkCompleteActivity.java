package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamOverTimeDialog;

import java.util.Map;

//蒸、炸、烤模式工作页面
public class WorkCompleteActivity extends SteamBaseActivity {

    public static final String TAG = "ModelWorkActivity";

    private TextView mCancelTv;
    private TextView mOkTv;
    private TextView mContent;
    private TextView mSingleOkTv;

    private SteamOverTimeDialog timeDialog;

    private int directive_offset = 11100000;//指令FLAG
    private static final int DIRECTIVE_OFFSET_END = 10;//主动结束工作
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;//继续烹饪
    private static final int DIRECTIVE_OFFSET_OVER_TIME = 40;//加时
    private static final int DIRECTIVE_OFFSET_WORK_FINISH = 60;//工作结束

    private long curveId;//曲线ID
    private long recipeId = 0;//菜谱ID ； 若菜谱ID非 0 ； 则当前工作模式来源与菜谱
    private int addTime = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_work_complete;
    }

    @Override
    protected void initView() {
        mCancelTv = findViewById(R.id.tv_cancel);
        mOkTv = findViewById(R.id.tv_ok);
        mContent = findViewById(R.id.tv_work_content);
        mSingleOkTv = findViewById(R.id.tv_single_ok);
        setOnClickListener(R.id.tv_cancel,R.id.tv_ok,R.id.tv_single_ok);

        MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s - directive_offset){
                case DIRECTIVE_OFFSET_END:
                    LogUtils.e(TAG+"主动结束回到主页");
                    //goHome();
                    break;
                case DIRECTIVE_OFFSET_OVER_TIME:

                    break;
                case DIRECTIVE_OFFSET_WORK_FINISH:
                    toCurveSavePage();
                    break;
            }
        });

        //设备状态监听
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
    }



    private void updateViews(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                goHome();
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                Intent result = new Intent();
                result.putExtra(Constant.ADD_TIME,addTime);
                setResult(RESULT_OK,result);
                finish();
                break;
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
               //dealWorkFinish(steamOven);
               break;
        }
    }

    /**
     * 跳转到曲线保存界面
     */
    private void toCurveSavePage(){
        dismissAllDialog();
        Intent intent = new Intent(this,CurveSaveActivity.class);
        intent.putExtra(Constant.CURVE_ID,curveId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initData() {
        curveId = getIntent().getLongExtra(Constant.CURVE_ID,0);
        recipeId = getIntent().getLongExtra(Constant.RECIPE_ID,0);
        if(recipeId != 0){
            mCancelTv.setVisibility(View.INVISIBLE);
            mOkTv.setVisibility(View.INVISIBLE);
            mSingleOkTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_cancel) {//加时
            showOverTimeDialog();
        }else if(id == R.id.tv_ok){//完成
            SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
        }else if(id == R.id.tv_single_ok){
            SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
        }
    }



    @Override
    public void goHome(){
        dismissAllDialog();
        Intent intent = new Intent(WorkCompleteActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void dismissAllDialog(){
        if(timeDialog != null && timeDialog.isShow()){
            timeDialog.dismiss();
        }
        if(timeDialog != null && timeDialog.isShow()){
            timeDialog.dismiss();
        }
    }



    /**
     * 展示加时弹窗
     */
    private void showOverTimeDialog(){
        if(timeDialog != null && timeDialog.isShow()){
            return;
        }
        timeDialog = new SteamOverTimeDialog(this);
        timeDialog.setContentText(R.string.steam_work_complete_add_time);
        timeDialog.setOKText(R.string.steam_sure);
        timeDialog.setData();
        timeDialog.setListeners(v -> {
            if(v.getId() == R.id.tv_ok){//确认加时
                //发送结束请求并跳转到保存曲线界面
                addTime = Integer.parseInt(timeDialog.getCurValue())*60;
                sendOverTimeCommand(addTime);
            }else if(v.getId() == R.id.tv_cancel) {//取消
                //sendWorkFinishCommand(DIRECTIVE_OFFSET_WORK_FINISH);
                //timeDialog.dismiss();
                //SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        timeDialog.show();
    }

    /**
     * 发送加时指令，单位秒
     * @param overTime
     */
    private void sendOverTimeCommand(int overTime){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 3);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_8) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, 1);


        commonMap.put(SteamConstant.addExtraTimeCtrlKey, 32);
        commonMap.put(SteamConstant.addExtraTimeCtrlLength, 1);
        commonMap.put(SteamConstant.addExtraTimeCtrl, overTime);

        if (overTime<=255){
            commonMap.put(SteamConstant.recipeSetMinutes, overTime);
        }else{
            commonMap.put(SteamConstant.addExtraTimeCtrlLength, 2);
            short time = (short)(overTime & 0xff);
            commonMap.put(SteamConstant.addExtraTimeCtrl, time);
            short highTime = (short) ((overTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.addExtraTimeCtrl1, highTime);
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,DIRECTIVE_OFFSET_OVER_TIME+directive_offset);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
