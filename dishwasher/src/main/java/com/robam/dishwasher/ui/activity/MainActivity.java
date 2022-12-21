package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.widget.Toast;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.manager.FunctionManager;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;
import java.util.List;

//远程入口，供烟机调用
public class MainActivity extends DishWasherBaseActivity {



    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_dishwasher_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_dishwasher_activity_main), navController);
        getContentView().postDelayed(()->{
            showRightCenter();
            setLock(HomeDishWasher.getInstance().lock);
        },DishWasherConstant.TIME_DELAYED);

        //final List<DishWasherModeBean> modeBeanList = FunctionManager.getFuntionList(getContext(), DishWasherModeBean.class,R.raw.dishwahser);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            LogUtils.i("MainActivity observe out ...");
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                    LogUtils.i("MainActivity observe in ....");
                    DishWasher dishWasher = (DishWasher) device;
                    if(toWaringPage(dishWasher.abnormalAlarmStatus)){
                        return;
                    }
                    if(!DishWasherCommandHelper.getInstance().isSafe()){//防止历史消息扰乱逻辑
                        return;
                    }
                    setLock(dishWasher.StoveLock == DishWasherState.LOCK);
                    HomeDishWasher.getInstance().isTurnOff = (dishWasher.powerStatus == DishWasherState.OFF);
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WAIT:
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                            dealWasherWorkingState(dishWasher);
                    }
                    break;
                }
            }
        });
        setLock(HomeDishWasher.getInstance().lock);
    }

    @Override
    protected void initData() {
        HomeDishWasher.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        if(StringUtils.isBlank(HomeDishWasher.getInstance().guid)){
            ToastUtils.showLong(this,R.string.dishwasher_no_guid);
            finish();
            return;
        }
        //ToastUtils.show(this,HomeDishWasher.getInstance().guid, Toast.LENGTH_LONG);
        DishWasherAbstractControl.getInstance().queryAttribute(HomeDishWasher.getInstance().guid);
    }

    private void dealWasherWorkingState(DishWasher dishWasher){
        if(dishWasher.workMode == 0){
            return;
        }
        switch (dishWasher.AppointmentSwitchStatus){
            case DishWasherState.APPOINTMENT_OFF:
                if(dishWasher.powerStatus == DishWasherState.WAIT){//待机状态下，无工作模式
                    return;
                }
                if(dishWasher.remainingWorkingTime == 0){//无剩余工作时间
                    return;
                }
                Intent intent = new Intent();
                DishWasherModeBean newMode  = new DishWasherModeBean();
                DishWasherModelUtil.initWorkingInfo(newMode,dishWasher);
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                intent.setClass(this, WorkActivity.class);
                startActivity(intent);
                break;
            case DishWasherState.APPOINTMENT_ON:
                if( dishWasher.AppointmentRemainingTime == 0){
                    return;
                }
                Intent appointingIntent = new Intent();
                DishWasherModeBean curMode  = new DishWasherModeBean();
                DishWasherModelUtil.initWorkingInfo(curMode,dishWasher);
                appointingIntent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, curMode);
                appointingIntent.setClass(this, AppointingActivity.class);
                startActivity(appointingIntent);
                HomeDishWasher.getInstance().workHours = curMode.time;
                HomeDishWasher.getInstance().orderWorkTime = dishWasher.AppointmentRemainingTime;
                break;
        }
    }



}
