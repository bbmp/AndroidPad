package com.robam.common.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class RotateView extends AppCompatImageView {

    private float degree;

    private int centerX, centerY;
    private float intervalDeg = 2f;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {

        @Override

        public void run() {

            degree += intervalDeg;

            if (degree > 360f) {

                degree -= 360f;

            }

            invalidate();

            handler.postDelayed(this, 16l);

        }

    };

    public RotateView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public void setDif(float degree) {
        intervalDeg = degree;
    }

    public void start() {

        handler.post(runnable);

    }

    public void pause() {

        handler.removeCallbacks(runnable);

    }

    public void destroy() {

        handler.removeCallbacksAndMessages(null);

    }

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        centerX = getWidth() / 2;

        centerY = getHeight() / 2;

    }

    @Override

    public void draw(Canvas canvas) {

        canvas.save();

        canvas.rotate(degree, centerX, centerY);

        super.draw(canvas);

        canvas.restore();

    }

    @Override

    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();

        handler.removeCallbacksAndMessages(null);

    }

}
