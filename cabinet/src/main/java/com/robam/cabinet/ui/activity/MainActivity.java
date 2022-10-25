package com.robam.cabinet.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.content.Intent;
import android.view.MotionEvent;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.CabinetMqttControl;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.manager.FunctionManager;
import java.util.List;

public class MainActivity extends CabinetBaseActivity {



    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_cabinet_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_cabinet_activity_main), navController);
        //findViewById(R.id.home_root).setOn

    }




    @Override
    protected void initData() {

        //开启远程控制
        HomeCabinet.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        CabinetAbstractControl.getInstance().init(new CabinetMqttControl());
        List<CabFunBean> functionList = FunctionManager.getFuntionList(getContext(), CabFunBean.class, R.raw.cabinet);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    switch (cabinet.status){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                            if(CabinetCommonHelper.isSafe()){
                                this.toWorkingPage(cabinet);
                            }
                            break;
                        case CabinetConstant.APPOINTMENT:
                            toAppointingPage(functionList,cabinet);
                            break;
                    }
                    break;
                }
            }
        });
    }

    private void toWorkingPage(Cabinet cabinet) {
        HomeCabinet.getInstance().workMode = cabinet.status;
        HomeCabinet.getInstance().workHours = cabinet.remainingModeWorkTime;
        Intent intent = new Intent(this,WorkActivity.class);
        startActivity(intent);
    }

    private void toAppointingPage( List<CabFunBean> functionList,Cabinet cabinet){
        //HomeCabinet.getInstance().orderTime = CabinetAppointmentUtil.getAppointmentTime(tvTime.getText().toString())+"";
        //HomeCabinet.getInstance().workHours = cabinet.defTime;
        //获取预约剩余时间
        //获取预约模式 以及 模式执行时间
        //cabinet.steriReminderTime;
        HomeCabinet.getInstance().orderTime = cabinet.remainingAppointTime+"";
        //TODO(确定能否获取到当前是那个模式处于预约状态 - 找设备开发人员确定，若无法获取，则通知UI调整设计)
        HomeCabinet.getInstance().workMode = 4;
        Intent intent = new Intent(this,AppointingActivity.class);
        intent.putExtra(CabinetConstant.EXTRA_MODE_BEAN, getCabFunBean(functionList,HomeCabinet.getInstance().workMode).mode);
        startActivity(intent);
    }

    private  CabFunBean getCabFunBean(List<CabFunBean> functionList,int workModel){
        for(CabFunBean funBean : functionList){
            if(funBean.funtionCode == workModel){
                return funBean;
            }
        }
        return null;
    }

}