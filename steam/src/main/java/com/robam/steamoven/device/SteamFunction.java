package com.robam.steamoven.device;

public interface SteamFunction {
    void shutDown(String... args);

    void powerOn(String... args);

    void orderWork();

    void stopWork();

    void startWork();

    void pauseWork();

    void continueWork();
}
