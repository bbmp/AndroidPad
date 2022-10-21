package com.robam.common.bean;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.robam.common.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 设备指令快速接收
 */
public class MqttDirective {

    public static final String DATE_SPLIT_FLAG = "&!&";

    private BusMutableLiveData<Integer> directive = new BusMutableLiveData<>(-100); //设备状态变化

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

   /* public void setDirectiveData(String top,short msgId){
        directive.setValue(top+DATE_SPLIT_FLAG+msgId);
    }*/

    /*public static short getMsgId(String content){
        return Short.parseShort(content.split(DATE_SPLIT_FLAG)[1]);
    }*/



}
