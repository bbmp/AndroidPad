package com.robam.stove.bean;

import java.io.Serializable;
import java.util.List;

//食材分类
public class MaterialClassify implements Serializable {
    public List<Material> main;

    public List<Material> accessory;
}
