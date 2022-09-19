package com.robam.common.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.robam.common.R;

import java.text.DecimalFormat;

//标记步骤
public class MarkViewStep extends MarkerView {
    private TextView tvStep;

    private IAxisValueFormatter xAxisValueFormatter;
    DecimalFormat df = new DecimalFormat(".00");

    public MarkViewStep(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.common_layout_markview_step);
        this.xAxisValueFormatter = xAxisValueFormatter;
        tvStep = findViewById(R.id.tv_step);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容

        super.refreshContent(e, highlight);
    }

    public void setTvValue(String strValue) {
        tvStep.setText(strValue);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() / 2);
    }
}
