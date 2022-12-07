package com.robam.common.bean;

public class RTopic {
    public final static String TOPIC_UNICAST = "u";//单播
    public final static String TOPIC_BROADCAST = "b";//广播
    public final static String TOPIC_FORMAT = "/%s/%s/%s";
    /**
     *  deviceType（5位） ， signNum （12位唯一编码）
     */
    protected  String deviceType , signNum;
    //
    private String topicType;
    /**
     *
     * @param dt
     * @param signNum
     */
    public RTopic(String topicType, String dt , String signNum) {
        this.topicType = topicType;
        this.deviceType = dt;
        this.signNum = signNum;
    }

    public String getSignNum() {
        return signNum;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getTopic() {
        return String.format(TOPIC_FORMAT, topicType, deviceType, signNum);
    }
}
