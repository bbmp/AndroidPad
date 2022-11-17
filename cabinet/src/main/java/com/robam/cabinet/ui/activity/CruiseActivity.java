package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.view.View;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.ui.dialog.IDialog;

/**
 *  智能巡航等待界面
 */
public class CruiseActivity extends CabinetBaseActivity {



    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_cruise;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        setOnClickListener(R.id.ll_left, R.id.iv_start);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    if(!CabinetCommonHelper.isSafe()){
                        return;
                    }
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
//                    if(toWaringPage(cabinet.alarmStatus)){
//                        return;
//                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                        case CabinetConstant.FUN_WARING:
                            toWorkingPage(cabinet);
                            break;
                        default:
                            break;
                    }

                    break;
                }
            }
        });


    }

    @Override
    protected void initData() {

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
                goHome();
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            workStop();
        } else if (id == R.id.iv_start) {
            workStop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toWorkingPage(Cabinet cabinet) {
        if(cabinet.remainingModeWorkTime > 0){//工作
            WorkModeBean workModeBean = new WorkModeBean(cabinet.workMode,0, cabinet.remainingModeWorkTime);
            Intent intent = new Intent(this,WorkActivity.class);
            intent.putExtra(Constant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
            finish();
        }else if(cabinet.remainingAppointTime > 0){//预约 每次结束后，都有一段时间预约时间是1380，需与设备端一起排查问题
            WorkModeBean workModeBean = new WorkModeBean(cabinet.workMode, cabinet.remainingModeWorkTime,cabinet.modeWorkTime);
            Intent intent = new Intent(this,AppointingActivity.class);
            intent.putExtra(Constant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
            finish();
        }
    }


}