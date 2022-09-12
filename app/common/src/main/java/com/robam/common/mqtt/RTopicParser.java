package com.robam.common.mqtt;


import android.annotation.SuppressLint;

import androidx.core.util.Preconditions;

import com.robam.common.bean.RTopic;


public class RTopicParser {


    @SuppressLint("RestrictedApi")
    public static RTopic parse(String topicString) {
        try {
            Preconditions.checkNotNull(topicString);
            String[] list = topicString.split("/");
            Preconditions.checkState(list.length >= 4);

            String topicType = list[1];
            String guid = list[2] + list[3];
            if (RTopic.TOPIC_UNICAST.equals(topicType)) {
                return new RTopic(RTopic.TOPIC_UNICAST, list[2], list[3]);
            } else if (RTopic.TOPIC_BROADCAST.equals(topicType)) {
                return new RTopic(RTopic.TOPIC_BROADCAST, list[2], list[3]);
            } else {
                Preconditions.checkState(false, "invalid topic");
            }
        } catch (Exception e) {

        }
        return null;
    }
}
