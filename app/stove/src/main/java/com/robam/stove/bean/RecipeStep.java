package com.robam.stove.bean;

public class RecipeStep {
    /**
     * 步骤描述
     */
    public String stepDesc;
    /**
     * 步骤
     */
    public int stepNo;
    /**
     * 步骤时间
     */
    public String stepTime;

    public RecipeStep(String stepDesc, int stepNo, String stepTime) {
        this.stepDesc = stepDesc;
        this.stepNo = stepNo;
        this.stepTime = stepTime;
    }
}
