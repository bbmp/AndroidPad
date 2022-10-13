package com.robam.steamoven.bean.model;

import java.io.Serializable;
import java.util.List;

//step参数
public class StepParams implements Serializable {
    public String deviceCategory;
    public List<Params> params;

    //挡位参数
    public class Params implements Serializable{
        public String code;
        public int value;  //
        public String valueName;
    }
}
