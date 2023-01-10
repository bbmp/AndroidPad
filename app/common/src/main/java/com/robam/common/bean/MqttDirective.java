package com.robam.common.bean;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备指令快速接收
 */
public class MqttDirective {

    private static final int MAX_WORK_TIME_OF_DURATION = 1000 * 60 * 5;//工作完成后，多长时间内显示完成状态
    private static final int SAFE_UPDATE_INTERVAL = 1000 * 12;//安全更新时间 30秒

    public static final String STR_LIVE_DATA_FLAG = "&:&:&";//安全更新时间 30秒

    @Deprecated
    private StickyLiveData<Integer> directive = new StickyLiveData<>(); //设备状态变化
    private Map<String,WorkState> workModelState = new ConcurrentHashMap<>();
    //private BusMutableLiveData<String> strLiveData = new BusMutableLiveData<>(""); //设备状态变化
    private StickyLiveData<String> stickyLiveData = new StickyLiveData();//设备状态变化


    private MqttDirective(){}

    private static class Holder {
        private static MqttDirective instance = new MqttDirective();
    }

    public static MqttDirective getInstance() {
        return MqttDirective.Holder.instance;
    }

    @Deprecated
    public StickyLiveData<Integer> getDirective() {
        return directive;
    }

    public void setDirectiveStickyData(int bsCode){
        directive.setStickyData(bsCode);
    }


    public StickyLiveData<String> getStrLiveData() {
        return stickyLiveData;
    }

    public void setStrLiveDataValue(String guid, int code){
        stickyLiveData.setStickyData(guid+STR_LIVE_DATA_FLAG+code);
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
                //LogUtils.e("hook");
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
   public void updateModelWorkState(String guid,int workModel,int repeatId){
       WorkState workState = workModelState.get(guid);
       if(workState != null && workState.flag == 1 &&
               System.currentTimeMillis() - workState.finishTimeL <= SAFE_UPDATE_INTERVAL){//安全检查，在任务刚结束的10秒内，不做更新
           return;
       }
       if(workState == null){
           workState = new WorkState();
           workState.finishTimeL = System.currentTimeMillis();
           workState.workModel = workModel;
           workState.flag = 0;
           workState.repeatId = repeatId;
           workModelState.put(guid,workState);
       }
       //LogUtils.e("MqttCabinet updateModelWorkState guid="+guid +" flag=" +workState.flag +" dir="+(System.currentTimeMillis() - workState.finishTimeL));
       workState.repeatId = repeatId;
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
       //LogUtils.e("MqttCabinet finishWorkModelState guid=" + guid);
   }

    public static class WorkState {
        public long finishTimeL;//模式时间毫秒值
        public int workModel;//工作模式
        public int repeatId;//菜谱ID
        public int flag;

        /**
         * 是否正常工作结束
         * @return
         */
        public boolean isFinish(){
            return flag == 1 && System.currentTimeMillis() - finishTimeL <= MAX_WORK_TIME_OF_DURATION ;
        }

        /**
         * 是否结束(正常/主动/异常结束)
         * @return
         */
        public boolean isEnd(){
            return System.currentTimeMillis() - finishTimeL <= MAX_WORK_TIME_OF_DURATION ;
        }

    }

    public WorkState getWorkState(String guid){
       return workModelState.get(guid);
    }


    public class StickyLiveData<T>  extends LiveData<T>{

        T mStickyData;
        volatile long mVersion = 0;

        void setStickyData(T stickyData){
            this.mStickyData = stickyData;
            setValue(stickyData);
        }

        void postStickyData(T stickyData ){
            this.mStickyData = stickyData;
            postValue(stickyData);
        }

        @Override
        protected void setValue(T value) {
            mVersion++;
            super.setValue(value);
        }



        public void postValue(T value) {
            mVersion++;
            super.postValue(value);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            //super.observe(owner, observer);
            observeSticky(owner,false,observer);
        }


        public void observeSticky(LifecycleOwner owner,Boolean sticky,Observer<? super T> observer){
            //允许指定注册的观察者 是否需要关心粘性事件
            //sticky = true，如果之前存在已经发送的数据，那么这个observier会受到之前的粘性事件的消息
//            owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
//                if(event == Lifecycle.Event.ON_DESTROY){
//                    eventMap.remove(eventName);
//                }
//            });
            super.observe(owner, new StickyObserver(this,sticky,observer));
        }
    }

    class StickyObserver<T> implements Observer<T> {
        private StickyLiveData<T> stickyLiveData;
        private Boolean sticky ;
        private Observer<T> observer;
        private volatile long lastVersion;
        public StickyObserver(StickyLiveData<T> stickyLiveData , Boolean sticky , Observer<T> observer){
            this.stickyLiveData = stickyLiveData;
            this.sticky = sticky;
            this.observer = observer;
            this.lastVersion = stickyLiveData.mVersion;
        }
        //lastVersion和livedata和version对齐的原因，就是为控制粘性事件的分发
        //sticky 不等于 true,只能接收到注册之后发送的消息，如果要接收粘性事件，则sticky 需要传递为true

        @Override
        public void onChanged(T t) {
            if(lastVersion >= stickyLiveData.mVersion){
                if(sticky && stickyLiveData.mStickyData != null){
                    observer.onChanged(stickyLiveData.mStickyData);
                }
                return;
            }
            lastVersion = stickyLiveData.mVersion;
            observer.onChanged(t);
        }
    }

}
