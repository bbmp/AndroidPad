package com.robam.steamoven.bean.model;

import java.io.Serializable;
import java.util.List;

public class StoveRecipeDetail implements Serializable {
    //菜谱id
    public long id;
    //名字
    public String name;
    //总时间
    public int needTime;
    //图片
    public String imgSmall;
    //步骤
    public List<RecipeStep2> steps;
    //食材分类
    public MaterialClassify materials;


}