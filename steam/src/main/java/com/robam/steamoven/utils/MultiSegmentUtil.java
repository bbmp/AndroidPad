package com.robam.steamoven.utils;

import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;

public class MultiSegmentUtil {

    /**
     * 获取多段数据
     * @param steamOven
     * @param index
     * @return
     */
    public static MultiSegment getCurSegment(SteamOven steamOven, int index){
        MultiSegment segment = new MultiSegment();
        if(steamOven.curSectionNbr == index){
            segment.setCookState(MultiSegment.COOK_STATE_START);
        }
        segment.recipeId = steamOven.recipeId;
        switch (index){
            case 0:
            case 1:
                segment.code = steamOven.mode;
                segment.model = SteamModeEnum.match(steamOven.mode);
                segment.defTemp = steamOven.setUpTemp;
                segment.downTemp = steamOven.setDownTemp;
                int time = steamOven.setTimeH * 256 + steamOven.setTime;//设置的工作时间 (秒)
                segment.duration = time/60 + (time%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam;
                int restTime = steamOven.restTimeH*256 + steamOven.restTime;//设置的工作时间 (秒)
                segment.workRemaining = restTime;
                break;
            case 2:
                segment.code = steamOven.mode2;
                segment.model = SteamModeEnum.match(steamOven.mode2);
                segment.defTemp = steamOven.setUpTemp2;
                segment.downTemp = steamOven.setDownTemp2;
                int time2 = steamOven.setTimeH2 * 256 + steamOven.setTime2;//设置的工作时间 (秒)
                segment.duration = time2/60 + (time2%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam2;
                int restTime2 = steamOven.restTimeH2 * 256 + steamOven.restTime2;//设置的工作时间 (秒)
                segment.workRemaining = restTime2;
                break;
            case 3:
                segment.code = steamOven.mode3;
                segment.model = SteamModeEnum.match(steamOven.mode3);
                segment.defTemp = steamOven.setUpTemp3;
                segment.downTemp = steamOven.setDownTemp3;
                int time3 = steamOven.setTimeH3 * 256 + steamOven.setTime3;//设置的工作时间 (秒)
                segment.duration = time3/60 + (time3%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam3;
                int restTime3 = steamOven.restTimeH3 * 256 + steamOven.restTime3;//设置的工作时间 (秒)
                segment.workRemaining = restTime3;
                break;
        }
        return segment;
    }

    /**
     * 获取当前运行模式数据对象
     * @param steamOven
     * @return
     */
    public static MultiSegment getSkipResult(SteamOven steamOven){
        MultiSegment segment = new MultiSegment();
        segment.code = steamOven.mode;
        segment.model = "";
        segment.steam = steamOven.steam;
        segment.defTemp = steamOven.curTemp;
        segment.downTemp = steamOven.setDownTemp;
        int setTime = steamOven.setTimeH * 256 + steamOven.setTime;
        segment.duration = setTime/60 + (setTime%60 == 0 ? 0 : 1);

        int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
        int restTimeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
        segment.workRemaining =restTimeF*60;

        segment.recipeId = steamOven.recipeId;

        boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
        segment.setWorkModel(isPreHeat?MultiSegment.COOK_STATE_PREHEAT:MultiSegment.WORK_MODEL_);

        boolean isWorking = steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_WORKING;
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        return segment;
    }
}
