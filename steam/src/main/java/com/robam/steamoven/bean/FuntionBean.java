package com.robam.steamoven.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 首页功能bean
 */
public class FuntionBean implements Serializable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String into ;
    public ArrayList<ModeBean> mode;


    public int getFuntionCode() {
        return funtionCode;
    }

    public void setFuntionCode(int funtionCode) {
        this.funtionCode = funtionCode;
    }

    public String getFuntionName() {
        return funtionName;
    }

    public void setFuntionName(String funtionName) {
        this.funtionName = funtionName;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public String getInto() {
        return into;
    }

    public void setInto(String into) {
        this.into = into;
    }


}