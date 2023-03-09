package com.robam.common.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.robam.common.R;


public class WindowsUtils {

    private static String TAG = WindowsUtils.class.getSimpleName();

    private static WindowManager mWindowManager = null;

    private static WindowManager.LayoutParams params;

    public static Boolean isShown = false;

    private static View mView = null;

    /**

     * 显示弹出框

     *

     * @param context

     */

    @SuppressWarnings("WrongConstant")

    public static void initPopupWindow(final Context context, View.OnClickListener listener) {

        if (isShown) {

            return;

        }

        isShown = true;

        // 获取WindowManager

        mWindowManager = (WindowManager) context

                .getSystemService(Context.WINDOW_SERVICE);

        mView = setUpView(context, listener);
        //不显示
        mView.setVisibility(View.GONE);

        params = new WindowManager.LayoutParams();

        // 类型，系统提示以及它总是出现在应用程序窗口之上。

        params.type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ;

        // 设置flag

        int flags = canTouchFlags;

        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件

        params.flags = flags;

        // 不设置这个弹出框的透明遮罩显示为黑色

        params.format = PixelFormat.TRANSLUCENT;

        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口

        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按

        // 不设置这个flag的话，home页的划屏会有问题

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.TOP;
        params.x = (int) context.getResources().getDimension(com.robam.common.R.dimen.dp_150);

        mWindowManager.addView(mView, params);

    }

    private static int canTouchFlags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

    private static int notTouchFlags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

    /**

     * 设置是否可响应点击事件

     *

     * @param isTouchable

     */

    public static void setTouchable(boolean isTouchable) {

        if (isTouchable) {

            params.flags = canTouchFlags;

        } else {

            params.flags = notTouchFlags;

        }

        mWindowManager.updateViewLayout(mView, params);

    }

    /**

     * 隐藏弹出框

     */
    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            mView.setVisibility(View.GONE);
        }
    }

    public static void closePopupWindow() {

        if (isShown && null != mView) {

            mWindowManager.removeView(mView);

            isShown = false;

        }

    }
    //显示
    public static void showPopupWindow() {
        if (isShown && null != mView) {
            mView.setVisibility(View.VISIBLE);
        }
    }


    private static ImageView ivFloat;
    private static boolean drag = false;

    private static View setUpView(final Context context, View.OnClickListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.common_layout_popwindow,

                null);

        ivFloat = view.findViewById(R.id.iv_float);
        ivFloat.setAlpha(0.5f);
        ivFloat.setOnClickListener(listener);

        ivFloat.setOnTouchListener(new View.OnTouchListener() {

            private float lastX; //上一次位置的X.Y坐标

            private float lastY;

            private float nowX; //当前移动位置的X.Y坐标

            private float nowY;

            private float tranX; //悬浮窗移动位置的相对值

            private float tranY;

            @Override

            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        // 获取按下时的X，Y坐标

                        lastX = event.getRawX();

                        lastY = event.getRawY();

                        drag = false;
                        ivFloat.setAlpha(1.0f);

                        break;

                    case MotionEvent.ACTION_MOVE:

                        // 获取移动时的X，Y坐标

                        nowX = event.getRawX();

                        nowY = event.getRawY();

                        // 计算XY坐标偏移量

                        tranX = nowX - lastX;

                        tranY = nowY - lastY;
                        //当水平或者垂直滑动距离大于20,才算拖动事件
                        if (Math.abs(tranX) > 20 || Math.abs(tranY) > 20) {
                            drag = true;
                            params.x += tranX;

                            params.y += tranY;

                            //更新悬浮窗位置

                            mWindowManager.updateViewLayout(mView, params);

                            //记录当前坐标作为下一次计算的上一次移动的位置坐标

                            lastX = nowX;

                            lastY = nowY;
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        ivFloat.setAlpha(0.5f);
                        if (drag)  //如果是拖动拦截
                            return true;
                        break;

                }

                return false;

            }

        });

        return view;

    }

}