package com.robam.common.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.robam.common.utils.ScreenUtils;

//可拖动的image
public class DragImage extends AppCompatImageView {

    private int myWidth;
    private int myHeight;
    //父控件的宽高
    private int parentWidth;
    private int parentHeight;

    //是否拖动
    private boolean isDrag = false;

    /**
     * 父控件
     */
    private ViewGroup parentView;

    public boolean isDrag() {
        return isDrag;
    }

    public DragImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        parentWidth = ScreenUtils.getWidthPixels(context);
        parentHeight = ScreenUtils.getHeightPixels(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        myWidth = getMeasuredWidth();
        myHeight = getMeasuredHeight();

    }

    private float downX;
    private float downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (this.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    downX = event.getX();
                    downY = event.getY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    final float xDistance = event.getX() - downX;
                    final float yDistance = event.getY() - downY;
                    int l, r, t, b;
                    //当水平或者垂直滑动距离大于10,才算拖动事件
                    if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {

                        isDrag = true;
                        //在父控件中左边的位置 = 当前在父控件中的位置 - 内边距 + 拖动的距离
                        l = (int) (getLeft() - parentView.getPaddingLeft() + xDistance);
                        //在父控件中的右边位置 = 在左边的位置 + 自己的宽
                        r = l + myWidth;
                        t = (int) (getTop() - parentView.getPaddingTop() + yDistance);
                        b = t + myHeight;
                        if (l < 0) {
                            //如果左边的距离超出父控件的左边有效范围，那么这个值会是负数，
                            // 为了保证在父控件中的拖动，所以设为父控件的左边的起点（包括内边距）
                            l = parentView.getPaddingLeft();
                            r = l + myWidth;
                        } else if (r > (parentWidth - parentView.getPaddingRight())) {
                            r = parentWidth - parentView.getPaddingRight();
                            l = r - myWidth;
                        }
                        if (t < 0) {
                            t = parentView.getPaddingTop();
                            b = t + myHeight;
                        } else if (b > parentHeight - parentView.getPaddingBottom()) {
                            b = parentHeight - parentView.getPaddingBottom();
                            t = b - myHeight;
                        }

                        this.layout(l, t, r, b);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isDrag) {
                        //如果是拖动，就拦截点击事件
                        return false;
                    }
                    break;
            }
            return super.onTouchEvent(event);
        }
        return false;
    }

}
