package com.robam.pan.bean;

import java.io.Serializable;

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
