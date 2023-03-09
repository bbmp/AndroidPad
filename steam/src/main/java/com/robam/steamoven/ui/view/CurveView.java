package com.robam.steamoven.ui.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.robam.steamoven.R;

import java.util.ArrayList;
import java.util.List;

public class CurveView extends View {

    private int axisColor;		// 轴线颜色
    private float axisWith;     // 轴线宽度
    private int[] lineColor = new int[]{Color.BLUE, Color.CYAN, Color.RED, Color.GREEN, Color.YELLOW}; 	// 数据线/点颜色
    private int[] lineWidth = null;	// 数据线/点宽度
    private int textColor;      // 文本颜色
    private int textSize;      	// 文本字体
    private int leftMargins;    // 左边距
    private int rightMargins;  	// 右边距
    private int bottomMargins;  // 下边距
    private int topMargins;    	// 上边距
    private float XScale;       // X的刻度长度
    private int xLength;        // X轴的长度
    private int YScale;         // Y的刻度长度
    private int yLength;        // Y轴的长度
    private List<Integer> data;   // y轴数据
    private boolean hasYAxis;  	// 显示Y轴轴线
    private boolean hasXAxis;  	// 显示X轴轴线
    private boolean hasYScale;  // 显示Y轴刻度
    private boolean hasXScale;  // 显示X轴刻度
    private int initX;			// 原点X
    private int initY;			// 原点Y
    private Paint axisPaint, textPaint, linePaint, areaPaint;
    private int[] areColor;
    private float mLineSmoothness = 0.18f;

    public CurveView(Context context) {
        super(context);
        initCureData(context);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCureData(context);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCureData(context);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCureData(context);
    }


