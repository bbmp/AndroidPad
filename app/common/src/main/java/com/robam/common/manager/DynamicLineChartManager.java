package com.robam.common.manager;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.robam.common.R;
import com.robam.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态折线图管理器
 */
public class DynamicLineChartManager {
    private LineChart lineChart;
    //    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private YAxis yAxis;
    private LineData lineData;
//    private LineDataSet lineDataSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();
    private Context context;

    int colorMulti ;

    //一条曲线
    public DynamicLineChartManager(LineChart mLineChart, Context mContext) {
        this.lineChart = mLineChart;
        this.context = mContext;
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        yAxis = lineChart.getAxisLeft();
        initLineChart();
//        initLineDataSet(name, color);

    }
    /**
     * 初始化LineChar图表对象
     */
    private void initLineChart() {

        // 不显示数据描述
        lineChart.getDescription().setEnabled(false);
        //禁止x轴y轴同时进行缩放
        lineChart.setPinchZoom(false);
        lineChart.getAxisRight().setEnabled(false);//关闭右侧Y轴
        lineChart.setDrawGridBackground(false);
        lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
        lineChart.setScaleEnabled(false);
        lineChart.setHighlightPerTapEnabled(false); //禁止点击
        lineChart.setHighlightPerDragEnabled(false); //禁止拖动
//        lineDataSet.setHighlightEnabled(false);
        /***折线图例 标签 设置***/
        Legend legend = lineChart.getLegend();
        //设置显示类型，LINE(线) CIRCLE(圆) SQUARE(方) EMPTY(无)  等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.EMPTY);
        legend.setTextSize(0f);
        legend.setEnabled(false);
        //描述显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);


        //X轴设置显示位置在底部
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        if (colorMulti != 0){
            xAxis.setTextColor(colorMulti);
        }else {
            xAxis.setTextColor(Color.parseColor("#a6FFFFFF"));
        }

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                return timeList.get((int) value % timeList.size());
                if (value == 0) {
                    return "";
                } else if (value > 60){
                    if (value%60 == 0){
                        return (int) value/60 + "min" ;
                    }
                    return (int) (value / 60) + "min" + (int) (value % 60) + "s";
                }
                return (int) value + "s";
            }
        });

        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        if (colorMulti != 0){
            yAxis.setGridColor(colorMulti);
            yAxis.setTextColor(colorMulti);
        }else {
            yAxis.setGridColor(Color.parseColor("#2fFFFFFF"));
            yAxis.setTextColor(Color.parseColor("#a6FFFFFF"));
        }

        yAxis.setLabelCount(5);
        //保证Y轴从0开始，不然会上移一点
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(250f);
        rightAxis.setAxisMinimum(0f);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.parseColor("#2fFFFFFF"));//设置X轴颜色
        xAxis.setDrawAxisLine(true);//是否绘制X轴
        xAxis.setDrawGridLines(false);//是否绘制X轴网格线
        xAxis.setGridColor(Color.parseColor("#2fFFFFFF"));

//        LineChartMarkView mv = new LineChartMarkView(context, xAxis.getValueFormatter());
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);

        //添加一个空的 LineData
        lineData = new LineData(lineDataSets);
        lineChart.setData(lineData);
