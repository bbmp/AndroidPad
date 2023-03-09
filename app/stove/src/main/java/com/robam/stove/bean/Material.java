package com.robam.stove.bean;

import java.io.Serializable;

//食材
public class Material implements Serializable {
    public String materialName;
    //重量
    public String standardWeight;
    //单位
    public String standardUnitName;

    public String getName() {
        return materialName;
    }

    public String getStandardWeight() {
        return standardWeight;
    }

    public String getStandardUnit() {
        return standardUnitName;
    }
}
