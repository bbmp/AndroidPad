package com.robam.steamoven.device;

//控制层切换，本地控制和远程被控制
public class SteamAbstractControl implements SteamFunction {

    private SteamFunction function;
    private static SteamAbstractControl instance = new SteamAbstractControl();


    public static SteamAbstractControl getInstance() {
        return instance;
    }

    public void init(SteamFunction steamFunction) {
        function = steamFunction;
    }
    @Override
    public void shutDown(String targetGuid) {
        function.shutDown(targetGuid);
    }

    @Override
    public void powerOn(String targetGuid) {
        function.powerOn(targetGuid);
    }

    @Override
    public void orderWork() {
        function.orderWork();
    }

    @Override
    public void stopWork() {
        function.stopWork();
    }

    @Override
    public void startWork() {
        function.startWork();
    }

    @Override
    public void pauseWork(String targetGuid) {
        function.pauseWork(targetGuid);
    }

    @Override
    public void continueWork(String targetGuid) {
        function.continueWork(targetGuid);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }
}
