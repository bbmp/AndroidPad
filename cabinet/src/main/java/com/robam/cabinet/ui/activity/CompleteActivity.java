package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;

public class CompleteActivity extends CabinetBaseActivity {
    CountDownTimer countDownTimer;
    private int modelCode = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_layout_work_complete;
    }

    @Override
    protected void initView() {
        View completeView = findViewById(R.id.complete_finish);
        setOnClickListener(completeView);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(!CabinetCommonHelper.isSafe()){
                        return;
                    }
                    if(toWaringPage(cabinet.faultId)){
                        return;
                    }
                    if(toOffLinePage(cabinet)){
                        return;
                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                        case CabinetConstant.FUN_WARING:
                            if(cabinet.remainingModeWorkTime > 0 && cabinet.workMode != 0){
                                goHome();//重新工作了，回到主页
                            }
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
    public void onClick(View view) {
        if (view.getId() == R.id.complete_finish) {
            Cabinet cabinet = getCabinet();
            if(cabinet != null){
                boolean isSmart = false;
                if(cabinet.smartCruising == 1 || cabinet.pureCruising == 1){
                    isSmart = true;
                }
                if(isSmart){
                    Intent intent = new Intent(CompleteActivity.this,CruiseActivity.class);
                    int model = cabinet.smartCruising == 1 ? CabinetEnum.SMART.getCode() : CabinetEnum.FLUSH.getCode();
                    intent.putExtra(Constant.SMART_MODEL,model);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
            goHome();
        }
    }

    @Override
    protected void initData() {
        modelCode = getIntent().getIntExtra(Constant.MODE_CODE,0);
        if(modelCode != 0){
            TextView title = findViewById(R.id.complete_title);
            title.setText(CabinetEnum.match(modelCode)+"完成");
        }
        countDownTimer = new CountDownTimer(1000 * 60 * 10, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                goHome();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}