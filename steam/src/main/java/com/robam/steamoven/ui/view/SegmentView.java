package com.robam.steamoven.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SegmentView extends FrameLayout {
    public SegmentView(@NonNull Context context) {
        super(context);
    }

    public SegmentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SegmentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SegmentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initChildView(Context context){
       //LayoutInflater.from(context).inflate()
    }

}
