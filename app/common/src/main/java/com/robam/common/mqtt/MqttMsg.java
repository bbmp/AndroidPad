package com.robam.common.mqtt;

import com.robam.common.bean.RTopic;

import org.json.JSONObject;

public class MqttMsg extends JSONObject {
    /**
     * msgId
     */
    protected short msgId;

    //源头guid
    private String guid;
    /**
     * 数据
     */
    protected byte[] data;
    /**
     * 主题
     */
    private RTopic rTopic;
    private String dt;
    private String userId;
    //设备类型
    private String dc;

    public MqttMsg(short msgId) {
        this.msgId = msgId;
    }

    public MqttMsg(short msgId, String guid, String dt, String dc, String userid, RTopic topic, byte[] data) {
        this.msgId = msgId;
        this.guid = guid;
        this.dt = dt;
        this.dc = dc;
        this.userId = userid;
        this.rTopic = topic;
        this.data = data;
    }



    public int getID() {
        return msgId;
    }

    public String getDc() {
        return dc;
    }

    public byte[] getBytes() {
        return data;
    }

    public RTopic getrTopic() {
        return rTopic;
    }

    public String getGuid() {
        return guid;
    }

    public static class Builder {
        private short msgId;
        private byte[] payload;
        private RTopic topic;
        private String dt;
        private String dc;
        private String userId;
        private String guid;

        public Builder setMsgId(short msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder setPayload(byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Builder setTopic(RTopic topic) {
            this.topic = topic;
            return this;
        }

        public Builder setDt(String dt) {
            this.dt = dt;
            return this;
        }

        public Builder setDc(String dc) {
            this.dc = dc;
            return this;
        }

        public Builder setUserId(String userid) {
            this.userId = userid;
            return this;
        }

        public Builder setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        public MqttMsg build() {
            return new MqttMsg(msgId, guid, dt, dc, userId, topic, payload);
        }
    }
}
