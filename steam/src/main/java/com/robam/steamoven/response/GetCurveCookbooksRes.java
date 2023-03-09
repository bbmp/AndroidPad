package com.robam.steamoven.response;

import com.robam.common.bean.BaseResponse;
import com.robam.steamoven.bean.SteamCurveDetail;

import java.util.List;

public class GetCurveCookbooksRes extends BaseResponse {
    public List<SteamCurveDetail> data;
}
