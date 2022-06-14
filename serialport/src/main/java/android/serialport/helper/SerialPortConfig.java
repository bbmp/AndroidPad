package android.serialport.helper;

public class SerialPortConfig {

    /**
     * 串口地址
     */
    public String path;
    /**
     * 波特率
     */
    public int baudRate = 9600;
    /**
     * 数据位 取值 位 7或 8
     */
    public int dataBits = 8;
    /**
     * 停止位 取值 1 或者 2
     */
    public int stopBits = 1;
    /**
     * 校验类型 0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     */
    public int parity  = 0;

    /**
     * 最大接收数据的长度
     */
    public int maxSize;

    /**
     * 是否需要返回最大数据接收长度
     */
    private boolean isReceiveMaxSize;

    public SerialPortConfig(String path, int baudRate, int dataBits, int stopBits, int parity, int maxSize) {
        this.path = path;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.maxSize = maxSize;
    }

    public static class Builder {
        private int baudRate = 9600;
        private int dataBits = 8;
        private int stopBits = 1;
        private int parity = 0;
        private String path = "dev/ttyS3";
        private int maxSize;


        public Builder setBaudRate(int baudRate) {
            this.baudRate = baudRate;
            return this;
        }

        public void setDataBits(int dataBits) {
            this.dataBits = dataBits;
        }

        public Builder setStopBits(int stopBits) {
            this.stopBits = stopBits;
            return this;
        }

        public Builder setParity(int parity) {
            this.parity = parity;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public SerialPortConfig build() {
            return new SerialPortConfig(path, baudRate, dataBits, stopBits, parity, maxSize);
        }
    }

}
