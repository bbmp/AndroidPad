package com.robam.stove.response;

import com.robam.common.bean.BaseResponse;
import com.robam.stove.bean.StoveCurveInfo;

import java.util.List;

public class GetCurveCookbooksRes extends BaseResponse {
    public List<StoveCurveInfo> payload;
}
