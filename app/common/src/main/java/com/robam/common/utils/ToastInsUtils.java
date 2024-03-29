package com.robam.common.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.robam.common.R;

/**
 * 单实例Toast
 */
public class ToastInsUtils {


    private static Toast toast;
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
        if(toast == null){
            Context applicationContext = cx.getApplicationContext();
            if(applicationContext instanceof  Application){
                init((Application) applicationContext,msg,duration);
            }else {
                return;
            }
        }
        toast.setDuration(duration);
        toast.setText(EMPTY+msg+EMPTY);
        toast.show();
    }

    public static void init(final Application application,String msg,int duration){
        toast = Toast.makeText(application,msg,duration);
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
    }


}
