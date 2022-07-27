package com.robam.roki.request;

import com.google.gson.GsonBuilder;

import java.util.List;

public class TagOtherCooksReq {
    public Long cookbookTagId;

    public boolean needStatisticCookbook;

    public int pageNo;

    public int pageSize;

    public int type;

    public List<Long> excludeCookIds;

    public Long userId;

    public TagOtherCooksReq(Long cookbookTagId, boolean needStatisticCookbook, int pageNo, int pageSize, int type) {
        this.cookbookTagId = cookbookTagId;
        this.needStatisticCookbook = needStatisticCookbook;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.type = type;
    }

    public TagOtherCooksReq(Long cookbookTagId, boolean needStatisticCookbook, int pageNo, int pageSize, int type, List<Long> excludeCookIds) {
        this.cookbookTagId = cookbookTagId;
        this.needStatisticCookbook = needStatisticCookbook;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.type = type;
        this.excludeCookIds = excludeCookIds;
    }
    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, TagOtherCooksReq.class);
    }
}
