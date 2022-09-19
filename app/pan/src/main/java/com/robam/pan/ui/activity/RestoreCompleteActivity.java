package com.robam.pan.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.bean.PanRecipeDetail;
import com.robam.pan.constant.PanConstant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线还原结束界面
public class RestoreCompleteActivity extends PanBaseActivity {
    private TextView tvRecipeName;
    //火力
    private TextView tvFire;
    //温度
    private TextView tvTemp;
    //时间
    private TextView tvTime;
    //曲线详情
    private PanCurveDetail panCurveDetail;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_restore_complete;
    }

    @Override
    protected void initView() {
        showCenter();

        if (null != getIntent())
            panCurveDetail = (PanCurveDetail) getIntent().getSerializableExtra(PanConstant.EXTRA_CURVE_DETAIL);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        cookChart = findViewById(R.id.cook_chart);
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);

        setOnClickListener(R.id.btn_back_home);
    }

    @Override
    protected void initData() {
        if (null != panCurveDetail) {
            tvRecipeName.setText(panCurveDetail.name);
            drawCurve(panCurveDetail);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_back_home) {
            //返回锅首页
            startActivity(MainActivity.class);
        }
    }
    //曲线绘制
    private void drawCurve(PanCurveDetail panCurveDetail) {
        Map<String, String> params = null;
        try {
            String[] data = new String[3];
            params = new Gson().fromJson(panCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            ArrayList<Entry> entryList = new ArrayList<>();
            ArrayList<Entry> appointList = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                data = entry.getValue().split("-");
                entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
            }
            List<CurveStep> stepList = panCurveDetail.stepList;
            if (null != stepList) {
                for (CurveStep curveStep: stepList) {
                    appointList.add(new Entry(Float.parseFloat(curveStep.markTime), curveStep.markTemp));
                }
            }
            dm = new DynamicLineChartManager(cookChart, this);
            dm.setLabelCount(5, 5);
            dm.setAxisLine(true, false);
            dm.setGridLine(false, false);
            dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_chart), entryList, true, false);
            cookChart.notifyDataSetChanged();
            //最后一点
            tvFire.setText("火力：" + data[1] + "档");
            tvTemp.setText("温度：" + data[0] + "℃");
            tvTime.setText("时间：" + TimeUtils.secToMinSecond(panCurveDetail.needTime));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            params = null;
        }
    }
}