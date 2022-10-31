package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.constant.ComnConstant;
import com.robam.common.device.Plat;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.dishwasher.device.DishWasherMqttControl;
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

    }

    @Override
    protected void initData() {

        //开启远程控制
        DishWasherAbstractControl.getInstance().init(new DishWasherMqttControl());
        HomeDishWasher.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        ToastUtils.show(this,HomeDishWasher.getInstance().guid, Toast.LENGTH_LONG);
        final List<DishWasherModeBean> modeBeanList = FunctionManager.getFuntionList(getContext(), DishWasherModeBean.class,R.raw.dishwahser);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                    DishWasher dishWasher = (DishWasher) device;
                    LogUtils.e("MainActivity mqtt msg arrive isWorking "+dishWasher.powerStatus);
                    if(toWaringPage(dishWasher.abnormalAlarmStatus)){
                        return;
                    }
                    if(!DishWasherCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    setLock(dishWasher.StoveLock == DishWasherState.LOCK);
                    //washer.AppointmentSwitchStatus 预约状态  开： DishWasherStatus.appointmentSwitchOn 关 ： DishWasherStatus.appointmentSwitchOff
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WAIT:
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                            dealWasherWorkingState(modeBeanList,dishWasher);
                    }
                    break;
                }
            }
        });
        setLock(HomeDishWasher.getInstance().lock);
        getLastState();
    }





    private void dealWasherWorkingState(List<DishWasherModeBean> modeBeanList ,DishWasher dishWasher){
        switch (dishWasher.AppointmentSwitchStatus){
            case DishWasherState.APPOINTMENT_OFF:
                Intent intent = new Intent();
                DishWasherModeBean dishWasherModeBean = DishWasherModelUtil.getDishWasher(modeBeanList,dishWasher.DishWasherWorkMode);
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, dishWasherModeBean);
                intent.setClass(this, WorkActivity.class);
                startActivity(intent);
                break;
            case DishWasherState.APPOINTMENT_ON:
                Intent appointingIntent = new Intent();
                DishWasherModeBean curWasherModel = DishWasherModelUtil.getDishWasher(modeBeanList,dishWasher.DishWasherWorkMode);
                appointingIntent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, curWasherModel);
                appointingIntent.setClass(this, AppointingActivity.class);
                startActivity(appointingIntent);
                HomeDishWasher.getInstance().workHours = curWasherModel.time;
                HomeDishWasher.getInstance().orderWorkTime = (int) dishWasher.AppointmentRemainingTime;
                break;
        }
    }

}
