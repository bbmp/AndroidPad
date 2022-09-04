package com.robam.dishwasher.bean;

import java.io.Serializable;

public class DishWaherModeBean implements Serializable {
    //模式id
    public int code ;
    //模式名称
    public String name ;

    public String backgroundImg ;
    //功能描述
    public String desc ;
    //跳转
    public String into ;
    //时间
    public int time;
    //温度
    public int temp;
}
