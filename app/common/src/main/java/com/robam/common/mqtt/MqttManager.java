package com.robam.common.mqtt;

import android.content.Context;
import android.util.Log;

import com.robam.common.bean.RTopic;
import com.robam.common.device.IPlat;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

//全局mqtt收发
public class MqttManager {
    private static final String TAG = MqttManager.class.toString();
    private String CLIENTID = "";
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    //private String HOST = "tcp://mqtt.myroki.com:1883";//服务器地址（协议+地址+端口号）
    public String HOST = "tcp://develop.mqtt.myroki.com:1883";//服务器地址（协议+地址+端口号） //modify by zm TODO(暂时切换到测试环境)
    private String USERNAME = "admin";//用户名
    private String PASSWORD = "jnkj2014";//密码
    public String RESPONSE_TOPIC = "message_arrived";//响应主题

    //初始化平台
    private IPlat iPlat;
    //初始化mqtt协议
    private IProtocol iProtocol;

    private static MqttManager INSTANCE = new MqttManager();

    public static MqttManager getInstance() {
        return INSTANCE;
    }

    public void start(Context context, IPlat plat, IProtocol protocol) {
        if (null != mqttAndroidClient)
            return;

        iPlat = plat;
        iProtocol = protocol;
        CLIENTID = iPlat.getDeviceOnlySign();
        mqttAndroidClient = new MqttAndroidClient(context, HOST, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(20); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(60); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
//        mMqttConnectOptions.setAutomaticReconnect(true);
        // last will message
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = getPublishTopic();
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
            }
        }
        doConnect();
    }

    /**
     * Mqtt是否连接成功监听
     */
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            LogUtils.e( "连接成功 ");
            try {
                String topic = new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt()
                        , iPlat.getMac()).getTopic();
                LogUtils.e("订阅主题 " + topic);
                mqttAndroidClient.subscribe(topic, 2, iPlat.getDeviceOnlySign(), mqttActionListener);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            LogUtils.e( "连接失败 ");
