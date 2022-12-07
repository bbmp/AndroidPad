package com.robam.stove.request;

import com.google.gson.Gson;

public class RecipeSearchReq {
    public String name;
//    public boolean contain3rd;
//    public long userId;
//    public boolean notNeedSearchHistory;
//    public boolean needStatisticCookbook;

    public RecipeSearchReq(String name, boolean contain3rd, long userId, boolean notNeedSearchHistory, boolean needStatisticCookbook) {
        this.name = name;
//        this.contain3rd = contain3rd;
//        this.userId = userId;
//        this.notNeedSearchHistory = notNeedSearchHistory;
//        this.needStatisticCookbook = needStatisticCookbook;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, RecipeSearchReq.class);
    }
}