    private void initCureData(Context context){
        initDataList();
        lineColor = new int[]{context.getResources().getColor(R.color.steam_chart)};
        areColor = new int[]{context.getResources().getColor(R.color.steam_chart_smooth)};
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null!= data) {
            initDataList();
            init();
            initPaint();
            lineDraw(canvas);
        }
    }

    private void initDataList(){
        data = new ArrayList<>();
        int height = getHeight();
        float maxLevel = 100;
        float baseHeight = height/maxLevel;
        data.add(0);
        data.add((int) (baseHeight * 5));
        data.add((int) (baseHeight*10));
        data.add((int) (baseHeight*30));
        data.add((int) (baseHeight*70));
        data.add((int) (baseHeight*72));
        data.add((int) (baseHeight*70));
        data.add((int) (baseHeight*72));
        data.add((int) (baseHeight*70));
        data.add((int) (baseHeight*72));
    }

    //绘制面积图
    private void lineDraw(Canvas canvas){
        List<Integer> temp = data;
        final int MAX = getMax(data);
        linePaint.setColor(getLineColor()[0]);
        areaPaint.setColor(areColor[0]);
        Path linePath = new Path();
        Path areaPath = new Path();
        areaPath.moveTo(initX, initY);
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;
        for (int i = 0; i < temp.size(); i ++) {
            if (Float.isNaN(currentPointX)) {
                currentPointX = getSpinnerPoint(i,MAX,temp.get(i)).x;
                currentPointY = getSpinnerPoint(i,MAX,temp.get(i)).y;
            }
            if (Float.isNaN(previousPointX)) {
                //是第一个点?
                if (i > 0) {
                    previousPointX = getSpinnerPoint(i - 1,MAX,temp.get(i-1)).x;
                    previousPointY =  getSpinnerPoint(i - 1,MAX,temp.get(i -1)).y;
                } else {
                    //用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是前两个点?
                if (i > 1) {
                    prePreviousPointX = getSpinnerPoint(i - 2,MAX,temp.get(i-2)).x;
                    prePreviousPointY = getSpinnerPoint(i - 2,MAX,temp.get(i-2)).y;
                } else {
                    //当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (i < temp.size() - 1) {
                nextPointX = getSpinnerPoint(i +1,MAX,temp.get(i+1)).x;
                nextPointY = getSpinnerPoint(i + 1,MAX,temp.get(i+1)).y;
            } else {
                //用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (i == 0) {
                linePath.moveTo(currentPointX, currentPointY);
                areaPath.lineTo(currentPointX, currentPointY);
            } else{
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (mLineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (mLineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (mLineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (mLineSmoothness * secondDiffY);

                linePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);

                areaPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);

            }
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
        areaPath.lineTo(leftMargins + (temp.size() - 1) * XScale, initY);
        canvas.drawPath(areaPath, areaPaint);
        canvas.drawPath(linePath, linePaint);
    }


    private void init(){
        //初始化绘图范围
        setLeftMargins(50);
        setRightMargins(50);
        setTopMargins(50);
        setBottomMargins(50);
        yLength = getHeight() - bottomMargins - topMargins;
        xLength = getWidth() - leftMargins - rightMargins;
        initY = yLength + getTopMargins();
        initX = getLeftMargins();
        XScale = xLength / (data.size() - 1);

        //初始化轴显示
        setHasXAxis(true);
        setHasYAxis(true);
        setHasXScale(true);
        setHasYData(true);

        //初始化轴线画笔
        axisPaint = new Paint();
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setAntiAlias(true);
        axisPaint.setColor(Color.GRAY);
        axisPaint.setStrokeWidth(1);

        //初始化折线画笔
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        BlurMaskFilter PaintBGBlur = new BlurMaskFilter(
                1, BlurMaskFilter.Blur.SOLID);
        linePaint.setMaskFilter(PaintBGBlur);
        linePaint.setStrokeWidth(3);

        //初始化面积画笔
        areaPaint = new Paint();
        areaPaint.setStyle(Paint.Style.FILL);
        PaintBGBlur = new BlurMaskFilter(
                1, BlurMaskFilter.Blur.INNER);
        areaPaint.setMaskFilter(PaintBGBlur);
        areaPaint.setColor(Color.BLACK);

        //初始化文本画笔
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(11);
        textPaint.setTextAlign(Paint.Align.CENTER);


    }

    //自定义画笔
    private void initPaint() {
        if (getAxisColor() != 0)
            axisPaint.setColor(getAxisColor());
        if (getAxisWidth() != 0)
            axisPaint.setStrokeWidth(getAxisWidth());
        if (getTextColor() != 0)
            textPaint.setColor(getTextColor());
        if (getTextSize() != 0)
            textPaint.setTextSize(getTextSize());
    }

    //定位
    private int YCoord(int Max, int y){
        return (int) (topMargins + yLength  * (1 - y / (float) Max) );
    }

    //获取图例最大值
    private int getMax(List<Integer> data){
        int max = 0;
        for (int j = 0; j< data.size(); j++)
        {
            if(data.get(j) > max){
                max = data.get(j);
            }
        }
        return max;
    }



    //轴线设置
    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisolor(int axisColor) {
        this.axisColor = axisColor;
    }

    public float getAxisWidth() {
        return axisWith;
    }

    public void setAxisWidth(int axisWith) {
        this.axisWith = axisWith;
    }

    //折线设置
    public void setLineColor(int[] lineColor) {
        this.lineColor = lineColor;
    }

    public int[] getLineColor() {
        return lineColor;
    }

    public void setLineWidth(int[] lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int[] getLineWidth() {
        return lineWidth;
    }

    //X轴设置
    public boolean isHasXAxis() {
        return hasXAxis;
    }

    public void setHasXAxis(boolean hasXAxis) {
        this.hasXAxis = hasXAxis;
    }

    public float getXScale() {
        return XScale;
    }

    public void setXScale(float xScale) {
        XScale = xScale;
    }

    public int getXLength() {
        return xLength;
    }

    public void setXLength(int xLength) {
        this.xLength = xLength;
    }

    public boolean isHasXScale() {
        return hasXScale;
    }

    public void setHasXScale(boolean hasXScale) {
        this.hasXScale = hasXScale;
    }

    //Y轴设置
    public boolean isHasYAxis() {
        return hasYAxis;
    }

    public void setHasYAxis(boolean hasYAxis) {
        this.hasYAxis = hasYAxis;
    }

    public int getYScale() {
        return YScale;
    }

    public void setYScale(int yScale) {
        YScale = yScale;
    }

    public int getYLength() {
        return yLength;
    }

    public void setYLength(int yLength) {
        this.yLength = yLength;
    }

    public boolean isHasYScale() {
        return hasYScale;
    }

    public void setHasYData(boolean hasYScale) {
        this.hasYScale = hasYScale;
    }

    //文本设置
    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    //边距设置
    public int getLeftMargins() {
        return leftMargins;
    }

    public void setLeftMargins(int leftMargins) {
        this.leftMargins = leftMargins;
    }

    public int getRightMargins() {
        return rightMargins;
    }

    public void setRightMargins(int rightMargins) {
        this.rightMargins = rightMargins;
    }

    public int getBottomMargins() {
        return bottomMargins;
    }

    public void setBottomMargins(int buttomMargins) {
        this.bottomMargins = buttomMargins;
    }

    public int getTopMargins() {
        return topMargins;
    }

    public void setTopMargins(int topMargins) {
        this.topMargins = topMargins;
    }

    private Point getSpinnerPoint(int valueIndex,int maxY,int value) {
        int x = (int) (leftMargins + valueIndex* XScale);
        int y =  YCoord(maxY, value);
        return new Point(x, y);
    }



}