//            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    /**
     * 订阅一组设备
     */
    public void subscribe(String[] topicFilters, int[] pos) {
        try {
            mqttAndroidClient.subscribe(topicFilters, pos, null, mqttActionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 单个订阅设备
     */
    public void subscribe(String dt, String number) {
        try {
            String topic = new RTopic(RTopic.TOPIC_BROADCAST, dt
                    , number).getTopic();
            LogUtils.e("订阅主题" + topic);
            mqttAndroidClient.subscribe(topic, 2, null, mqttActionListener);//订阅主题，参数：主题、服务质量
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 取消订阅
     */
    public void unSubscribe(String dt, String number) {
        try {
            String topic = new RTopic(RTopic.TOPIC_UNICAST, dt
                    , number).getTopic();
            mqttAndroidClient.unsubscribe(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        try {
            return mqttAndroidClient.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 连接
     */
    public void doConnect() {
        try {
            mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 重连
     */
    public interface IConncect {
        void onSuccess();
    }
    public void reConnect(IConncect iConncect) {
        try {
            LogUtils.e( "reConnect ");
            mqttAndroidClient.connect(mMqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (null != iConncect)
                        iConncect.onSuccess();
                    LogUtils.e( "重连成功 ");
                    try {
                        String topic = new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt()
                                , iPlat.getMac()).getTopic();
                        LogUtils.e("订阅主题 " + topic);
                        mqttAndroidClient.subscribe(topic, 2, iPlat.getDeviceOnlySign(), mqttActionListener);//订阅主题，参数：主题、服务质量
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * 订阅成功回调(订阅设备主题)
     */
    IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            LogUtils.e( "订阅成功 ");
            //发送设备上线成功 主设备
            if (null != asyncActionToken && iPlat.getDeviceOnlySign().equals(asyncActionToken.getUserContext()))
                deviceConnectedNoti();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            LogUtils.e("订阅失败 ");
        }
    };

    /**
     *  断开连接
     */
    public void stop() {
        try {
            mqttAndroidClient.disconnect(); //断开连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
      close
     */
    public void close() {
        try {
            if (isConnected())
                mqttAndroidClient.disconnect();
            mqttAndroidClient.close(); //关闭
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 订阅的主题的消息回调
     */
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            iProtocol.decode(topic, message.getPayload());
//            if (null != mqttMsgCallback && null != message)
//                mqttMsgCallback.messageArrived(message.getPayload());
            //统一 处理响应
//            if (msgId == MsgKeys.getDeviceAttribute_Req){
//                MqttMsg msg = new MqttMsg.Builder()
//                        .setMsgId(MsgKeys.getDeviceAttribute_Rep)
//                        .setGuid(iPlat.getDeviceOnlySign())
//                        .setDt(iPlat.getDt())
//                        .setSignNum(iPlat.getMac())
//                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt(), iPlat.getMac()))
//                        .build();
//                publish(msg, iProtocol);
//            }else if (msgId == MsgKeys.setDeviceAttribute_Req){
//                MqttMsg msg = new MqttMsg.Builder()
//                        .setMsgId(MsgKeys.setDeviceAttribute_Req)
//                        .setGuid(iPlat.getDeviceOnlySign())
//                        .setDt(iPlat.getDt())
//                        .setSignNum(iPlat.getMac())
//                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt(), iPlat.getMac()))
//                        .build();
//                publish(msg, iProtocol);
//            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            LogUtils.e( "连接断开 ");
//            doClientConnection();//连接断开，重连
        }
    };

    private String getPublishTopic() {
        return "/u/" + "DB620/" + iPlat.getMac();
    }

    /**
     * @param topic
     * @param data
     */
    public void publish(String topic, byte[] data) {
        //获取发布主题
        Integer qos = 0;
        Boolean retained = false;
        try {
            LogUtils.e( "发送的主题： " + topic);
            LogUtils.e( "发送的消息： " + StringUtils.bytes2Hex(data));
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, data, qos.intValue(), retained.booleanValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 发布 （模拟其他客户端发布消息）
     *  具体协议实现
     * @param
     */
    public void publish(MqttMsg msg, IProtocol protocol) {
        //获取发布主题
        String topic = msg.getrTopic().getTopic();
        Integer qos = 0;
        Boolean retained = false;
        try {
            byte[] data = protocol.encode(msg);
            //LogUtils.e( "发送的主题： " + topic);
            LogUtils.e( "发送的消息： top " +topic + " " +StringUtils.bytes2Hex(data));
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, data, qos.intValue(), retained.booleanValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void publish(MqttMsg msg, IProtocol protocol,MqttSendMsgListener listener) {
        //获取发布主题
        String topic = msg.getrTopic().getTopic();
        Integer qos = 0;
        Boolean retained = false;
        try {
            byte[] data = protocol.encode(msg);
            //LogUtils.e( "发送的主题： " + topic);
            LogUtils.e( "发送的消息： top " +topic + " " +StringUtils.bytes2Hex(data));
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, data, qos.intValue(), retained.booleanValue(), null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if(listener != null){
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if(listener != null){
                        listener.onFailure();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     *
     * @param message 消息
     */
    public void response(String message) {
        String topic = RESPONSE_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送设备上线通知
     */
    private void deviceConnectedNoti() {
        Log.i(TAG, "发送设备上线通知  " + android.os.Process.myTid() + "  " + iPlat.getMac());
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.DeviceConnected_Noti)
                .setGuid(iPlat.getDeviceOnlySign())
                .setDt(iPlat.getDt())
                .setUserId(iPlat.getMac())
                .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, iPlat.getDt(), iPlat.getMac()))
                .build();

        publish(msg, iProtocol);
    }

    public static interface MqttSendMsgListener{
        void onSuccess();
        void onFailure();
    }
}
