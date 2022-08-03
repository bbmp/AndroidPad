package com.robam.common.mqtt;

import android.content.Context;
import android.util.Log;

import com.robam.common.bean.RTopic;
import com.robam.common.device.IPlat;

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
    private final String CLIENTID = "";
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    private String HOST = "tcp://mqtt.myroki.com:1883";//服务器地址（协议+地址+端口号）
    //    public String HOST = "tcp://develop.mqtt.myroki.com:1883";//服务器地址（协议+地址+端口号）
    private String USERNAME = "admin";//用户名
    private String PASSWORD = "jnkj2014";//密码
    public String PUBLISH_TOPIC = getPublishTopic();//发布主题
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
        iPlat = plat;
        iProtocol = protocol;
        mqttAndroidClient = new MqttAndroidClient(context, HOST, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(20); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(60); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码

        // last will message
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
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
        doClientConnection();
    }

    /**
     * Mqtt是否连接成功监听
     */
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                String topic = new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt()
                        , iPlat.getMac()).getTopic();
                mqttAndroidClient.subscribe(topic, 2, null, mqttActionListener);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 订阅成功回调(订阅设备主题)
     */
    IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "订阅成功 ");
            //发送设备上线成功
            deviceConnectedNoti();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//            Log.i(TAG, "订阅失败 " + exception.getMessage());
        }
    };

    /**
     *  断开连接
     */
    private void stop() {
        try {
            mqttAndroidClient.disconnect(); //断开连接
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 订阅的主题的消息回调
     */
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + Arrays.toString(message.getPayload()));
            int msgId = iProtocol.decode(topic, message.getPayload());
//            if (null != mqttMsgCallback && null != message)
//                mqttMsgCallback.messageArrived(message.getPayload());
            //统一 处理
            if (msgId == MsgKeys.getDeviceAttribute_Req){
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.getDeviceAttribute_Rep)
                        .setDt(iPlat.getDt())
                        .setSignNum(iPlat.getMac())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt(), iPlat.getMac()))
                        .build();
                publish(msg, iProtocol);
            }else if (msgId == MsgKeys.setDeviceAttribute_Req){
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.setDeviceAttribute_Req)
                        .setDt(iPlat.getDt())
                        .setSignNum(iPlat.getMac())
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, iPlat.getDt(), iPlat.getMac()))
                        .build();
                publish(msg, iProtocol);
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            doClientConnection();//连接断开，重连
        }
    };

    private String getPublishTopic() {
        return "/u/" + "DB620/" ;
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
            Log.i(TAG, "发送的主题： " + topic);
            Log.i(TAG, "发送的消息： " + Arrays.toString(data));
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, data, qos.intValue(), retained.booleanValue());
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
                .setDt(iPlat.getDt())
                .setSignNum(iPlat.getMac())
                .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, iPlat.getDt(), iPlat.getMac()))
                .build();

        publish(msg, iProtocol);
    }
}
