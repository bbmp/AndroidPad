package com.robam.steamoven.bean.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : huxw
 *     e-mail : xwhu93@163.com
 *     time   : 2022/05/26
 *     desc   : 规格下对应的模式参数
 *     version: 1.0
 * </pre>
 */
public class RecipeWorkMode extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;
    /**
     * 模式
     */
    @Column
    public int mode;
    /**
     * 模式名
     */
    @Column
    public String mode_name;
    /**
     * 默认温度
     */
    @Column
    public int temp;
    /**
     * 默认时间
     */
    @Column
    public int cook_time;
    /**
     * 可选择最小时间
     */
    @Column
    public int min_time;
    /**
     * 可选择最大时间
     */
    @Column
    public int max_time;

    public RecipeWork recipeWork ;

    /**
     * 获取时间范围
     * @return
     */
    public List<Integer> getTimeData(){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = min_time ; i <= max_time ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }

    /**
     * 默认时间index
     * @return
     */
    public int getDefTimeIndex(){
        return cook_time - min_time ;
    }
}
