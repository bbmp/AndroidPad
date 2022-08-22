package com.robam.stove.bean;

public class RecipeMaterial {
    private String name;
    private int num;
    private String unit;

    public RecipeMaterial(String name, int num, String unit) {
        this.name = name;
        this.num = num;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public String getUnit() {
        return unit;
    }
}
