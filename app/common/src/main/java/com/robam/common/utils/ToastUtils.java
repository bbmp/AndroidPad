package com.robam.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.robam.common.R;

public class ToastUtils {

    public static final String EMPTY = "    ";
    public static void showThrowable(Context cx, Throwable t) {
        if (t != null)
            showShort(cx, t.getMessage());
    }

    public static void showException(Context cx, Exception e) {
        if (e != null)
            showShort(cx, e.getMessage());
    }

    public static void showShort(Context cx, int resId) {
        show(cx, resId, Toast.LENGTH_SHORT);
    }

    public static void showShort(Context cx, String msg) {
        show(cx, msg, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context cx, int resId) {
        show(cx, resId, Toast.LENGTH_LONG);
    }

    public static void showLong(Context cx, String msg) {
        show(cx, msg, Toast.LENGTH_LONG);
    }

    public static void show(Context cx, int resId, int duration) {
        String msg = cx.getString(resId);
        show(cx, msg, duration);
    }

    public static void show(final Context cx, final String msg, final int duration) {

        if (TextUtils.isEmpty(msg))
            return;

        Toast toast = Toast.makeText(cx, EMPTY+msg+EMPTY, duration);
        toast.setGravity(Gravity.CENTER,0,0);
        View toastView = toast.getView();
        if(toastView != null){
            toastView.setBackgroundResource(R.drawable.common_black_round_rect);
            if(toastView instanceof ViewGroup){
                View child = ((ViewGroup) toastView).getChildAt(0);
                if(child != null && child instanceof TextView){
                    ((TextView) child).setTextColor(Color.WHITE);
                }
            }
        }
        toast.show();

    }

}
