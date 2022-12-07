package com.robam.roki.utils;

import com.robam.roki.bean.AbsRecipe;
import com.robam.roki.bean.Recipe;

public class RecipeUtils {
    /**
     * 获取菜谱展示图片
     *
     * @param recipe
     * @return
     */
    public static String getRecipeImgUrl(AbsRecipe recipe) {
        if (recipe instanceof Recipe) {
            Recipe rokiRecipe = (Recipe) recipe;
            if (rokiRecipe.imgLarge != null && !rokiRecipe.imgLarge.isEmpty()) {
                return rokiRecipe.imgLarge;
            }

            if (rokiRecipe.imgMedium != null && !rokiRecipe.imgMedium.isEmpty()) {
                return rokiRecipe.imgMedium;
            }

            if (rokiRecipe.imgSmall != null && !rokiRecipe.imgSmall.isEmpty()) {
                return rokiRecipe.imgSmall;
            }

            if (rokiRecipe.imgPoster != null) {
                return rokiRecipe.imgPoster;
            }
        }


        return "";

    }
}
