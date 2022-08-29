package com.robam.pan.bean;

import java.io.Serializable;
import java.util.List;

public class PanRecipeDetail implements Serializable {
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
