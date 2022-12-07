package com.robam.roki.request;

import com.google.gson.GsonBuilder;

public class CookbookbythemeIdReq {
    public String lang;

    public long limit;

    public int start;

    public int themeId;

    public boolean needStatisticCookbook;

    public CookbookbythemeIdReq(String lang, long limit, int start, int themeId) {
        this.lang = lang;
        this.limit = limit;
        this.start = start;
        this.themeId = themeId;
        this.needStatisticCookbook = false;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CookbookbythemeIdReq.class);
    }
}
