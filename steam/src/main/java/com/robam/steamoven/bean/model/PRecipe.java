package com.robam.steamoven.bean.model;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * <pre>
 *     author : huxw
 *     e-mail : xwhu93@163.com
 *     time   : 2022/05/26
 *     desc   : P档菜谱
 *     version: 1.0
 * </pre>
 */
public class PRecipe extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id;
    /**
     * 菜谱功能
     */
    @Column
    public String function;
    /**
     * 菜谱id 对应P1 P2
     */
    @Column
    public int recipe_id;
    /**
     * 所属分类名
     */
    @Column
    public String recipe_class_name;
    /**
     * 所属分类名
     */
    @Column
    public String recipe_name;
    /**
     * 主食材
     */
    @Column
    public String main_food;
    /**
     * 菜谱描述
     */
    @Column
    public String recipe_des;
    /**
     * 菜谱img
     */
    @Column
    public String recipe_img;
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
     * 放置位置
     */
    @Column
    public String recommend_site;
    /**
     * 预热提醒
     */
    @Column
    public int preheat_warn;
    /**
     * 切换提醒
     */
    @Column
    public int warning_tone;
    /**
     * 规格下对应的模式参数
     */
    public List<RecipeWork> recipe_work;


    public List<RecipeWork> getRecipe_work() {
        List<RecipeWork> recipeWorks = LitePal.where("precipe_id = ?", id + "").find(RecipeWork.class);
        return recipeWorks;
    }

    /**
     * 根据P档菜谱名称查询菜谱详情
     * @return
     */
    public RecipeDetail getRecipeDetail() {
        RecipeDetail recipeDetail = LitePal.where("recipeName = ?", recipe_name).findFirst(RecipeDetail.class);
        return recipeDetail;
    }

}
