package com.robam.common.mqtt;

import com.robam.common.bean.RTopic;

public class MqttMsg {
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
    private String signNum;

    public MqttMsg(short msgId) {
        this.msgId = msgId;
    }

    public MqttMsg(short msgId, String guid, String dt, String signNum, RTopic topic, byte[] data) {
        this.msgId = msgId;
        this.guid = guid;
        this.dt = dt;
        this.signNum = signNum;
        this.rTopic = topic;
        this.data = data;
    }



    public int getID() {
        return msgId;
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
        private String signNum;
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

        public Builder setSignNum(String signNum) {
            this.signNum = signNum;
            return this;
        }

        public Builder setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        public MqttMsg build() {
            return new MqttMsg(msgId, guid, dt, signNum, topic, payload);
        }
    }
}
