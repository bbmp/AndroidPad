package com.robam.common.ui.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.robam.common.R;

public class CircleProgressView extends View {

    private Paint mBackPaint, mProgPaint,mRoundPaint;   // 绘制画笔
    private RectF mRectF;       // 绘制区域
    private int[] mColorArray;  // 圆环渐变色
    private float mProgress;      // 圆环进度(0-100)

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.common_CircleProgressView);

        // 初始化背景圆环画笔
        mBackPaint = new Paint();
        mBackPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mBackPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mBackPaint.setAntiAlias(true);              // 设置抗锯齿
        mBackPaint.setDither(true);                 // 设置抖动
        mBackPaint.setStrokeWidth(typedArray.getDimension(R.styleable.common_CircleProgressView_common_backWidth, 5));
        mBackPaint.setColor(typedArray.getColor(R.styleable.common_CircleProgressView_common_backColor, Color.LTGRAY));

        // 初始化进度圆环画笔
        mProgPaint = new Paint();
        mProgPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mProgPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mProgPaint.setAntiAlias(true);              // 设置抗锯齿
        mProgPaint.setDither(true);                 // 设置抖动
        mProgPaint.setStrokeWidth(typedArray.getDimension(R.styleable.common_CircleProgressView_common_progWidth, 10));
        mProgPaint.setColor(typedArray.getColor(R.styleable.common_CircleProgressView_common_progColor, Color.BLUE));


        mRoundPaint = new Paint();
        mRoundPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mRoundPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mRoundPaint.setAntiAlias(true);              // 设置抗锯齿
        mRoundPaint.setDither(true);                 // 设置抖动
        mRoundPaint.setStrokeWidth(typedArray.getDimension(R.styleable.common_CircleProgressView_common_progWidth, 10));
        mRoundPaint.setColor(typedArray.getColor(R.styleable.common_CircleProgressView_common_progColor, Color.BLUE));

        // 初始化进度圆环渐变色
        int startColor = typedArray.getColor(R.styleable.common_CircleProgressView_common_progStartColor, -1);
        int firstColor = typedArray.getColor(R.styleable.common_CircleProgressView_common_progFirstColor, -1);
        if (startColor != -1 && firstColor != -1) mColorArray = new int[]{startColor, firstColor};
        else mColorArray = null;

        // 初始化进度
        mProgress = typedArray.getInteger(R.styleable.common_CircleProgressView_common_progress, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((viewWide > viewHigh ? viewHigh : viewWide) - (mBackPaint.getStrokeWidth() > mProgPaint.getStrokeWidth() ? mBackPaint.getStrokeWidth() : mProgPaint.getStrokeWidth()));
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        mRectF = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray.length > 1){
            //mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
            mProgPaint.setShader(new SweepGradient(getMeasuredWidth()/2, getMeasuredWidth()/2, mColorArray, new float[]{0f,0.65f} ));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(-90 - 5,getWidth()/2,getHeight()/2);
        canvas.drawArc(mRectF, 0, 360, false, mBackPaint);
        canvas.drawArc(mRectF, 5, 360 * mProgress / 100, false, mProgPaint);
//        if (mProgress <= 24.75) {
//            canvas.drawArc(mRectF, 270, 360 * mProgress / 100, false, mProgPaint);
//        } else if (mProgress > 24.75 && mProgress < 25.25) {
//            canvas.drawArc(mRectF, 270, (float) (360 * 24.75 / 100), false, mProgPaint);
//        } else if (mProgress >= 25.25 && mProgress <= 49.75) {
//            canvas.drawArc(mRectF, 270, (float) (360 * 24.75 / 100), false, mProgPaint);
//            canvas.drawArc(mRectF, (float) (360 * 0.25 / 100), (float) (360 * (mProgress-25.25) / 100), false, mProgPaint);
//        } else if (mProgress > 49.75 && mProgress < 50.25) {
//            canvas.drawArc(mRectF, 270, (float) (360 * 24.75 / 100), false, mProgPaint);
//            canvas.drawArc(mRectF, (float) (360 * 0.25 / 100), (float) (360 * (24.5) / 100), false, mProgPaint);
//        } else {
//            canvas.drawArc(mRectF, 270, (float) (360 * 24.75 / 100), false, mProgPaint);
//            canvas.drawArc(mRectF, (float) (360 * 0.25 / 100), (float) (360 * (24.5) / 100), false, mProgPaint);
//            canvas.drawArc(mRectF, 90 + (float) (360 * 0.25 / 100), (float) (360 * (mProgress-50.25) / 100), false, mProgPaint);
//        }

//        canvas.drawArc(mRectF, 270, 360 * 0.05f / 100, false, mRoundPaint);//开始圆角
//        canvas.drawArc(mRectF, 270 + 360 * mProgress / 100, 360 * 0.05f / 100, false, mRoundPaint);//结束圆角
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 获取当前进度
     *
     * @return 当前进度（0-100）
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * 设置当前进度
     *
     * @param progress 当前进度（0-100）
     */
    public void setProgress(float progress) {
        if (progress > 100)
            progress = 100;
        if (progress < 0)
            progress = 0;
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    public void setProgress(int progress, long animTime) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    /**
     * 设置背景圆环宽度
     *
     * @param width 背景圆环宽度
     */
    public void setBackWidth(int width) {
        mBackPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置背景圆环颜色
     *
     * @param color 背景圆环颜色
     */
    public void setBackColor(@ColorRes int color) {
        mBackPaint.setColor(ContextCompat.getColor(getContext(), color));
        invalidate();
    }

    /**
     * 设置进度圆环宽度
     *
     * @param width 进度圆环宽度
     */
    public void setProgWidth(int width) {
        mProgPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置进度圆环颜色
     *
     * @param color 景圆环颜色
     */
    public void setProgColor(@ColorRes int color) {
        mProgPaint.setColor(ContextCompat.getColor(getContext(), color));
        mProgPaint.setShader(null);
        invalidate();
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param startColor 进度圆环开始颜色
     * @param firstColor 进度圆环结束颜色
     */
    public void setProgColor(@ColorRes int startColor, @ColorRes int firstColor) {
        mColorArray = new int[]{ContextCompat.getColor(getContext(), startColor), ContextCompat.getColor(getContext(), firstColor)};
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param colorArray 渐变色集合
     */
    public void setProgColor(@ColorRes int[] colorArray) {
        if (colorArray == null || colorArray.length < 2) return;
        mColorArray = new int[colorArray.length];
        for (int index = 0; index < colorArray.length; index++)
            mColorArray[index] = ContextCompat.getColor(getContext(), colorArray[index]);
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }
}