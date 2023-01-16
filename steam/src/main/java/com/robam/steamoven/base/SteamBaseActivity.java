package com.robam.steamoven.base;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.DeviceErrorInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.manager.DeviceWarnInfoManager;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.manager.SteamActivityManager;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.activity.MainActivity;
import com.robam.steamoven.ui.activity.MatchNetworkActivity;
import com.robam.steamoven.ui.activity.RemindActivity;
import com.robam.steamoven.ui.activity.WaringActivity;


public abstract class SteamBaseActivity extends BaseActivity {
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListener(R.id.ll_left);
        SteamActivityManager.getInstance().addActivity(this);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
        setOnClickListener(R.id.ll_left);
    }

    public void hideLeft(){
        findViewById(R.id.ll_left).setVisibility(View.INVISIBLE);
    }

    public void showLeftCenter() {
        findViewById(R.id.ll_left_center).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_left_center).setOnClickListener(v -> {
            SteamOven steamOven = getSteamOven();
            if(steamOven != null){
                SteamCommandHelper.sendSteamOrRotateCommand(QualityKeys.rotateSwitch, (short) (((int)steamOven.rotateSwitch) == 0?1:0),109);
            }
        });
    }

    public void hideLeftCenter(){
        findViewById(R.id.ll_left_center).setVisibility(View.INVISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        ImageView ivWifi = findViewById(R.id.iv_center);
        //监听网络连接状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ivWifi.setVisibility(View.VISIBLE);
                else
                    ivWifi.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void showRightCenter(){
        View rightCenter = findViewById(R.id.ll_right_center);
        if(rightCenter == null){
            return;
        }
        rightCenter.setVisibility(View.VISIBLE);
        setOnClickListener(R.id.ll_right_center);
    }

    public void setRight(String value) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(value);
    }


    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }
    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        findViewById(R.id.ll_right).setVisibility(View.INVISIBLE);
    }

    public SteamOven getSteamOven(){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                return (SteamOven) device;
            }
        }
        return null;
    }

    public SteamOven getSteamOven(String deviceGuid){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof SteamOven && device.guid.equals(deviceGuid)) {
                return (SteamOven) device;
            }
        }
        return null;
    }


    /**
     * 回到主页
     */
    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到告警页面
     * @param steamOven
     * @return
     */
    public boolean toWaringPage(SteamOven steamOven){
        if(steamOven.faultId != 0 && preRemindResId != R.string.steam_water_deficient){
            DeviceErrorInfo deviceErrorInfo = DeviceWarnInfoManager.getInstance().getDeviceErrorInfo(IDeviceType.RZKY, DeviceUtils.getDeviceTypeId(steamOven.guid), steamOven.faultId);
            if(deviceErrorInfo == null){
                return false;
            }
            if(deviceErrorInfo.code == Constant.WARING_CODE_11 && steamOven.getResidueTotalTime() <= 0){//WARING_CODE_11 是缺水状态，若剩余时间是0(即工作结束了)，不需要弹缺水告警
                return false;
            }
            Intent intent = new Intent(this, WaringActivity.class);
            intent.putExtra(ComnConstant.WARING_CODE,steamOven.faultId);
            startActivity(intent);
            return true;
        }
        return false;
    }


    /**
     * 上一次提醒内容对应资源ID
     */
    private int preRemindResId = -1;

    /**
     * 调整到提醒页面
     * @param curDevice
     * @return
     */
    public boolean toRemandPage(SteamOven curDevice){
        boolean needWater;
        if(curDevice.recipeId != 0){
            needWater = RecipeManager.getInstance().needWater(IDeviceType.SERIES_STEAM, curDevice.recipeId);
        }else{
            needWater = SteamModeEnum.needWater(curDevice.mode) || SteamModeEnum.isManuallyAddSteam(curDevice.mode);
        }
        return this.toRemandPage(curDevice,needWater);
    }

    public boolean toRemandPage(SteamOven curDevice,int curMode){
        boolean needWater = SteamModeEnum.needWater(curMode) || SteamModeEnum.isManuallyAddSteam(curMode);
        return this.toRemandPage(curDevice,needWater);
    }

    public boolean toRemandPage(SteamOven curDevice,boolean needWater){
        int remindResId = SteamCommandHelper.getRemindPromptResId(curDevice,needWater);
        if(remindResId != 0 && preRemindResId != remindResId){
            preRemindResId = remindResId;
            Intent intent = new Intent(this, RemindActivity.class);
            intent.putExtra(Constant.REMIND_BUS_CODE,remindResId);
            intent.putExtra(Constant.REMIND_NEED_WATER,needWater);
            startActivity(intent);
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SteamActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 去往离线提示页面
     * @param steamOven
     * @return
     */
    protected boolean toOffLinePage(SteamOven steamOven){
        if(steamOven.status == Device.OFFLINE && steamOven.queryNum > 2){
            Intent intent = new Intent(this, MatchNetworkActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void showRemindPage(int remindResId,boolean needWater,int modeCode,boolean needCheckDescale){
        Intent intent = new Intent(this, RemindActivity.class);
        intent.putExtra(Constant.REMIND_BUS_CODE,remindResId);
        intent.putExtra(Constant.REMIND_NEED_WATER,needWater);
        intent.putExtra(Constant.REMIND_NEED_Descale,needCheckDescale);
        intent.putExtra(Constant.REMIND_MODE_CODE,modeCode);
        startActivity(intent);
    }




}
