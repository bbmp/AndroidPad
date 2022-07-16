package com.robam.stream.device;

public interface SteamFunction {
    void shutDown();

    void powerOn();

    void orderWork();

    void stopWork();

    void startWork();

    void pauseWork();

    void continueWork();
}
