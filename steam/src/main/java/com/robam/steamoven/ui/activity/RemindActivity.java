package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;

public class RemindActivity extends SteamBaseActivity {

    private TextView titleTv;
    private int remainBusCode;
    private TextView sureTv;
    private boolean needWater;
    private boolean needCheckDescale;
    private int curMode = -1;
    private boolean remindChange = false;
    private boolean needAutoClose = false;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_remind;
    }

    @Override
    protected void initView() {
        titleTv = findViewById(R.id.remind_title);
        sureTv = findViewById(R.id.remind_btn);
        setOnClickListener(R.id.remind_btn);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(toOffLinePage(steamOven)){
                        return;
                    }

                                        //int remindResId = SteamCommandHelper.getRemindPromptResId(steamOven, needWater);
                    //缺水需要处理
//                    if(needAutoClose && steamOven.getResidueTotalTime() <= 0){//工作模式结束
//                        pageEnd();
//                        return;
//                    }
                    if(needAutoClose){
                        int tempMode = curMode == -1 ? steamOven.mode : curMode;
                        int remindResId = SteamCommandHelper.getRunPromptResId(steamOven, tempMode, needWater,needCheckDescale);
                        remindChange = (remindResId != remainBusCode);
                        if(remindResId == 0 || remindResId == -1){
                            pageEnd();
                        }else{
                            if(remindResId != remainBusCode){
                                titleTv.setText(remindResId);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        remainBusCode = getIntent().getIntExtra(Constant.REMIND_BUS_CODE,0);
        needWater = getIntent().getBooleanExtra(Constant.REMIND_NEED_WATER,false);
        curMode = getIntent().getIntExtra(Constant.REMIND_MODE_CODE,-1);
        needCheckDescale = getIntent().getBooleanExtra(Constant.REMIND_NEED_Descale,false);
        needAutoClose = getIntent().getBooleanExtra(Constant.REMIND_NEED_AUTO_CLOSE,false);
        if(remainBusCode != 0 && remainBusCode != -1){
            titleTv.setText(remainBusCode);
        }
//        if(remainBusCode == R.string.steam_water_deficient_remain || remainBusCode == R.string.steam_waste_water){
//            sureTv.setText(R.string.steam_iamknown);
//        }
        sureTv.setText(R.string.steam_iamknown);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.remind_btn){
            remindChange = false;
            pageEnd();
        }
    }

    private void pageEnd(){
        Intent result = new Intent();
        result.putExtra(Constant.REMIND_ID_CHANGE,remindChange);
        setResult(RESULT_OK,result);
        finish();
    }
}