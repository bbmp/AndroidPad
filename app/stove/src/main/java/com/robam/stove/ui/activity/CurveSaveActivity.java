package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.robam.common.manager.LineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.ArrayList;
import java.util.List;

//曲线保存
public class CurveSaveActivity extends StoveBaseActivity {
    private LineChart lineChart;//曲线图带限制线
    private LineChartManager lineChartManager;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();
        showRightCenter();

        lineChart = findViewById(R.id.lineChart);
        lineChartManager = new LineChartManager(lineChart);
        setOnClickListener(R.id.tv_back, R.id.tv_save, R.id.iv_edit_name);
    }

    @Override
    protected void initData() {
        setLineChartData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_back) {
            //回到首页
            startActivity(MainActivity.class);
        } else if (id == R.id.tv_save) {
            //保存成功
            ToastUtils.showShort(this, R.string.stove_save_success);
        } else if (id == R.id.iv_edit_name) {
            //编辑曲线名字
            curveEidt();
        }
    }
    //曲线名称
    private void curveEidt() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, R.id.tv_cancel);
        iDialog.show();
        ClearEditText editText = iDialog.getRootView().findViewById(R.id.et_curve_name);
        //单独处理确认事件
        iDialog.getRootView().findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //校验输入是否为空
                if (TextUtils.isEmpty(editText.getText())) {
                    ToastUtils.showShort(CurveSaveActivity.this, R.string.stove_input_empty);
                    return;
                }
                iDialog.dismiss();
            }
        });
    }

    private void setLineChartData() {
        //设置X轴数据
        ArrayList xValues = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            xValues.add((float) i);
        }
        //设置Y轴数据
        List<Float> yValue = new ArrayList<>();
        //一条曲线模拟数据
        for (int j = 0; j < 12; j++) {
            yValue.add((float) (Math.random() * 80));
        }

        //设置数据并显示一条曲线
        lineChartManager.showLineChart(xValues, yValue, "", Color.BLUE);
        lineChartManager.setDescription("");
        //Y轴0-100 分10格
//        lineChartManager.setYAxis(100, 0, 11);
        //警戒线80 红色
//        lineChartManager.setHightLimitLine(80, "高温报警", Color.RED);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                Log.e(TAG, "----e:" + e.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }
}