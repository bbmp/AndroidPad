package com.robam.common.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 抛掷速度的控制
 */
public class RecyclerSpeedView extends RecyclerView {
    private float speed_divisor = 0.45f;


    public RecyclerSpeedView(@NonNull Context context) {
        super(context);
    }

    public RecyclerSpeedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerSpeedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= speed_divisor; // keep it less than 1.0 to slowdown
        return super.fling(velocityX, velocityY);
    }
}
