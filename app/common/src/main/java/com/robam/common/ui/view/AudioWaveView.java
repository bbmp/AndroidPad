package com.robam.common.ui.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.robam.common.R;

import java.util.Random;

public class AudioWaveView extends View {
    private Paint paint;
    private RectF rectF1;
    private RectF rectF2;
    private RectF rectF3;
    private RectF rectF4;
    private RectF rectF5;
    private RectF rectF6;
    private RectF rectF7;
    private RectF rectF8;
    private RectF rectF9;
    private RectF rectF10;
    private RectF rectF11;
    private RectF rectF12;
    private int viewWidth;
    private int viewHeight;
    /** 每个条的宽度 */
    private int rectWidth;
    /** 条数 */
    private int columnCount = 12;

    private int maxColumnCount = 12;
    /** 条间距 */
    private final int space = 6;
    /** 条随机高度 */
    private int randomHeight;
    //高度
    private int maxHeight, minHeight;
    private Random random;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
            handler.sendEmptyMessageDelayed(0, 200); //每间隔200毫秒发送消息刷新
        }
    };

    public AudioWaveView(Context context) {
        super(context);
        init(context, null);
    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setHeight(int maxHeight, int minHeight, int columnCount) {
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.columnCount = columnCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        rectWidth = (viewWidth - space * (maxColumnCount - 1)) / maxColumnCount;
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#33ffffff"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        random = new Random();
//        View view = inflate(context, R.layout.common_layout_round_rect, this);
//        View rect1 = view.findViewById(R.id.iv_rect1);
//        rect1.setPivotX(0f);
//        rect1.setPivotY(45f);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rect1, "scaleY", 0.1f, 0.3f);
//        scaleY.setDuration(500);
//        scaleY.setRepeatMode(ValueAnimator.REVERSE);
//        scaleY.setInterpolator(new LinearInterpolator());
//        scaleY.setRepeatCount(ValueAnimator.INFINITE);
//        scaleY.start();
        initRect();
        handler.sendEmptyMessageDelayed(0, 200); //每间隔200毫秒发送消息刷新
    }

    private void initRect() {
        rectF1 = new RectF();
        rectF2 = new RectF();
        rectF3 = new RectF();
        rectF4 = new RectF();
        rectF5 = new RectF();
        rectF6 = new RectF();
        rectF7 = new RectF();
        rectF8 = new RectF();
        rectF9 = new RectF();
        rectF10 = new RectF();
        rectF11 = new RectF();
        rectF12 = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (maxHeight != 0 && minHeight != 0) {
            int left = rectWidth + space;
            int height = (int) (viewHeight / 9.0); //分成9段
            int start = (viewWidth - columnCount*rectWidth - (columnCount - 1)*space) / 2;
            //画每个条之前高度都重新随机生成
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));

            rectF1.set(start + left * 0, randomHeight, start + left * 0 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF2.set(start + left * 1, randomHeight, start + left * 1 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF3.set(start + left * 2, randomHeight, start + left * 2 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF4.set(start + left * 3, randomHeight, start + left * 3 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF5.set(start + left * 4, randomHeight, start + left * 4 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF6.set(start + left * 5, randomHeight, start + left * 5 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF7.set(start + left * 6, randomHeight, start + left * 6 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF8.set(start + left * 7, randomHeight, start + left * 7 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF9.set(start + left * 8, randomHeight, start + left * 8 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF10.set(start + left * 9, randomHeight, start + left * 9 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF11.set(start + left * 10, randomHeight, start + left * 10 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF12.set(start + left * 11, randomHeight, start + left * 11 + rectWidth, viewHeight);

            canvas.drawRoundRect(rectF1, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF2, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF3, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF4, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF5, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF6, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            if (columnCount == 8) {
                canvas.drawRoundRect(rectF7, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF8, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            }
            if (columnCount == 12) {
                canvas.drawRoundRect(rectF7, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF8, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF9, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF10, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF11, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
                canvas.drawRoundRect(rectF12, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            }
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}