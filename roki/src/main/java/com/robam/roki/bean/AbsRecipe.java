package com.robam.roki.bean;

public abstract class AbsRecipe {
    public long id;

    public String name;



    /**
     * 供应商ID
     */
    public long type;  // 0 roki 5 下厨房 6 味库 7 豆果

    /**
     * 收藏次数
     */
    public int collectCount;

    /**
     * 浏览量
     */
    public int viewCount;

    public void setId(long id) {
        this.id = id;
    }

    /*
     *是否支持配送   by zhaiyuanyi
     */
    public boolean allowDistribution;

    public long getId() {
        return id;
    }
}
