package com.robam.pan.response;

import com.robam.common.bean.BaseResponse;
import com.robam.pan.bean.PPanRecipe;

import java.util.List;

//获取p档菜谱
public class GetPRecipeRes extends BaseResponse {
    public List<PPanRecipe> data;
}
