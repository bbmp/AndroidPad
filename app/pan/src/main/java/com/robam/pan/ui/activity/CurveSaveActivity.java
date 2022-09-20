package com.robam.pan.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.aztec.encoder.HighLevelEncoder;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.ui.view.MarkViewAdd;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线保存
public class CurveSaveActivity extends PanBaseActivity {
    private TextView tvBack, tvSave;
    //曲线名字
    private TextView tvCurveName;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();

        tvCurveName = findViewById(R.id.tv_curve_name);
        cookChart = findViewById(R.id.cook_chart);
        setOnClickListener(R.id.tv_back, R.id.tv_save, R.id.iv_edit_name);
    }

    @Override
    protected void initData() {
        drawCurve();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_back) {
            //回到首页
            startActivity(MainActivity.class);
        } else if (id == R.id.tv_save) {
            //保存成功
            ToastUtils.showShort(this, R.string.pan_save_success);
        } else if (id == R.id.iv_edit_name) {
            //编辑曲线名字
            curveEidt();
        }
    }
    //曲线命名
    private void curveEidt() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
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
                    ToastUtils.showShort(CurveSaveActivity.this, R.string.pan_input_empty);
                    return;
                }
                tvCurveName.setText(editText.getText());
                iDialog.dismiss();
            }
        });
    }

    //曲线绘制
    private void drawCurve() {
        if (null != getIntent()) {
            ArrayList<Entry> entryList = getIntent().getParcelableArrayListExtra(PanConstant.EXTRA_ENTRY_LIST);

            ArrayList<Entry> stepList = getIntent().getParcelableArrayListExtra(PanConstant.EXTRA_STEP_LIST);

            if (null != entryList) {
                dm = new DynamicLineChartManager(cookChart, this);
                dm.setLabelCount(5, 5);
                dm.setAxisLine(true, false);
                dm.setGridLine(false, false);
                dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_chart), entryList, true, false);
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