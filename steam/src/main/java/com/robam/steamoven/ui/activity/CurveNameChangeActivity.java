package com.robam.steamoven.ui.activity;


import android.view.View;
import android.widget.EditText;

import com.github.mikephil.charting.data.Entry;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastInsUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CookingCurveQueryRes;
import com.robam.steamoven.bean.CurveData;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.utils.MqttSignal;
import com.robam.steamoven.utils.SkipUtil;
import com.robam.steamoven.utils.SteamPageData;

import java.util.ArrayList;
import java.util.List;

//曲线选中进入
public class CurveNameChangeActivity extends SteamBaseActivity {
    private long curveId;
    //曲线步骤

    private MqttSignal mqttSignal;

    private CookingCurveQueryRes curveDetailRes;

    private EditText nameEt;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_curve_name_change;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        nameEt = findViewById(R.id.tv_recipe_name_change);

        if (null != getIntent())
            curveId = getIntent().getLongExtra(SteamConstant.EXTRA_CURVE_ID, -1);
        setOnClickListener(R.id.change_ok);
        setOnClickListener(R.id.change_cancel);
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
                            toWorkPage2(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }


                }
            }
        });
    }

    private void toWorkPage2(SteamOven steamOven){
        if(steamOven.mode == 0){
            return;
        }
        SkipUtil.toWorkPage(steamOven,this);
    }



    @Override
    protected void initData() {
        getCurveDetail();
        mqttSignal = new MqttSignal();
    }

    //曲线详情
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, curveId, CookingCurveQueryRes.class, new RetrofitCallback<CookingCurveQueryRes>() {
            @Override
            public void onSuccess(CookingCurveQueryRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.data) {
                    curveDetailRes = getCurveDetailRes;
                    nameEt.setText(getCurveDetailRes.data.name+"");
                }
            }

            @Override
            public void onFaild(String err) {
                ToastInsUtils.showLong(CurveNameChangeActivity.this,R.string.steam_curve_info_error_prompt);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
         if (id == R.id.ll_left) { //返回
            finish();
        }else if(id == R.id.change_cancel){
             finish();
        }else if(id == R.id.change_ok){
             if(StringUtils.isEmpty(nameEt.getText().toString())){
                 ToastInsUtils.showLong(this,R.string.steam_curve_name_empty_prompt);
                 return;
             }
             saveCurveStep(nameEt.getText().toString().trim());
        }

    }

    private void saveCurveStep(String name){
        if(curveDetailRes == null || curveDetailRes.data == null){
            ToastInsUtils.showLong(this,R.string.steam_curve_no_device);
            return;
        }
        UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
        if(userInfo == null){
            ToastInsUtils.showLong(this,R.string.steam_no_user_info);
            return;
        }
        curveDetailRes.data.name = name;
        if(curveDetailRes.data.curveStepList == null || curveDetailRes.data.curveStepList.size() == 0){
            curveDetailRes.data.curveStepList = new ArrayList<>();;
        }
        CloudHelper.saveCurveStepData(this, userInfo.id,curveDetailRes.data, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if(!isDestroyed()){
                    SteamPageData.getInstance().getBsData().setValue(new SteamPageData.BusData(curveDetailRes.data.curveCookbookId,name,null));
                    ToastInsUtils.showLong(CurveNameChangeActivity.this,R.string.steam_curve_success);
                    finish();
                }
            }

            @Override
            public void onFaild(String err) {
                ToastInsUtils.showLong(CurveNameChangeActivity.this,R.string.steam_curve_error);
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        mqttSignal.pageHide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqttSignal.pageShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttSignal.clear();
    }




}