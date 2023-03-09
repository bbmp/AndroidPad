package com.robam.pan.response;

import com.robam.common.bean.BaseResponse;
import com.robam.pan.bean.PanCurveDetail;

import java.util.List;

public class GetCurveCookbooksRes extends BaseResponse {
    public List<PanCurveDetail> data;
}
