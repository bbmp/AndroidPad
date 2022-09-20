package com.robam.common.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.robam.common.R;
import com.robam.common.utils.LogUtils;

import java.util.List;

public class MarkViewAdd extends MarkerView {

    private TextView tvStep;
    private TextView tvMark;
    private ImageView ivTriangle;
    private IAxisValueFormatter xAxisValueFormatter;
    public float drawingPosX;
    public float drawingPosY;

    public MarkViewAdd(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.common_layout_markview_add);
        this.xAxisValueFormatter = xAxisValueFormatter;
        tvMark = findViewById(R.id.tv_mark);
        ivTriangle = findViewById(R.id.iv_triangle);
        tvStep = findViewById(R.id.tv_step);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Chart chart = getChartView();
        if (chart instanceof LineChart) {
            LineData lineData = ((LineChart) chart).getLineData();
            //获取所有hilight
            Highlight[] highlights = chart.getHighlighted();

            if (highlight.getDataSetIndex() == 0) {//第一条线
                tvMark.setVisibility(VISIBLE);
                ivTriangle.setVisibility(VISIBLE);
                tvStep.setVisibility(GONE);
            } else {
                tvMark.setVisibility(GONE);
                ivTriangle.setVisibility(GONE);
                tvStep.setVisibility(VISIBLE);
                int index = highlight.getDataIndex();
                tvStep.setText(index + "");
            }
        }
        //展示自定义X轴值 后的X轴内容
        super.refreshContent(e, highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);
        this.drawingPosX = posX + offset.x;
        this.drawingPosY = posY + offset.y;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() / 2);
    }

}
