package com.robam.pan.bean;

import java.io.Serializable;
import java.util.List;

public class PanRecipeDetail implements Serializable {
    //菜谱id
    public long id;
    //名字
    public String name;
    //总时间
    public int needTime;
    //图片
    public String imgCover11;
    //步骤
    public List<RecipeStep> stepRespDtoList;
    //食材分类
    public List<Material> materialDtoList;
    //调味料
    public List<Material> condimentDtoList;
    //曲线数据
    public CurveCookbook curveCookbookDto;

}
