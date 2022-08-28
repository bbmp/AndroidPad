package com.robam.stove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

//灶具功能
public class StoveFunBean {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public List<ModeBean> mode ;
    public String into ;

    private StoveFunBean() {

    }

}
