package com.robam.stove.request;

import com.google.gson.Gson;

public class GetRecipesByDeviceReq {
    public String dc;
    public String recipeType;
    public int start;
    public int limit;

    public GetRecipesByDeviceReq(String dc, String recipeType, int start, int limit) {
        this.dc = dc;
        this.recipeType = recipeType;
        this.start = start;
        this.limit = limit;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetRecipesByDeviceReq.class);
    }
}
