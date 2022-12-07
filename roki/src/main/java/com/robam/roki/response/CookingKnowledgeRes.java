package com.robam.roki.response;

import com.robam.common.bean.BaseResponse;
import com.robam.roki.bean.CookingKnowledge;

import java.util.List;

public class CookingKnowledgeRes extends BaseResponse {
    private List<CookingKnowledge> cookingKnowledges;

    public List<CookingKnowledge> getCookingKnowledges() {
        return cookingKnowledges;
    }
}
