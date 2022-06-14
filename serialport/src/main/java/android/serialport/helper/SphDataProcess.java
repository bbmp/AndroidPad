package android.serialport.helper;

import android.os.Looper;
import android.os.Message;
import android.serialport.SerialPort;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SphDataProcess {

    /**
     * 记录读取数据的大小
     */
    private int mSerialBufferSize;
    /**
     * 串口接收数据保存数组
     */
    private byte[] mSerialBuffer;

    /**
     * 最大读取长度，默认1024
     */
    private int maxSize;
    /**
     * 是否按最大接收长度进行返回
     */
    private boolean isReceiveMaxSize;
    /**
     * 数据回调
     */
    private SphResultCallback onResultCallback;

    /**
     * 发送、接收串口超时控制
     */
    private SphHandler sphHandler;

    /**
     * 数据同步控制
     */
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    /**
     * 发送命令
     */
    private static final int SENDCMD_WHAT = 2;
    /**
     * 接收命令
     */
    private static final int RECEIVECMD_WHAT = 3;
    /**
     * 串口对象
     */
    private SerialPort serialPort;


    public SphDataProcess(SerialPort serialPort, int maxSize) {
        this.serialPort = serialPort;
        this.maxSize = maxSize;
        sphHandler = new SphHandler(this);
    }

    /**
     * 串口读数据
     */
    public void readData() {
        // 创建数据接收数组
        createReadBuff();
        // 读取数据
        try {
            int size = 0;
            byte[] bytes = new byte[maxSize];
            size = serialPort.getInputStream().read(bytes);

            if (size > 0) {
                processingRecData(bytes, size);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 串口写入数据
     */
    public void writeData() {

        try {
            byte[] commands = (byte[]) blockingQueue.take();

            if (null != onResultCallback)
                sendMessage(commands, SENDCMD_WHAT);

            serialPort.getOutputStream().write(commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据配置对串口数据进行处理
     *
     * @param bytes  当前读取的数据字节数组
     * @param revLen 当前读取的数据长度
     */
    public void processingRecData(byte[] bytes, int revLen) {
        // 按设置的最大返回长度进行返回
        if (isReceiveMaxSize) {
            reCreateData(bytes, revLen);
            return;
        }
        resultCallback(bytes);
    }

    /**
     * 处理数据读取反馈，对读取的数据按maxSize进行处理
     * 如果数据一次没有读取完整，通过数组拷贝将数据补全完整
     *
     * @param bytes  当前读取的数据字节数组
     * @param revLen 当前读取的数据长度
     */
    private void reCreateData(byte[] bytes, int revLen) {
        if (hasReadDone(revLen) || mSerialBufferSize + revLen > maxSize) {
            // 截取剩余需要读取的长度
            int copyLength = maxSize - mSerialBufferSize;
            arrayCopy(bytes, 0, copyLength);
            mSerialBufferSize += copyLength;
            checkReCreate(mSerialBuffer);

            // 对反馈数据剩余的数据进行重新拷贝
            int lastLength = revLen - copyLength;
            arrayCopy(bytes, copyLength, lastLength);
            mSerialBufferSize = lastLength;
            checkReCreate(mSerialBuffer);
        } else {
            // 没有读取完整的情况，继续进行读取
            arrayCopy(bytes, 0, revLen);
            mSerialBufferSize += revLen;
            checkReCreate(mSerialBuffer);
        }
    }

    /**
     * 判断当前数据是否读取完整
     *
     * @param revLen 读取数据的长度
     * @return
     */
    private boolean hasReadDone(int revLen) {
        return revLen >= maxSize && mSerialBufferSize != maxSize;
    }

    /**
     * 判断是否完成重组
     *
     * @param resultBytes
     */
    private void checkReCreate(byte[] resultBytes) {
        if (mSerialBufferSize == maxSize) {
            resultCallback(resultBytes);
        }
    }

    /**
     * 判断数据是否读取完成，通过回调输出读取数据
     */
    private void resultCallback(byte[] resultBytes) {
        if (onResultCallback == null) {
            reInit();
            return;
        }
        sendMessage(resultBytes, RECEIVECMD_WHAT);
        reInit();
    }

    /**
     * 重置数据
     */
    private void reInit() {
        mSerialBufferSize = 0;
    }

    /**
     * 通过数组拷贝，对数据进行重组
     *
     * @param bytes  当前读取的数据字节数组
     * @param srcPos 需要拷贝的源数据位置
     * @param length 拷贝的数据长度
     */
    private void arrayCopy(byte[] bytes, int srcPos, int length) {
        System.arraycopy(bytes, srcPos, mSerialBuffer, mSerialBufferSize, length);
    }


    /**
     * 添加串口发送命令
     *
     * @param command
     */
    public void addCommands(byte[] command) {
        // 初始化同步控制
        try {
            blockingQueue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setSphResultCallback(SphResultCallback onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    /**
     * 设置是否按最大接收长度进行返回
     *
     * @param receiveMaxSize
     */
    public void setRecevieMaxSize(boolean receiveMaxSize) {
        this.isReceiveMaxSize = receiveMaxSize;
    }

    /**
     * 创建数据接收数组
     */
    public void createReadBuff() {
        if (mSerialBuffer == null) {
            mSerialBuffer = new byte[maxSize];
        }
    }

    /**
     * 发送串口数据到主线程
     * @param commands  串口数据
     * @param what          数据标识
     */
    private void sendMessage(byte[] commands, int what){
        Message message = sphHandler.obtainMessage();
        message.what = what;
        message.obj = commands;
        sphHandler.sendMessage(message);
    }

    /**
     * 获取最大读取长度
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }


    /**
     * 数据通过Handler发送到主线程
     */
    private static class SphHandler extends android.os.Handler {
        private SphDataProcess processingRecData;
        public SphHandler(SphDataProcess processingRecData) {
            super(Looper.getMainLooper());
            this.processingRecData = processingRecData;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            processingRecData.receiveData(msg);
        }
    }

    /**
     * 处理数据回调
     * @param msg
     */
    private void receiveData(Message msg){
        switch (msg.what) {
            case SENDCMD_WHAT:
                onResultCallback.onSendData((byte[]) msg.obj);
                break;
            case RECEIVECMD_WHAT:
                onResultCallback.onReceiveData((byte[]) msg.obj);
                break;
            default:
                break;
        }
    }
}