//        //设置在曲线图中显示的最大数量
//        lineChart.setVisibleXRangeMaximum(7);
    }
    //设置坐标轴label数量
    public void setLabelCount(int xCount, int yCount) {
        xAxis.setLabelCount(xCount, false);
        yAxis.setLabelCount(yCount, false);
    }
    //是否绘制坐标轴
    public void setAxisLine(boolean xAxisLine, boolean yAxisLine) {
        xAxis.setDrawAxisLine(xAxisLine);//是否绘制X轴
        if (xAxisLine)
            xAxis.setAxisLineColor(Color.parseColor("#2fFFFFFF"));
        yAxis.setDrawAxisLine(yAxisLine);
        if (yAxisLine)
            yAxis.setAxisLineColor(Color.parseColor("#2fFFFFFF"));
    }
    //是否绘制网格线
    public void setGridLine(boolean xGridLine, boolean yGridLine) {
        xAxis.setDrawGridLines(xGridLine);
        if (xGridLine)
            xAxis.setGridColor(Color.parseColor("#2fFFFFFF"));
        yAxis.setDrawGridLines(yGridLine);
        if (yGridLine)
            yAxis.setGridColor(Color.parseColor("#2fFFFFFF"));
    }
    //是否缩放
    public void setScaled(List list) {
        float ratio = (float) list.size()/(float) 5;
        lineChart.zoom(ratio, 1.0f, 0, 0);
        lineChart.setDragEnabled(true);

        lineChart.setScaleEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setScaleXEnabled(true);
//        xAxis.setAxisMaximum(20);
        xAxis.setAvoidFirstLastClipping(true);
    }

    public void setAxisMaximum(float maxYValue){
        yAxis.setAxisMaximum(maxYValue);
    }

    /**
     *  初始化折线(一条线)
     *
     *  lineDataSet 是针对本条曲线的配置对象
     *
     * @param name  曲线名称
     * @param color  颜色
     * @param list    曲线列表数据
     * @param isDrawFilled  是否填充
     */
    public void initLineDataSet(String name, int color, List list,boolean isDrawFilled, boolean isDash) {
        Drawable drawable = null;

        LineDataSet lineDataSet = new LineDataSet(list, name);

        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(Color.TRANSPARENT);
        //不显示折线上的值
        lineDataSet.setDrawValues(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(true);
        //设置曲线填充
        lineDataSet.setDrawFilled(isDrawFilled);
        //设置填充
//        if (Utils.getSDKInt() >= 18) {
        if (isDash) {//虚线
            lineDataSet.enableDashedLine(10f, 5f, 0f);
            drawable = ContextCompat.getDrawable(context, R.drawable.common_chart_dash_fill);
        } else
            drawable = ContextCompat.getDrawable(context, R.drawable.common_chart_fill);
        lineDataSet.setFillDrawable(drawable);
//        } else {
//            lineDataSet.setFillColor(context.getResources().getColor(R.color.line_chart_easy));
//        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setDrawCircles(true);//是否绘制两个点之间的圆点
        lineDataSet.setDrawCirclesLast(true);//只绘制最后一个圆点

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSets.add(lineDataSet);
        lineData.notifyDataChanged();
        lineChart.invalidate();

    }

    /**
     *  初始化折线(一条线)
     *
     *  lineDataSet 是针对本条曲线的配置对象
     *
     * @param name  曲线名称
     * @param color  颜色
     * @param list    曲线列表数据
     * @param isDrawFilled  是否填充
     */
    public void initLineDataSet(String name, int color,List list,boolean isDrawFilled) {

        LineDataSet lineDataSet = new LineDataSet(list, name);

        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(10f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(Color.parseColor("#ffff9a4d"));
        lineDataSet.setHighLightColor(color);
        lineDataSet.setDrawHighlightIndicators(false);
        //不显示折线上的值
        lineDataSet.setDrawValues(false);
        lineDataSet.setValueTextColor(Color.WHITE);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(isDrawFilled);
        lineDataSet.enableDashedLine(0f, 5f, 0f);
        lineDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int index = lineDataSet.getEntryIndex(entry) + 1;
                LogUtils.e("index=" + index);
                return index + "";
            }
        });

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(15f);
        lineDataSet.setDrawCircles(false);//是否绘制两个点之间的圆点
        lineDataSet.setDrawCirclesLast(false);//只绘制最后一个圆点

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSets.add(lineDataSet);
        lineData.notifyDataChanged();
        lineChart.invalidate();
    }

    /**
     * 添加一条额外得垂直线
     * @param name
     * @param color
     * @param list
     */
    public void addExtraLine(String name, int color,List list) {
        LineDataSet lineDataSet = new LineDataSet(list, name);

        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(1.5f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(color);
        //显示折线上的值
        lineDataSet.setDrawValues(true);
        //设置曲线填充
        lineDataSet.setDrawFilled(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(false);
        //设置填充
//        if (Utils.getSDKInt() >= 18) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.common_chart_fill);
        lineDataSet.setFillDrawable(drawable);
//        } else {
//            lineDataSet.setFillColor(context.getResources().getColor(R.color.line_chart_easy));
//        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(15f);
        lineDataSet.setValueTextColor(color);
        lineDataSet.setDrawCircles(true);//是否绘制两个点之间的圆点
        lineDataSet.setDrawCirclesLast(true);//只绘制最后一个圆点

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value > 0) {
                    return "预热完成";
                } else {
                    return "";
                }

            }
        });

        lineDataSets.add(lineDataSet);
        lineData.notifyDataChanged();
        lineChart.invalidate();
    }


    public void addEntry(Entry entry, int dataSetIndex) {

        ILineDataSet iLineDataSet = lineData.getDataSetByIndex(dataSetIndex);

        if (null != iLineDataSet && !iLineDataSet.contains(entry)) { //未添加过的点
            iLineDataSet.addEntry(entry);
            //通知数据已经改变
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            //设置在曲线图中显示的最大数量
            lineChart.setVisibleXRangeMaximum(300);
            //移到某个位置
            ILineDataSet iLineDataSet1 = lineData.getDataSetByIndex(0);
            if (null != iLineDataSet1)
                lineChart.moveViewToX(iLineDataSet1.getEntryCount() * 2);
        }
    }
    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        yAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        yAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }
    public void setLeftLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        xAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }

}

