package com.robam.roki.request;

import com.google.gson.GsonBuilder;

public class CookingKnowledgeReq {
    private String typeCode;

    private int isActive;

    private String lable;

    private int pageNo;

    private int pageSize;

    public CookingKnowledgeReq(String typeCode, int isActive, String lable, int pageNo, int pageSize) {
        this.typeCode = typeCode;
        this.isActive = isActive;
        this.lable = lable;
        this.pageNo = pageNo;
        this.pageSize = pageSize;

    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CookingKnowledgeReq.class);
    }
}
