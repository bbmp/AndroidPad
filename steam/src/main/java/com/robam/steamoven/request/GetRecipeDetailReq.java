package com.robam.steamoven.request;

import com.google.gson.Gson;

public class GetRecipeDetailReq {
    private long cookbookId;
    private String entranceCode;
    private String needStepsInfo;


    public GetRecipeDetailReq(long cookbookId, String entranceCode, String needStepsInfo) {
        this.cookbookId = cookbookId;
        this.entranceCode = entranceCode;
        this.needStepsInfo = needStepsInfo;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetRecipeDetailReq.class);
    }
}
