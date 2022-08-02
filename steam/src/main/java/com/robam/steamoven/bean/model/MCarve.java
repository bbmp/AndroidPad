package com.robam.steamoven.bean.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 自己保存的曲线数据
 */
public class MCarve extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;

    /**
     * 曲线名称
     */
    @Column
    public String carve_name;
    /**
     * 曲线的json字符串
     */
    @Column
    public String carve_data;

    /**
     * 工作模式的JSON字符串
     */
    public String mode_data ;
}
