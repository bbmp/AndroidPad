package com.robam.common.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.robam.common.R;

public class MarkViewAdd extends MarkerView {

    private TextView tv_mark;
    private IAxisValueFormatter xAxisValueFormatter;
    private MarkerViewAddListener markerViewAddListener;

    public void setMarkerViewAddListener(MarkerViewAddListener markerViewAddListener) {
        this.markerViewAddListener = markerViewAddListener;
    }

    public MarkViewAdd(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.common_layout_markview_add);
        this.xAxisValueFormatter = xAxisValueFormatter;
        tv_mark = findViewById(R.id.tv_mark);
        tv_mark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != markerViewAddListener)
                    markerViewAddListener.onMarkerViewAdd();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, 0);
    }

    public interface MarkerViewAddListener {
        void onMarkerViewAdd();
    }
}
