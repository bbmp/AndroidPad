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
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.robam.common.R;

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
    private LineDataSet lineDataSet;
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        if (colorMulti != 0){
            xAxis.setTextColor(colorMulti);
        }else {
            xAxis.setTextColor(Color.parseColor("#2fFFFFFF"));
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

        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        if (colorMulti != 0){
            yAxis.setGridColor(colorMulti);
            yAxis.setTextColor(colorMulti);
        }else {
            yAxis.setGridColor(Color.parseColor("#2fFFFFFF"));
            yAxis.setTextColor(Color.parseColor("#2fFFFFFF"));
        }

        yAxis.setLabelCount(5);
        //保证Y轴从0开始，不然会上移一点
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(250f);
        rightAxis.setAxisMinimum(0f);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);//设置X轴颜色
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
        if(list==null||list.isEmpty()){
            lineDataSet = new LineDataSet(null, name);
        }else{
            lineDataSet = new LineDataSet(list, name);
        }
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(1.5f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(color);
        //不显示折线上的值
        lineDataSet.setDrawValues(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(isDrawFilled);
        //设置填充
//        if (Utils.getSDKInt() >= 18) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.common_chart_fill);
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
    public void initLineDataSet(String name, int color,List list,boolean isDrawFilled , int colorMulti) {
        if(list==null||list.isEmpty()){
            lineDataSet = new LineDataSet(null, name);
        }else{
            lineDataSet = new LineDataSet(list, name);
        }
        this.colorMulti = colorMulti ;
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(1.5f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(color);
        //不显示折线上的值
        lineDataSet.setDrawValues(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(isDrawFilled);
        //设置填充
//        if (Utils.getSDKInt() >= 18) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.common_chart_fill);
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
     * 添加一条额外得垂直线
     * @param name
     * @param color
     * @param list
     */
    public void addExtraLine(String name, int color,List list) {
        if(list==null||list.isEmpty()){
            lineDataSet = new LineDataSet(null, name);
        }else{
            lineDataSet = new LineDataSet(list, name);
        }
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

    public void update() {
        lineData.notifyDataChanged();
        lineChart.invalidate();
    }

    /**
     * 动态添加数据（一条折线图）
     *逐点添加
     * @param entry 数据点
     * @param setID 曲线ID
     */
    public void addEntry(Entry entry,int setID) {

        lineData.addEntry(entry, 1);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        //设置在曲线图中显示的最大数量
//        lineChart.setVisibleXRangeMaximum(7);
        //移到某个位置
//        lineChart.moveViewToX(lineData.getEntryCount() - 6);
    }

    public void addEntry(Entry entry) {
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);

        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
//        lineChart.setVisibleXRangeMaximum(7);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
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

