package com.robam.stove.response;

import com.robam.common.bean.BaseResponse;
import com.robam.stove.bean.StoveRecipe;

import java.util.List;

public class GetRecipesByDeviceRes extends BaseResponse {
    public List<StoveRecipe> cookbooks;
}
