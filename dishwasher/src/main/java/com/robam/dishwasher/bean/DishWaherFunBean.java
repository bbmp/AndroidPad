package com.robam.dishwasher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DishWaherFunBean implements Serializable {
    //功能id
    public int funtionCode ;
    //功能名称
    public String funtionName ;
    public String backgroundImg ;
    //功能描述
    public String desc ;
    //跳转
    public String into ;
    //模式
    public DishModeBean mode;
}
