package com.robam.dishwasher.bean;

import java.io.Serializable;

public class DishWasherAuxBean implements Serializable {
    //模式id
    public short code ;
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

    /**
     * 附加code（锅具强洗/加强除菌/长效净存/下层洗）
     */
    public int auxCode = -1;

    public DishWasherAuxBean getNewMode(){
        DishWasherAuxBean modeBean = new DishWasherAuxBean();
        modeBean.code = code;
        modeBean.name = name;
        modeBean.backgroundImg = backgroundImg;
        modeBean.desc = desc;
        modeBean.into = into;
        modeBean.temp = temp;
        modeBean.time = time;
        modeBean.auxCode = auxCode;
        return modeBean;
    }

}
