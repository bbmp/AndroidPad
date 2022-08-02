package com.robam.steamoven.bean.model;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * <pre>
 *     author : huxw
 *     e-mail : xwhu93@163.com
 *     time   : 2022/06/06
 *     desc   : 菜谱详情（包括资源）
 *     version: 1.0
 * </pre>
 */
public class RecipeDetail extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;
    /**
     * 主食材
     */
    @Column
    public String food;
    /**
     * 主图
     */
    @Column
    public String mainIma;
    /**
     * 佐料
     */
    @Column
    public String material;
    /**
     * 菜谱名称
     */
    @Column
    public String recipeName;

    /**
     * 菜谱步骤
     */
    public List<RecipeStep> step;

    /**
     * 获取步骤
     * @return
     */
    public List<RecipeStep> getRecipe_steps() {
        List<RecipeStep> recipeSteps = LitePal.where("recipedetail_id = ?", id + "").find(RecipeStep.class);
        return recipeSteps;
    }
}
