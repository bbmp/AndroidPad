package com.robam.steam.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜谱模式
 */
public class RecipeMode extends LitePalSupport {
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
     * 烹饪模式
     */
    @Column
    public int mode;
    /**
     * 烹饪温度
     */
    @Column
    public int temp;
    /**
     * 烹饪时间
     */
    @Column
    public int cook_time;

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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getCook_time() {
        return cook_time;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }

    public List<Integer> getTimes(){
        ArrayList<Integer> integers = new ArrayList<>();
        int i = cook_time - 2;
        int i1 = cook_time + 2;
        while (i <= i1){
            integers.add(i++);
        }
//        integers.add(cook_time);
        return  integers ;
    }
}
