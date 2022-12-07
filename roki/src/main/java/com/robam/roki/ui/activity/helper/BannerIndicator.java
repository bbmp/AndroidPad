package com.robam.roki.ui.activity.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.youth.banner.indicator.BaseIndicator;
import com.youth.banner.util.BannerUtils;

public class BannerIndicator extends BaseIndicator {
    private int mNormalRadius;
    private int mSelectedRadius;
    private int maxRadius;

    private int width;
    private int height;
    private int radiusLine;


    public BannerIndicator(Context context) {
        this(context, (AttributeSet)null);

//        mPaint.setTextSize(BannerUtils.dp2px(10));
//        mPaint.setTextAlign(Paint.Align.CENTER);
        width = (int) BannerUtils.dp2px(8);
        height = (int) BannerUtils.dp2px(2);
        radiusLine = (int) BannerUtils.dp2px(20);


    }

    public BannerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNormalRadius = this.config.getNormalWidth() / 2;
        this.mSelectedRadius = this.config.getSelectedWidth() / 2;


    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            this.mNormalRadius = this.config.getNormalWidth() / 2;
            this.mSelectedRadius = this.config.getSelectedWidth() / 2;
            this.maxRadius = Math.max(this.mSelectedRadius, this.mNormalRadius);
            int width = (count - 1) * this.config.getIndicatorSpace() + this.config.getSelectedWidth() + this.config.getNormalWidth() * (count - 1);
            this.setMeasuredDimension(width+30, Math.max(this.config.getNormalWidth(), this.config.getSelectedWidth()));
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            float left = 0.0F;

            for(int i = 0; i < count; ++i) {
                this.mPaint.setColor(this.config.getCurrentPosition() == i ? this.config.getSelectedColor() : this.config.getNormalColor());
                int indicatorWidth = this.config.getCurrentPosition() == i ? this.config.getSelectedWidth() : this.config.getNormalWidth();
                int radius = this.config.getCurrentPosition() == i ? this.mSelectedRadius : this.mNormalRadius;
//                if (this.config.getCurrentPosition() == i){

                RectF rectF = new RectF(left, 0, left + indicatorWidth, height);
//                    mPaint.setColor(Color.parseColor("#ff61acff"));
                canvas.drawRoundRect(rectF, radius, radius, mPaint);
                left += (float)(indicatorWidth  + this.config.getIndicatorSpace());

//                }else {
//                    RectF rectF = new RectF(left, 0, left + width, height);
//                    mPaint.setColor(Color.parseColor("#80999999"));
//                    canvas.drawRoundRect(rectF, radiusLine, radiusLine, mPaint);
//                    left += (float)(width  + this.config.getIndicatorSpace());
//                }

//                left += (float)(indicatorWidth + this.config.getIndicatorSpace());
            }

        }
    }
}
