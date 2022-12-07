package com.robam.pan.request;

import com.google.gson.Gson;

public class RecipeSearchReq {
    public String name;


    public RecipeSearchReq(String name, boolean contain3rd, long userId, boolean notNeedSearchHistory, boolean needStatisticCookbook) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, RecipeSearchReq.class);
    }
}
