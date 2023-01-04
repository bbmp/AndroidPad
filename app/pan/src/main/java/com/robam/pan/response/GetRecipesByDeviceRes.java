package com.robam.pan.response;

import com.robam.common.bean.BaseResponse;
import com.robam.pan.bean.PanRecipe;

import java.util.List;

public class GetRecipesByDeviceRes extends BaseResponse {
    public List<PanRecipe> data;
}
