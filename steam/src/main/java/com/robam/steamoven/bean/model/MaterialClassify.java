package com.robam.steamoven.bean.model;

import com.robam.steamoven.bean.Material;

import java.io.Serializable;
import java.util.List;

//食材分类
public class MaterialClassify implements Serializable {
    public List<Material> main;

    public List<Material> accessory;
}
