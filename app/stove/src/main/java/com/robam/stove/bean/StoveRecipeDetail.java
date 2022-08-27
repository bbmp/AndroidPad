package com.robam.stove.bean;

import java.util.List;

public class StoveRecipeDetail {
    public long id;
    //名字
    public String name;
    //总时间
    public int needTime;
    //图片
    public String imgSmall;
    //步骤
    public List<RecipeStep> steps;
    //食材分类
    public MaterialClassify materials;


}
