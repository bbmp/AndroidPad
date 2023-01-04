package com.robam.steamoven.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.ui.activity.AppointingActivity;
import com.robam.steamoven.ui.activity.AuxModelWorkActivity;
import com.robam.steamoven.ui.activity.ModelWorkActivity;
import com.robam.steamoven.ui.activity.MultiWorkActivity;

import java.util.ArrayList;
import java.util.List;

public class SkipUtil {

    /**
     * 跳转到指定业务页面
     * @param steamOven
     */
    public static void toWorkPage(SteamOven steamOven, Activity activity){
        if(steamOven.mode == 0){
            return;
        }
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE:
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT://预约页面
                HomeSteamOven.getInstance().orderTime = steamOven.orderLeftTime;
                Intent appointIntent = new Intent(activity, AppointingActivity.class);
                MultiSegment segment = MultiSegmentUtil.getSkipResult(steamOven);
                segment.workRemaining = steamOven.orderLeftTime;
                appointIntent.putExtra(Constant.SEGMENT_DATA_FLAG, segment);
                activity.startActivity(appointIntent);
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                if(steamOven.restTimeH == 0 && steamOven.restTime == 0){
                    return;
                }
                //辅助模式工作页面
                if(SteamModeEnum.isAuxModel(steamOven.mode)){
                    Intent intent = new Intent(activity, AuxModelWorkActivity.class);
                    intent.putExtra(Constant.SEGMENT_DATA_FLAG,MultiSegmentUtil.getSkipResult(steamOven));
                    activity.startActivity(intent);
                    return;
                }

                Intent intent;
                List<MultiSegment> list;
                if(steamOven.sectionNumber >= 2 && steamOven.recipeId == 0){
                    //多段工作页面
                    intent = new Intent(activity, MultiWorkActivity.class);
                    list = getMultiWorkResult(steamOven);
                }else{
                    //基础模式工作页面
                    intent = new Intent(activity, ModelWorkActivity.class);
                    list = new ArrayList<>();
                    list.add(MultiSegmentUtil.getSkipResult(steamOven));
                }
                intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                activity.startActivity(intent);
                break;
        }
    }


    /**
     * 获取多段数据集合
     * @return
     */
    private static List<MultiSegment> getMultiWorkResult(SteamOven steamOven){
        List<MultiSegment> multiSegments = new ArrayList<>();
        for(int i = 0;i < steamOven.sectionNumber;i++){
            multiSegments.add(MultiSegmentUtil.getCurSegment(steamOven,i+1));
        }
        return multiSegments;
    }


}
