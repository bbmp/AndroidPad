package com.robam.steamoven.bean.model;

import com.robam.steamoven.bean.RecipeMode;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Recipe extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;
    /**
     * 菜谱id
     */
    @Column
    public int recipe_id;
    /**
     * 菜谱分类id
     */
    @Column
    public int recipe_class_id;
    /**
     * 菜谱名
     */
    @Column
    public String recipe_name;
    /**
     * 主要食材
     */
    @Column
    public String main_food;
    /**
     * 菜谱描述
     */
    @Column
    public String recipe_des;
    /**
     * 菜谱图片
     */
    @Column
    public String recipe_img;
    /**
     * 模式
     */
    @Column
    public int mode;
    /**
     * 温度
     */
    @Column
    public int temp;
    /**
     * 时间
     */
    @Column
    public int cook_time;
    /**
     * 二维码
     */
    @Column
    public String qr_code;
    /**
     * 工具
     */
    @Column
    public String tool;
    /**
     * 推荐位置
     */
    @Column
    public String recommend_site;
    /**
     * 曲线id
     */
    @Column
    public int carve_id;

    public RecipeClassifyMode recipeClassifyMode ;

    public List<RecipeMode> recipe_mode;
    public List<RecipeMaterialBean> recipe_materials;
    public List<RecipeStep2> recipe_steps;

    public RecipeClassifyMode getRecipeClassifyMode() {
        return recipeClassifyMode;
    }

    public void setRecipeClassifyMode(RecipeClassifyMode recipeClassifyMode) {
        this.recipeClassifyMode = recipeClassifyMode;
    }

    public List<RecipeMode> getRecipe_mode() {
        List<RecipeMode> modes = LitePal.where("recipe_id = ?", id + "").find(RecipeMode.class);
        return modes;
    }

    public List<RecipeMaterialBean> getRecipe_materials() {
        List<RecipeMaterialBean> materialBeans = LitePal.where("recipe_id = ?", recipe_id + "").find(RecipeMaterialBean.class);
        return materialBeans;
    }

    public List<RecipeStep2> getRecipe_steps() {
        List<RecipeStep2> recipeSteps = LitePal.where("recipe_id = ?", recipe_id + "").find(RecipeStep2.class);
        return recipeSteps;
    }
}
