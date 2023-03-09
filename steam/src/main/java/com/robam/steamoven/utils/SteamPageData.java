package com.robam.steamoven.utils;

import androidx.lifecycle.MutableLiveData;

public class SteamPageData {

    private MutableLiveData<BusData> bsData = new MutableLiveData<BusData>(null);
    private static class Holder {
        private static SteamPageData instance = new SteamPageData();
    }
    public static SteamPageData getInstance() {
        return SteamPageData.Holder.instance;
    }

    public MutableLiveData<BusData> getBsData() {
        return bsData;
    }

    public static class BusData{

          public BusData(long bsCode,String content,Object bsObj){
              this.bsCode = bsCode;
              this.content = content;
              this.bsObj = bsObj;
          }

        public long bsCode;
        public String content;
        public Object bsObj;
    }
}
