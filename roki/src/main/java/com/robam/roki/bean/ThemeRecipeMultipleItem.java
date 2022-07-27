package com.robam.roki.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ThemeRecipeMultipleItem implements MultiItemEntity {

    public static final int IMG_RECIPE_MSG_TEXT = 5;//菜谱列表
    public static final int IMG_THEME_RECIPE_MSG_TEXT = 6;//精选专题菜谱信息
    public static final int IMG_RECIPE_EMPTY = 7; //占位,否则尾部字体不变
    private int itemType;
    private Recipe recipe;
    private RecipeTheme recipeTheme;

    public ThemeRecipeMultipleItem(int itemType) {
        this.itemType = itemType;
    }

    public ThemeRecipeMultipleItem(int itemType, Recipe recipe) {
        this.itemType = itemType;
        this.recipe = recipe;
    }

    public ThemeRecipeMultipleItem(int itemType, RecipeTheme recipeTheme) {
        this.itemType = itemType;
        this.recipeTheme = recipeTheme;
    }

    public Recipe getRecipe() {
        return recipe;
    }


    public RecipeTheme getRecipeTheme() {
        return recipeTheme;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
