package com.robam.roki.response;

import com.robam.common.bean.BaseResponse;
import com.robam.roki.bean.Recipe;

import java.util.List;

public class WeekTopsRes extends BaseResponse {
    public List<Recipe> payload;
}
