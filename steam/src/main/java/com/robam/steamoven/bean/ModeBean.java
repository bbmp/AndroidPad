package com.robam.steamoven.bean;

import org.litepal.annotation.Column;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 各模式对应参数根据功能说明书修改 ， code根据电源板协议对应
 */
public class ModeBean implements Serializable {
    @Column
    public int funCode ;
    /**
     * 协议对应code
     */
    @Column
    public int code;
    /**
     * 模式名
     */
    @Column
    public String name;
    /**
     * action
     */
    @Column
    public String into;
    /**
     * 默认温度
     */
    @Column
    public int defTemp;
    /**
     * 模式时间
     */
    @Column
    public int defTime;
    /**
     * 最小温度
     */
    @Column
    public int minTemp;
    /**
     * 最大温度
     */
    @Column
    public int maxTemp;
    /**
     * 最小时间
     */
    @Column
    public int minTime;
    /**
     * 最大时间
     */
    @Column
    public int maxTime;

    /**
     * 可设置最小蒸汽量
     */
    public int minSteam ;
    /**
     * 可设置最大蒸汽量
     */
    public int maxSteam ;
    /**
     * 模式默认蒸汽量
     */
    public int defSteam ;

    /**
     * 是否可以开门工作
     */
    @Column
    public int openDoorWork ;
    /**
     * 是否需要水箱
     */
    @Column
    public int needWater ;
    /**
     * 是否可以加蒸汽 0：不可以 1：可以
     */
    @Column
    public int addSteam ;

    /**
     * 是否支持旋转烤
     */
    @Column
    public int rotate ;

    /**
     * 是否支持多段
     */
    @Column
    public int mult ;

    /**
     * 是否支持预约
     */
    @Column
    public int order ;

    @Column
    public int defDownTemp;

    @Column
    public int minDownTemp;

    @Column
    public int maxDownTemp;

    public int getDefDownTemp(int selectTemp){
        return selectTemp - 20 > minTemp ? selectTemp - 20 : minTemp ;
    }

    /**
     * 默认温度index
     * @return
     */
    public int getDefTempIndex(){
        return defTemp - minTemp ;
    }

    /**
     * 默认时间index
     * @return
     */
    public int getDefTimeIndex(){
        return defTime - minTime ;
    }

    /**
     * 默认蒸汽量index
     * @return
     */
    public int getDefSteamIndex(){
        return defSteam - minSteam ;
    }


    /**
     * 获取modo温度范围
     * @return
     */
    public List<Integer> getTempData(){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = minTemp ; i <= maxTemp ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }

    /**
     * 获取modo下管温度范围
     * @param selectTemp 选择的上温度
     * @return
     */
    public  List<Integer> getDownTempData(int selectTemp){
        int temp1 = selectTemp - 20 < minTemp ? minTemp : selectTemp - 20  ;
        int temp2 = selectTemp + 20 > maxTemp ? maxTemp : selectTemp + 20  ;
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = temp1 ; i <= temp2 ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }


    /**
     * 获取时间范围
     * @return
     */
    public  List<Integer> getTimeData(){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = minTime ; i <= maxTime ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }

    /**
     * 获取蒸汽范围
     * @return
     */
    public  List<Integer> getSteamData(){
        ArrayList<Integer> tempData = new ArrayList<>();
        for (int i = minSteam ; i <= maxSteam ; i ++ ){
            tempData.add(i);
        }
        return tempData ;
    }

}
