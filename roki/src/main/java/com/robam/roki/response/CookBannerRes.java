package com.robam.roki.response;

import com.robam.common.bean.BaseResponse;
import com.robam.roki.bean.CookBanner;

import java.util.List;

public class CookBannerRes extends BaseResponse {
    public int code;
    public String message;
    public boolean success;
    public List<CookBanner> data;
    public int pageIndex;
    public int pageSize;
    public int total;
    public int totalPage;

}
