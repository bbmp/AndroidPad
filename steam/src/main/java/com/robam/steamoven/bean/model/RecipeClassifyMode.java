package com.robam.steamoven.bean.model;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class RecipeClassifyMode extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id;
    /**
     * 主键
     */
    @Column
    public String className;

    public List<Recipe> recipes;

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = LitePal.where("recipe_class_id = ?", id + "").find(Recipe.class);
        return recipes;
    }
}
