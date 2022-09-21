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
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.manager.LineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.ArrayList;
import java.util.List;

//曲线保存
public class CurveSaveActivity extends StoveBaseActivity {
    private IDialog editDialog;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();
        showRightCenter();

        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.stove_no_curve_data)); //没有数据时显示的文字
        setOnClickListener(R.id.tv_back, R.id.tv_save, R.id.iv_edit_name);
    }

    @Override
    protected void initData() {
        drawCurve();
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
        if (null == editDialog) {
            editDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
            editDialog.setCancelable(false);
            editDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }, R.id.tv_cancel);
            ClearEditText editText = editDialog.getRootView().findViewById(R.id.et_curve_name);
            //单独处理确认事件
            editDialog.getRootView().findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //校验输入是否为空
                    if (TextUtils.isEmpty(editText.getText())) {
                        ToastUtils.showShort(CurveSaveActivity.this, R.string.stove_input_empty);
                        return;
                    }
                    editDialog.dismiss();
                }
            });
        }
        editDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != editDialog && editDialog.isShow())
            editDialog.dismiss();
    }

    //曲线绘制
    private void drawCurve() {
        if (null != getIntent()) {
            ArrayList<Entry> entryList = getIntent().getParcelableArrayListExtra(StoveConstant.EXTRA_ENTRY_LIST);

            ArrayList<Entry> stepList = getIntent().getParcelableArrayListExtra(StoveConstant.EXTRA_STEP_LIST);

            if (null != entryList) {
                dm = new DynamicLineChartManager(cookChart, this);
                dm.setLabelCount(5, 5);
                dm.setAxisLine(true, false);
                dm.setGridLine(false, false);
                dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.stove_chart), entryList, true, false);
                cookChart.notifyDataSetChanged();
                if (null != stepList) {
                    MarkViewStep mv = new MarkViewStep(this, cookChart.getXAxis().getValueFormatter());
                    mv.setChartView(cookChart);
                    cookChart.setMarker(mv);
                    List<Highlight> highlights = new ArrayList<>();
                    int dataIndex = 1;
                    for (Entry entry: stepList) {
                        highlights.add(new Highlight(entry.getX(), entry.getY(), 0, dataIndex));
                        dataIndex++;
                    }
                    cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
                }
            }
        }

    }
}