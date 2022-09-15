package com.robam.steamoven.device;

public class HomeSteamOven {
    //当前进入的一体机
    public static HomeSteamOven getInstance() {
        return HomeSteamOven.SteanOvenHolder.instance;
    }
    private static class SteanOvenHolder {
        private static final HomeSteamOven instance = new HomeSteamOven();
    }

    public String guid;
}
