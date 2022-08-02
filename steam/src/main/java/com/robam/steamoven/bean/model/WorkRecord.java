package com.robam.steamoven.bean.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : huxw
 *     e-mail : xwhu93@163.com
 *     time   : 2022/05/13
 *     desc   : 工作记录表 （每次工作记录生成一个工作记录）
 *     version: 1.0
 * </pre>
 */
public class WorkRecord extends LitePalSupport {
    /**
     * 主键
     */
    @Column
    public int id ;

    /**
     * 唯一工作GUID（新增 关联视频名）
     */
    @Column
    public String workGuid ;
    /**
     * 当次工作名称
     */
    @Column
    public String workName;
    /**
     * 是否有视频 0：无 1：有 方便查询
     */
    @Column
    public int ownVideo;
    /**
     * 当次工作视频url
     */
    @Column
    public String videoUrl;

    /**
     * 工作对应曲线
     */
    @Column
    public MCarve carve ;

}
