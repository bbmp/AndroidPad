package com.robam.steam.bean;

import androidx.lifecycle.ViewModel;

public class SteamOvenViewModel extends ViewModel {

    private SteamOven steamOven = SteamOven.getInstance();

    public SteamOven getSteamOven() {
        return steamOven;
    }
}
