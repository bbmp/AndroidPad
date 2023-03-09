package com.robam.steamoven.bean;

import java.io.Serializable;
import java.util.List;

public class CurveData implements Serializable {

    public String deviceGuid;
    public Long cookbookId;
    public String cookbookName;
    public String cookbookImgCover;
    public long curveCookbookId;
    public String curveCookbookName;
    public String curveCookbookImgCover;
    public Boolean isSent;
    public Integer orderNo;
    public String temperatureCurveParams;
    public String smartPanModeCurveParams;
    public String curveStageParams;
    public List<CurveStepListDTO> curveStepList;
    //设备参数
    public String deviceParams;

    public String curveSettingParams;

    public String name;
    public int needTime;


    /** todo lei 还没有字段
     * 是否下架 false下架 true上架
     */
    public boolean grounding;


    public static class CurveStepListDTO {
        public String curveStepId;
        public String curveCookbookId;
        public String no;
        public String markName;
        public String markTime;
        public int markTemp;
        public String curveStageParams;
        public String imageUrl;
        public String voiceUrl;
        public String videoUrl;
        public String description;
        public int status;
    }
}
