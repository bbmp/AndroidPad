package com.robam.steamoven.bean;

import java.io.Serializable;

/**
 * 多段
 */
public class MultiSegment implements Serializable {
    //段数
    public int no;
    //模式
    public String model;
    //时长
    public String duration;
    //温度
    public String temperature;

    public int modelNum = 0;

}
