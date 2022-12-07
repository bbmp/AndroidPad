package com.robam.steamoven.bean;

import java.io.Serializable;

//食材
public class Material implements Serializable {
    public String name;
    //重量
    public String standardWeight;
    //单位
    public String standardUnit;

    public String getName() {
        return name;
    }

    public String getStandardWeight() {
        return standardWeight;
    }

    public String getStandardUnit() {
        return standardUnit;
    }
}
