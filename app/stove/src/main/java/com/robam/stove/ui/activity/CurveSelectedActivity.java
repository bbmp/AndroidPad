package com.robam.stove.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.CurveStep;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetCurveCookbooksRes;
import com.robam.stove.response.GetCurveDetailRes;
import com.robam.stove.ui.adapter.RvStep3Adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线选中，烹饪曲线其他进入
public class CurveSelectedActivity extends StoveBaseActivity {
    private long curveid;
    //曲线步骤
    private RvStep3Adapter rvStep3Adapter;
    private RecyclerView rvStep;
    //曲线名字
    private TextView tvCurveName;
    //开始烹饪
    private TextView tvStartCook;
    //曲线详情
    private StoveCurveDetail stoveCurveDetail;
    //火力
    private TextView tvFire;
    //温度
    private TextView tvTemp;
    //时间
    private TextView tvTime;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent())
            curveid = getIntent().getLongExtra(StoveConstant.EXTRA_CURVE_ID, -1);
        rvStep = findViewById(R.id.rv_step);
        tvCurveName = findViewById(R.id.tv_recipe_name);
        tvStartCook = findViewById(R.id.tv_start_cook);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.stove_no_curve_data)); //没有数据时显示的文字
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_15)));
        rvStep3Adapter = new RvStep3Adapter();
        rvStep.setAdapter(rvStep3Adapter);

        setOnClickListener(R.id.tv_start_cook);
    }

    @Override
    protected void initData() {
        getCurveDetail();

    }
    //曲线详情
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, curveid, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.payload) {
                    stoveCurveDetail = getCurveDetailRes.payload;
                    tvCurveName.setText(stoveCurveDetail.name);

                    List<CurveStep> curveSteps = new ArrayList<>();
                    if (null != stoveCurveDetail.stepList) {
                        curveSteps.addAll(stoveCurveDetail.stepList);
                        tvStartCook.setVisibility(View.VISIBLE);
                    }
                    rvStep3Adapter.setList(curveSteps);
                    //绘制曲线
                    drawCurve(stoveCurveDetail);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_start_cook) {
            //曲线还原
            Intent intent = new Intent();
            if (null != stoveCurveDetail)
                intent.putExtra(StoveConstant.EXTRA_CURVE_DETAIL, stoveCurveDetail);
            intent.setClass(this, CurveRestoreActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ll_left) { //返回
            finish();
        }

    }

    //曲线绘制
    private void drawCurve(StoveCurveDetail stoveCurveDetail) {
        Map<String, String> params = null;
        try {
            String[] data = new String[3];
            params = new Gson().fromJson(stoveCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            ArrayList<Entry> entryList = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                data = entry.getValue().split("-");
                entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
            }

            dm = new DynamicLineChartManager(cookChart, this);
            dm.setLabelCount(5, 5);
            dm.setAxisLine(true, false);
            dm.setGridLine(false, false);
            dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.stove_chart), entryList, true, false);
            cookChart.notifyDataSetChanged();
            //绘制步骤标记
            List<CurveStep> stepList = stoveCurveDetail.stepList;
            if (null != stepList) {
                MarkViewStep mv = new MarkViewStep(this, cookChart.getXAxis().getValueFormatter());
                mv.setChartView(cookChart);
                cookChart.setMarker(mv);
                List<Highlight> highlights = new ArrayList<>();
                int dataIndex = 1;
                for (CurveStep step : stepList) {
                    highlights.add(new Highlight(Float.parseFloat(step.markTime), step.markTemp, 0, dataIndex));
                    dataIndex++;
                }
                cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
            }
            //最后一点
            tvFire.setText("火力：" + data[1] + "档");
            tvTemp.setText("温度：" + data[0] + "℃");
            tvTime.setText("时间：" + TimeUtils.secToMinSecond(stoveCurveDetail.needTime));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            params = null;
        }
    }
}