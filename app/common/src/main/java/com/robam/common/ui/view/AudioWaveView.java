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
    private int viewWidth;
    private int viewHeight;
    /** 每个条的宽度 */
    private int rectWidth;
    /** 条数 */
    private int columnCount = 8;
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

    public void setHeight(int maxHeight, int minHeight) {
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        rectWidth = (viewWidth - space * (columnCount - 1)) / columnCount;
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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (maxHeight != 0 && minHeight != 0) {
            int left = rectWidth + space;
            int height = (int) (viewHeight / 9.0); //分成9段
            //画每个条之前高度都重新随机生成
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));

            rectF1.set(left * 0, randomHeight, left * 0 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF2.set(left * 1, randomHeight, left * 1 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF3.set(left * 2, randomHeight, left * 2 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF4.set(left * 3, randomHeight, left * 3 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF5.set(left * 4, randomHeight, left * 4 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF6.set(left * 5, randomHeight, left * 5 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF7.set(left * 6, randomHeight, left * 6 + rectWidth, viewHeight);
            randomHeight = (viewHeight - height * (random.nextInt(maxHeight) % (maxHeight - minHeight + 1) + minHeight));
            rectF8.set(left * 7, randomHeight, left * 7 + rectWidth, viewHeight);

            canvas.drawRoundRect(rectF1, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF2, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF3, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF4, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF5, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF6, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF7, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
            canvas.drawRoundRect(rectF8, (float) (rectWidth / 2.0), (float) (rectWidth / 2.0), paint);
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}