package android.serialport.helper;

import android.serialport.SerialPort;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SerialPortHelper {
    private final static String TAG = SerialPortHelper.class.getSimpleName();

    private int baudRate = 9600;
    private int dataBits = 8;
    private char checkBits = 'N';
    private int stopBits = 1;
    private int parity = 0;
    private String path = "dev/ttyS3";
    private SphThreads sphThreads;
    private SerialPort serialPort;
    /**
     * 最大接收数据的长度
     */
    private int maxSize;

    /**
     * 是否需要返回最大数据接收长度
     */
    private boolean isReceiveMaxSize;

    /**
     * 数据处理
     */
    private SphDataProcess processingData;

    /**
     *  循环指令
     */
    private byte[] payload = new byte[]{1, 0, 0, 0, 1, 2, 4,3};
    /**
     * 线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());

    private static SerialPortHelper INSTANCE = new SerialPortHelper();

    public static SerialPortHelper getInstance() {
        return INSTANCE;
    }

    private SerialPortHelper() {
        // 设置默认串口参数
        maxSize = 16;
        isReceiveMaxSize = true;
    }
    /**
     * 打开串口设备
     */
    public void openDevice(SphResultCallback onResultCallback){
        if(path == null){
            throw new IllegalArgumentException("You not have setting the device path, " +
                    "you must 'new SerialPortHelper(String path)' or call 'openDevice(String path)' ");
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    serialPort = new SerialPort //
                            .Builder(path, baudRate) // 串口地址地址，波特率
                            .parity(parity) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                            .dataBits(dataBits) // 数据位,默认8；可选值为5~8
                            .stopBits(stopBits) // 停止位，默认1；1:1位停止位；2:2位停止位
                            .flags(0)
                            .build();
                    // 创建数据处理
                    processingData = new SphDataProcess(serialPort, maxSize);
                    processingData.setRecevieMaxSize(isReceiveMaxSize);
                    processingData.setSphResultCallback(onResultCallback);
                    // 开启读写线程
                    sphThreads = new SphThreads(processingData);

                    //循环
                    while (true) {
                        processingData.addCommands(payload);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG,"cannot open the device !!! " +
                            "path:"+path);
                }
            }
        });
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
        processingData.addCommands(commands);

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
