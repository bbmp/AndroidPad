package com.robam.stream.device;

//本地控制，串口通信,界面启动入口决定是本地控制还是远程控制
public class SteamLocalControl implements SteamFunction{
    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {
//        byte[] payload = SerialPortMsgHelper.powerOn();
//        SteamOven.getInstance().marshaller(payload);
//        DeviceFactory.getPlatform().screenOn();
    }

    @Override
    public void orderWork() {

    }

    @Override
    public void stopWork() {

    }

    @Override
    public void startWork() {

    }

    @Override
    public void pauseWork() {

    }

    @Override
    public void continueWork() {

    }
}
