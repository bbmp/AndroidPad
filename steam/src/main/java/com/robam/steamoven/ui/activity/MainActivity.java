package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.utils.MultiSegmentUtil;

import java.util.ArrayList;
import java.util.List;

//非主入口调用入口
public class MainActivity extends SteamBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_main;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_stream_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_stream_activity_main), navController);
        getContentView().postDelayed(()->{
            showRightCenter();
        }, Constant.TIME_DELAYED);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                            toWorkPage(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }


                }
            }
        });
    }

    @Override
    protected void initData() {
        if (null != getIntent())
            HomeSteamOven.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
        LogUtils.e("HomeSteamOven guid " + HomeSteamOven.getInstance().guid);

    }

    private void toWorkPage(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE:
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                HomeSteamOven.getInstance().orderTime = steamOven.orderLeftTime;
                Intent appointIntent = new Intent(this,AppointingActivity.class);
                MultiSegment segment = getResult(steamOven);
                segment.workRemaining = steamOven.orderLeftTime;
                appointIntent.putExtra(Constant.SEGMENT_DATA_FLAG, segment);
                startActivity(appointIntent);
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                if(steamOven.sectionNumber >= 2){
                    Intent intent = new Intent(this,MultiWorkActivity.class);
                    List<MultiSegment> list = getMultiWorkResult(steamOven);
                    intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(this,ModelWorkActivity.class);
                    List<MultiSegment> list = new ArrayList<>();
                    list.add(getResult(steamOven));
                    intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                    startActivity(intent);
                }

                break;
        }
    }

    /**
     * 获取多段数据集合
     * @return
     */
    private List<MultiSegment> getMultiWorkResult(SteamOven steamOven){
        List<MultiSegment> multiSegments = new ArrayList<>();
        for(int i = 0;i < steamOven.sectionNumber;i++){
            multiSegments.add(MultiSegmentUtil.getCurSegment(steamOven,i+1));
        }
        return multiSegments;
    }


    /**
     * 获取当前运行模式数据对象
     * @param steamOven
     * @return
     */
    private MultiSegment getResult(SteamOven steamOven){
        MultiSegment segment = new MultiSegment();
        segment.code = steamOven.mode;
        segment.model = "";
        segment.steam = steamOven.steam;
        segment.defTemp = steamOven.curTemp;
        segment.downTemp = steamOven.setDownTemp;
        int setTime = steamOven.setTimeH * 256 + steamOven.setTime;
        segment.duration = setTime;

        int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
        int restTimeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
        segment.workRemaining =restTimeF*60;

        boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
        segment.setWorkModel(isPreHeat?MultiSegment.COOK_STATE_PREHEAT:MultiSegment.WORK_MODEL_);

        boolean isWorking = steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_WORKING;
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        return segment;
    }



}
