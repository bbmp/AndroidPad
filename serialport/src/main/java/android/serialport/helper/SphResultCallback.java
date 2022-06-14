package android.serialport.helper;

public interface SphResultCallback {

    /**
     * 发送命令
     * @param sendCom 串口发送的命令
     */
    void onSendData(byte[] sendCom);

    /**
     * 收到的数据
     * @param data 串口收到的数据
     */
    void onReceiveData(byte[] data);

}
