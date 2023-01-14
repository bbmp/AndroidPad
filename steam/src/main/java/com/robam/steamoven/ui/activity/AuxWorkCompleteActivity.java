package com.robam.steamoven.ui.activity;

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

//蒸、炸、烤模式工作页面
public class AuxWorkCompleteActivity extends SteamBaseActivity {

    public static final String TAG = "AuxWorkCompleteActivity";
    private TextView mContentTv;
    private int auxCode;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_aux_work_complete;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.tv_cancel);
        //设备状态监听
        mContentTv = findViewById(R.id.tv_work_content);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
//                    if(toWaringPage(steamOven)){
//                        return;
//                    }
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
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                goHome();
                break;
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
               //dealWorkFinish(steamOven);
               break;
        }
    }



    @Override
    protected void initData() {
        auxCode = getIntent().getIntExtra(Constant.AUX_MODEL,0);
        if(auxCode != 0){
            mContentTv.setText(SteamModeEnum.match(auxCode)+"已完成");
        }else{
            mContentTv.setText("工作完成");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_cancel) {
            goHome();
        }
    }












}
