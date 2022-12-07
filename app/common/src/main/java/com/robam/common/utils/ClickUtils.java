package com.robam.common.utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class ClickUtils {
    public static void setDoubleClick(final View view) {
        final long[] firClick = {0};

        final long[] secClick = {0};
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    firClick[0] = secClick[0];

                    secClick[0] = System.currentTimeMillis();

                    if (secClick[0] - firClick[0] < 500) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public static void setLongClick(final Handler handler, final View longClickView, final long delayMillis, final View.OnLongClickListener longClickListener) {
        if (null != longClickView) {
            longClickView.setOnTouchListener(new View.OnTouchListener() {
                private int TOUCH_MAX = 50;
                private int mLastMotionX;
                private int mLastMotionY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            // 抬起时,移除已有Runnable回调,抬起就算长按了(不需要考虑用户是否长按了超过预设的时间)
                            handler.removeCallbacks(r);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (Math.abs(mLastMotionX - x) > TOUCH_MAX
                                    || Math.abs(mLastMotionY - y) > TOUCH_MAX) {
                                // 移动误差阈值
                                // xy方向判断
                                // 移动超过阈值，则表示移动了,就不是长按(看需求),移除 已有的Runnable回调
                                handler.removeCallbacks(r);
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            // 每次按下重新计时
                            // 按下前,先移除 已有的Runnable回调,防止用户多次单击导致多次回调长按事件的bug
                            handler.removeCallbacks(r);
                            mLastMotionX = x;
                            mLastMotionY = y;
                            // 按下时,开始计时
                            handler.postDelayed(r, delayMillis);
                            break;
                    }
                    return true;//onclick等其他事件不能用请改这里
                }

                private Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (longClickListener != null) {// 回调给用户,用户可能传null,需要判断null
                            longClickListener.onLongClick(longClickView);
                        }
                    }
                };
            });
        }
    }

}
