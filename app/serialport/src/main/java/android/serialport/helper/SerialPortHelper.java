package android.serialport.helper;

import android.serialport.SerialPort;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SerialPortHelper {
    private final static String TAG = SerialPortHelper.class.getSimpleName();

    private SphThreads sphThreads;
    private SerialPort serialPort;

    private SerialPortConfig serialPortConfig;

    /**
     * 是否需要返回最大数据接收长度
     */
    private boolean isReceiveMaxSize;


    /**
     *  循环指令
     */
    private byte[] payload = new byte[]{1, 0, 0, 0, 1, 2, 4,3};

    private static SerialPortHelper INSTANCE = new SerialPortHelper();

    public static SerialPortHelper getInstance() {
        return INSTANCE;
    }

    private SerialPortHelper() {

    }

    /**
     * 初始化 设置参数
     */
    public void init(SerialPortConfig config) {
        // 设置默认串口参数
        serialPortConfig = config;
        isReceiveMaxSize = false;
    }
    /**
     * 打开串口设备
     */
    public void openDevice(SphResultCallback onResultCallback){
        if(serialPortConfig == null){
            throw new IllegalArgumentException("'SerialPortConfig' must can not be null!!! ");
        }
        if(serialPortConfig.path == null){
            throw new IllegalArgumentException("You not have setting the device path, " +
                    "you must 'new SerialPortHelper(String path)' or call 'openDevice(String path)' ");
        }

        try {
            serialPort = new SerialPort //
                    .Builder(serialPortConfig.path, serialPortConfig.baudRate) // 串口地址地址，波特率
                    .parity(serialPortConfig.parity) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                    .dataBits(serialPortConfig.dataBits) // 数据位,默认8；可选值为5~8
                    .stopBits(serialPortConfig.stopBits) // 停止位，默认1；1:1位停止位；2:2位停止位
                    .flags(0)
                    .build();

            // 开启读写线程
            sphThreads = new SphThreads(serialPort, serialPortConfig.maxSize);
            sphThreads.setRecevieMaxSize(isReceiveMaxSize);
            sphThreads.setSphResultCallback(onResultCallback);
            sphThreads.start();

            if (null != onResultCallback)
                onResultCallback.onOpenSuccess();

        } catch (Exception e) {
            Log.e(TAG,"cannot open the device !!! " +
                    "path:"+serialPortConfig.path);
            if (null != onResultCallback)
                onResultCallback.onOpenFailed();
        }

    }

    /**
     * 发送串口命令
     * @param commands 串口命令
     *
     */
    public void addCommands(byte[] commands){
        if(null == serialPort){
            Log.e(TAG,"You not open device !!! ");
            return;
        }

        // 添加发送命令
        sphThreads.addCommands(commands);

    }


    /**
     * 关闭串口
     */
    public void closeDevice(){
        if (null != serialPort)
            serialPort.tryClose();
        //停止线程
        if(sphThreads !=null){
            sphThreads.stop();
        }
    }
}
