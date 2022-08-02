package com.robam.steamoven.device;

//远程控制一体机,mqtt协议打包，从烟机到一体机界面,应该只有烟机会有,供烟机端调用
public class SteamMqttControl implements SteamFunction {
    @Override
    public void shutDown() {
        //mqtt指令打包
    }

    @Override
    public void powerOn() {

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
