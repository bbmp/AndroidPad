package com.robam.steamoven.bean.model;

import com.robam.steamoven.bean.model.Recipe;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 食材
 */
public class RecipeMaterialBean extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;
    /**
     * 菜谱id
     */
    @Column
    public int recipe_id ;
    /**
     * 食材名称
     */
    @Column
    public String material_name ;
    /**
     * 食材数量
     */
    @Column
    public int material_num ;
    /**
     * 食材单位
     */
    @Column
    public String material_unit ;

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

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public int getMaterial_num() {
        return material_num;
    }

    public void setMaterial_num(int material_num) {
        this.material_num = material_num;
    }

    public String getMaterial_unit() {
        return material_unit;
    }

    public void setMaterial_unit(String material_unit) {
        this.material_unit = material_unit;
    }
}
