package com.robam.roki.request;

import com.google.gson.GsonBuilder;

public class TagOtherThemeReq {
    public Long cookbookTagId;

    public boolean needStatisticCookbook;

    public int pageNo;

    public int pageSize;

    public int type;

    public TagOtherThemeReq(Long cookbookTagId, boolean needStatisticCookbook, int pageNo, int pageSize, int type) {
        this.cookbookTagId = cookbookTagId;
        this.needStatisticCookbook = needStatisticCookbook;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.type = type;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, TagOtherThemeReq.class);
    }
}
