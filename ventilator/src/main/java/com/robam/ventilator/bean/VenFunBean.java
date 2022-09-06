package com.robam.ventilator.bean;

import androidx.annotation.IdRes;

//功能
public class VenFunBean {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public int iconRes ;
    public String into ;

    public VenFunBean(int funtionCode, String funtionName, String backgroundImg, int res, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.iconRes = res;
        this.into = into;
    }
}
