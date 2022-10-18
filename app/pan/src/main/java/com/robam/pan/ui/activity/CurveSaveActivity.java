package com.robam.pan.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.LineChartDataBean;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.ClearEditText;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.ChartDataUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.common.constant.PanConstant;
import com.robam.pan.device.HomePan;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;

import java.util.ArrayList;
import java.util.List;

//曲线保存
public class CurveSaveActivity extends PanBaseActivity {
    private TextView tvBack, tvSave;
    //曲线名字
    private TextView tvCurveName;

    private IDialog editDialog;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    private List<CurveStep> curveSteps = new ArrayList<>();

    private long curveId; //曲线id
    private int needTime;

    private String curveStageParams;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        showCenter();

        tvCurveName = findViewById(R.id.tv_curve_name);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.pan_no_curve_data)); //没有数据时显示的文字
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
            //保存曲线
            saveCurve();
        } else if (id == R.id.iv_edit_name) {
            //编辑曲线名字
            curveEidt();
        }
    }
    //保存曲线
    private void saveCurve() {
        CloudHelper.curveSave(this, AccountInfo.getInstance().getUser().getValue().id, curveId, HomePan.getInstance().guid, tvCurveName.getText().toString(),
                needTime, curveSteps, curveStageParams, BaseResponse.class, new RetrofitCallback<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        ToastUtils.showShort(CurveSaveActivity.this, R.string.pan_save_success);
                    }

                    @Override
                    public void onFaild(String err) {

                    }
                });
    }

    //曲线命名
    private void curveEidt() {
        if (null == editDialog) {
            editDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_CURVE_EDIT);
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
                        ToastUtils.showShort(CurveSaveActivity.this, R.string.pan_input_empty);
                        return;
                    }
                    tvCurveName.setText(editText.getText());
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
            needTime = getIntent().getIntExtra(PanConstant.EXTRA_NEED_TIME, 0);
            curveId = getIntent().getLongExtra(PanConstant.EXTRA_CURVE_ID, -1);
            ArrayList<Entry> entryList = getIntent().getParcelableArrayListExtra(PanConstant.EXTRA_ENTRY_LIST);
            if (null != entryList) {
                List<LineChartDataBean> list = new ArrayList<>();
                for (int i=0; i<entryList.size(); i++)
                    list.add(new LineChartDataBean(entryList.get(i).getX(), entryList.get(i).getY()));
                curveStageParams = ChartDataUtils.listToJsonStr(list);
            }

            ArrayList<Entry> stepList = getIntent().getParcelableArrayListExtra(PanConstant.EXTRA_STEP_LIST);
            if (null != stepList) { //转化成标记步骤
                for (int i=0; i<stepList.size(); i++)
                    curveSteps.add(new CurveStep(i+1, stepList.get(i).getX() + "", stepList.get(i).getY()));
            }

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