package com.robam.roki.bean;

public class CookingKnowledge {
    public long id;

    /**
     * 图片封面url
     */
    public String pictureCoverUrl;
    /**
     * 视频封面url
     */
    public String videoCoverUrl;
    /**
     * 视频url
     */
    public String videoUrl;

    /**
     * 0-图片；1-视频
     */
    public int contentType;

    /**
     * 视频
     */
    public String videoId;
    /**
     * 头像url
     */
    public String portraitUrl;
    /**
     * 标题
     */
    public String title;

    /**
     * 标签
     */
    public String lable;

    public long getId() {
        return id;
    }

    public String getPictureCoverUrl() {
        return pictureCoverUrl;
    }

    public String getVideoCoverUrl() {
        return videoCoverUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getContentType() {
        return contentType;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLable() {
        return lable;
    }
}
