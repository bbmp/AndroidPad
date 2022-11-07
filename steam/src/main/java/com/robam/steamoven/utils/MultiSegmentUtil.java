package com.robam.steamoven.utils;

import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamModeEnum;

public class MultiSegmentUtil {

    public static MultiSegment getCurSegment(SteamOven steamOven, int index){
        MultiSegment segment = new MultiSegment();
        if(steamOven.curSectionNbr == index){
            segment.setCookState(MultiSegment.COOK_STATE_START);
        }
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
                int restTime = steamOven.restTime * 256 + steamOven.restTimeH;//设置的工作时间 (秒)
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
                int restTime2 = steamOven.restTime2 * 256 + steamOven.restTimeH2;//设置的工作时间 (秒)
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
                int restTime3 = steamOven.restTime3 * 256 + steamOven.restTimeH3;//设置的工作时间 (秒)
                segment.workRemaining = restTime3;
                break;
        }
        return segment;
    }
}
