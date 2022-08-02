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
 *     desc   : 菜谱工作下可选择的规格
 *     version: 1.0
 * </pre>
 */
public class RecipeWork extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;
    /**
     * 可选规格
     */
    @Column
    public String recipe_norms;

    public PRecipe pRecipe ;
    /**
     * 规格下对应的相关模式
     */
    public List<RecipeWorkMode> recipe_work_mode;

    public List<RecipeWorkMode> getRecipe_work_mode() {
        List<RecipeWorkMode> recpeModes = LitePal.where("recipework_id = ?", id + "").find(RecipeWorkMode.class);
        return recpeModes;
    }

    /**
     * 获取总的默认时间 每段都有默认时间长度
     * @return
     */
    public int getWorkTime() {
        return LitePal.where("recipework_id = ?", id + "").sum(RecipeWorkMode.class ,"cook_time" ,int.class);
    }

}
