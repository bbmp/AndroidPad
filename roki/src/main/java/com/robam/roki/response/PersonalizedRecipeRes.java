package com.robam.roki.response;

import com.robam.common.bean.BaseResponse;
import com.robam.roki.bean.Recipe;
import com.robam.roki.bean.Recipe3rd;

import java.util.List;

public class PersonalizedRecipeRes extends BaseResponse {
    public List<Recipe> cookbooks;

    public List<Recipe3rd> cookbook_3rds;
}
