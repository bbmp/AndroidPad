package com.robam.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {

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

        Toast toast = Toast.makeText(cx, msg, duration);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

    }

}
