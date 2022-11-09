package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
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
        //getContentView().postDelayed(()-> showRightCenter(), Constant.TIME_DELAYED);
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
                        case SteamStateConstant.POWER_STATE_TROUBLE:
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
        if (null != getIntent()){
            HomeSteamOven.getInstance().guid = getIntent().getStringExtra(ComnConstant.EXTRA_GUID);
            SteamAbstractControl.getInstance().queryAttribute(HomeSteamOven.getInstance().guid);
        }
        //状态检查
        if(StringUtils.isBlank(HomeSteamOven.getInstance().guid)){
            ToastUtils.showLong(this,R.string.steam_guid_prompt);
            finish();
        }

    }

    /**
     * 跳转到指定业务页面
     * @param steamOven
     */
    private void toWorkPage(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE:
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT://预约页面
                HomeSteamOven.getInstance().orderTime = steamOven.orderLeftTime;
                Intent appointIntent = new Intent(this,AppointingActivity.class);
                MultiSegment segment = MultiSegmentUtil.getSkipResult(steamOven);
                segment.workRemaining = steamOven.orderLeftTime;
                appointIntent.putExtra(Constant.SEGMENT_DATA_FLAG, segment);
                startActivity(appointIntent);
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                //辅助模式工作页面
                if(SteamModeEnum.isAuxModel(steamOven.mode)){
                    Intent intent = new Intent(this,AuxModelWorkActivity.class);
                    intent.putExtra(Constant.SEGMENT_DATA_FLAG,MultiSegmentUtil.getSkipResult(steamOven));
                    startActivity(intent);
                    return;
                }

                Intent intent;
                List<MultiSegment> list;
                if(steamOven.sectionNumber >= 2){
                    //多段工作页面
                    intent = new Intent(this, MultiWorkActivity.class);
                    list = getMultiWorkResult(steamOven);
                }else{
                    //基础模式工作页面
                    intent = new Intent(this, ModelWorkActivity.class);
                    list = new ArrayList<>();
                    list.add(MultiSegmentUtil.getSkipResult(steamOven));
                }
                intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
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
}
