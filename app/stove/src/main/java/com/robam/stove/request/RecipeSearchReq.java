package com.robam.stove.request;

import com.google.gson.Gson;

public class RecipeSearchReq {
    public String devicePlat;
    public boolean needSearchHistory;
    public int pageIndex;
    public int pageSize;
    public String search;
    public int searchType;
    public long userId;
//    public boolean contain3rd;
//    public long userId;
//    public boolean notNeedSearchHistory;
//    public boolean needStatisticCookbook;


    public RecipeSearchReq(String devicePlat, boolean needSearchHistory, int pageIndex, int pageSize, String search, int searchType, long userId) {
        this.devicePlat = devicePlat;
        this.needSearchHistory = needSearchHistory;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.search = search;
        this.searchType = searchType;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, RecipeSearchReq.class);
    }
}
