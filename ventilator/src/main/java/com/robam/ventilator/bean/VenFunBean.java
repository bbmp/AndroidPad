package com.robam.ventilator.bean;

//功能
public class VenFunBean {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    public VenFunBean(int funtionCode, String funtionName, String backgroundImg, String mode, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.mode = mode;
        this.into = into;
    }
}
