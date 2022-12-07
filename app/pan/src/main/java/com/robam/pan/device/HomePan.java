package com.robam.pan.device;


import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.Entry;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.subdevice.Pan;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomePan {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());

    //当前进入的锅首页
    public static HomePan getInstance() {
        return HomePan.PanHolder.instance;
    }

    private static class PanHolder {
        private static final HomePan instance = new HomePan();
    }

    public ArrayList<Entry> entryList = new ArrayList<>();  //创作列表或还原列表

    //当前guid
    public String guid;
    //锅温度
    public MutableLiveData<Integer> panTemp = new MutableLiveData<Integer>(0);

    public boolean isPanOffline() {
        for (Device device : AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device.status == Device.ONLINE) {

                return false;
            }
        }
        return true;
    }
    //10s快炒
    private Future<Integer> future;
    public void stirFry() {
        curTime = 0;
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                while (curTime < 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    curTime++;
                }
                return curTime;
            }
        };
        future = threadPoolExecutor.submit(callable);
    }
    //停止10s翻炒
    public void stopStirFry() {
        try {
            future.cancel(true);
        } catch (Exception e) {}
    }
    //曲线创建
    private int curTime;
    public void curveCreate() {
        Pan pan = null;
        for (Device device : AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device.guid.equals(guid)) {
                pan = (Pan) device;
                break;
            }
        }
        curTime = 0;
        Pan finalPan = pan;
        if (null != finalPan) {
            Entry entry = new Entry(curTime, finalPan.panTemp); //第一个点
            entryList.add(entry);
            Callable<Integer> callable = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                    curTime += 2;
                    Entry entry = new Entry(curTime, finalPan.panTemp);
                    entryList.add(entry);
                    return curTime;
                }
            };
            future = threadPoolExecutor.submit(callable);
        }
    }
}
