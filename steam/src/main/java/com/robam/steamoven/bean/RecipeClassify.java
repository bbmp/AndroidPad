package com.robam.steamoven.bean;

import java.util.List;

public class RecipeClassify {
    public String recipeClassify;
    public List<RecipesDTO> recipes;

    public static class RecipesDTO {
        public int recipeId;
        public String recipeName;
        public String recipeImg;
        public String qrCode;
        public List<WorkModesDTO> workModes;
        public List<FoodsDTO> foods;
        public List<StepsDTO> steps;

        public static class WorkModesDTO {
            public String mode;
            public int defTemp;
            public int defTime;
            public int minTime;
            public int maxTime;
        }

        public static class FoodsDTO {
            public String mode;
            public String unit;
            public int num;
        }

        public static class StepsDTO {
            public int step;
            public String stepDes;
            public String setpImg;
        }
    }
}
