package com.robam.steamoven.device;

public interface SteamFunction {
    void shutDown(String targetGuid);

    void powerOn(String targetGuid);

    void orderWork();

    void stopWork();

    void startWork();

    void pauseWork(String targetGuid);

    void continueWork(String targetGuid);

    //查询一体机状态
    void queryAttribute(String targetGuid);
}
