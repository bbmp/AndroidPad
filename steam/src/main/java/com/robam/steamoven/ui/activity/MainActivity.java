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
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;

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
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                Intent intent = new Intent(this,ModelWorkActivity.class);
                List<MultiSegment> list = new ArrayList<>();
                list.add(getResult(steamOven));
                list.get(0).setWorkModel(1);
                intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
                break;
        }
    }


    private MultiSegment getResult(SteamOven steamOven){
        MultiSegment segment = new MultiSegment();
        segment.code = steamOven.workMode;
        segment.model = "";
        //segment.model = steamOven.workMode;
        //segment.steam = value;
        segment.defTemp = 55;
        //segment.downTemp = value;
        segment.duration = 3000;
        return segment;
    }



}
