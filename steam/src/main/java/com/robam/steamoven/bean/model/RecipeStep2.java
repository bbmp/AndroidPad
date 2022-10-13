package com.robam.steamoven.bean.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 菜谱步骤
 */
public class RecipeStep2 extends LitePalSupport {
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
     * 步骤顺序
     */
    @Column
    public int step_no;
    /**
     * 步骤描述
     */
    @Column
    public String step_des;
    /**
     * 步骤图片
     */
    @Column
    public String step_img;
    /**
     * 是否备菜步骤
     */
    @Column
    public int e_prepare ;

    /**
     * 入表时间
     */
    @Column
    public long inset_time;
    /**
     * 更新时间
     */
    @Column
    public long update_time;

    public Recipe recipe ;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getStep_des() {
        return step_des;
    }

    public void setStep_des(String step_des) {
        this.step_des = step_des;
    }

    public String getStep_img() {
        return step_img;
    }

    public void setStep_img(String step_img) {
        this.step_img = step_img;
    }

    public int getE_prepare() {
        return e_prepare;
    }

    public void setE_prepare(int e_prepare) {
        this.e_prepare = e_prepare;
    }

    public long getInset_time() {
        return inset_time;
    }

    public void setInset_time(long inset_time) {
        this.inset_time = inset_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}