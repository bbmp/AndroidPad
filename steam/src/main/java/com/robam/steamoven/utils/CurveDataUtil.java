package com.robam.steamoven.utils;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class CurveDataUtil {
    private static List entryList = new ArrayList<Entry>();//数据集合

    public static void initList(ArrayList<Entry> list){
        entryList.clear();
        entryList.addAll(list);
    }


    public static List getEntryList() {
        return entryList;
    }
}
