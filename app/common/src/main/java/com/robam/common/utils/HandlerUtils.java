package com.robam.common.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class HandlerUtils {

    public static Handler getMainHandler(HandlerBack handlerBack){
        return new MainHandler(handlerBack);
    }

    private static class MainHandler extends Handler {
        private WeakReference<HandlerBack> weakReference;
        public MainHandler(HandlerBack activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            HandlerBack handlerBack = weakReference.get();
            if(handlerBack != null){
                handlerBack.onMsg(msg);
            }
        }
    }

    public static interface HandlerBack{
        void onMsg(Message msg);
    }
}
