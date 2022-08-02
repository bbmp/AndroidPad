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
    public void shutDown() {
        function.shutDown();
    }

    @Override
    public void powerOn() {
        function.powerOn();
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
    public void pauseWork() {
        function.pauseWork();
    }

    @Override
    public void continueWork() {
        function.continueWork();
    }
}
