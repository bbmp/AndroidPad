package com.robam.cabinet.ui.activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.content.Intent;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;

public class MainActivity extends CabinetBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_cabinet_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_cabinet_activity_main), navController);
        getContentView().postDelayed(()->{
            showRightCenter();
            setLock(HomeCabinet.getInstance().lock);
        },50);
    }


    @Override
    protected void initData() {
        //开启远程控制
        HomeCabinet.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        if(StringUtils.isBlank(HomeCabinet.getInstance().guid)){
            ToastUtils.showLong(this,R.string.cabinet_no_guid);
            return;
        }
        CabinetAbstractControl.getInstance().queryAttribute(HomeCabinet.getInstance().guid);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(!CabinetCommonHelper.isSafe()){
                        return;
                    }
                    switch (cabinet.status){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                            this.toWorkingPage(cabinet);
                            break;
                    }
                    break;
                }
            }
        });
    }

    private void toWorkingPage(Cabinet cabinet) {
        if(cabinet.remainingAppointTime != 0){//预约
            WorkModeBean workModeBean = new WorkModeBean(cabinet.status, cabinet.remainingModeWorkTime,0);
            Intent intent = new Intent(this,AppointingActivity.class);
            intent.putExtra(CabinetConstant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
        }else if(cabinet.remainingModeWorkTime != 0){//工作
            WorkModeBean workModeBean = new WorkModeBean(cabinet.status,0, cabinet.remainingModeWorkTime);
            Intent intent = new Intent(this,WorkActivity.class);
            intent.putExtra(CabinetConstant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
        }
    }
}