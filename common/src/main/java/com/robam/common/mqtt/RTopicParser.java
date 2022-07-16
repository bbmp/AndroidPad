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
            Preconditions.checkState(list.length >= 3);

            String topicType = list[0];
            String guid = list[1] + list[2];
            if (RTopic.TOPIC_UNICAST.equals(topicType)) {
                return new RTopic(RTopic.TOPIC_UNICAST, list[1], list[2]);
            } else if (RTopic.TOPIC_BROADCAST.equals(topicType)) {
                return new RTopic(RTopic.TOPIC_BROADCAST, list[1], list[2]);
            } else {
                Preconditions.checkState(false, "invalid topic");
            }
        } catch (Exception e) {

        }
        return null;
    }
}
