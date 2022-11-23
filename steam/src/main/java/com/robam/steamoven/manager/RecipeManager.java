package com.robam.steamoven.manager;


import java.util.HashMap;
import java.util.Map;

public class RecipeManager {
    private static class Holder {
        private static RecipeManager instance = new RecipeManager();
    }

    public static RecipeManager getInstance() {
        return RecipeManager.Holder.instance;
    }

    private Map<Integer,String> recipeName = new HashMap<>();


}
