package com.robam.common.bean;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.robam.common.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备指令快速接收
 */
public class MqttDirective {

    public static final String DATE_SPLIT_FLAG = "&!&";

    private BusMutableLiveData<Integer> directive = new BusMutableLiveData<>(-100); //设备状态变化
    private Map<String,WorkState> workModelState = new ConcurrentHashMap<>();

    private MqttDirective(){

    }

    private static class Holder {
        private static MqttDirective instance = new MqttDirective();
    }

    public static MqttDirective getInstance() {
        return MqttDirective.Holder.instance;
    }

    public MutableLiveData<Integer> getDirective() {
        return directive;
    }

    private static class BusMutableLiveData<T> extends MutableLiveData {

        public BusMutableLiveData(T value) {
            super(value);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
            super.observe(owner, observer);
            hook(observer);
        }

        private void hook(Observer<? super T> observer) {
            try {
                LogUtils.e("hook");
                Class<LiveData> liveDataClass = LiveData.class;
                Field mObserversField = liveDataClass.getDeclaredField("mObservers");
                mObserversField.setAccessible(true);
                Object mObserversObject = mObserversField.get(this);
                Class<?> mObserversObjectClass = mObserversObject.getClass();
                //获取到mObservers对象的get方法
                Method getMethod = mObserversObjectClass.getDeclaredMethod("get", Object.class);
                getMethod.setAccessible(true);
                //执行get方法
                Object invokeEntry = getMethod.invoke(mObserversObject, observer);
                Object observerWraper = null;
                if (invokeEntry != null && invokeEntry instanceof Map.Entry) {
                    observerWraper = ((Map.Entry<?, ?>) invokeEntry).getValue();
                }
                if (observerWraper == null) {
                    throw new NullPointerException("observerWraper 为空");
                }
                Class<?> superclass = observerWraper.getClass().getSuperclass();
                Field mLastVersion = superclass.getDeclaredField("mLastVersion");
                mLastVersion.setAccessible(true);

                Field mVersion = liveDataClass.getDeclaredField("mVersion");
                mVersion.setAccessible(true);

                Object mVersionValue = mVersion.get(this);
                mLastVersion.set(observerWraper, mVersionValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param guid
     * @param workModel
     */
   public void updateModelWorkState(String guid,int workModel){
       WorkState workState = workModelState.get(guid);
       if(workState == null){
           workState = new WorkState();
           workState.finishTimeL = System.currentTimeMillis();
           workState.workModel = workModel;
           workState.flag = 0;
           workModelState.put(guid,workState);
       }
       workState.finishTimeL = System.currentTimeMillis();
       workState.workModel = workModel;
       workState.flag = 0;//0 - 查询更新 ； 1 - 结束事件更新
   }

   public void finishWorkModelState(String guid){
       WorkState workState = workModelState.get(guid);
       if(workState == null){
           return;
       }
       workState.finishTimeL = System.currentTimeMillis();
       workState.flag = 1;//0 - 查询更新 ； 1 - 结束事件更新
   }

    public static class WorkState {
       public long finishTimeL;//完成结束时间
        public int workModel;//完成工作模式
        public int flag;
    }

    public WorkState getWorkState(String guid){
       return workModelState.get(guid);
    }




}
