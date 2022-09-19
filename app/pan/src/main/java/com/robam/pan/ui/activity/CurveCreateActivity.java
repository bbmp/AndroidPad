package com.robam.pan.ui.activity;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MarkViewAdd;
import com.robam.common.utils.DateUtil;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.factory.PanDialogFactory;

import java.util.ArrayList;

//曲线创作中
public class CurveCreateActivity extends PanBaseActivity {
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private TextView tvFire, tvTemp, tvTime;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;
    private ArrayList<Entry> entryList = new ArrayList<>();  //还原列表
    private ArrayList<Entry> stepList = new ArrayList<>(); //标记列表
    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        cookChart = findViewById(R.id.cook_chart);
        setOnClickListener(R.id.ll_left, R.id.iv_stop_create);
    }

    @Override
    protected void initData() {
        startCreate();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.iv_stop_create) {
            stopCook();
        }
    }

    //开始创建
    private void startCreate() {

        runnable = new Runnable() {

            @Override

            public void run() {

                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime));
                Entry entry = new Entry(curTime, (float) (Math.random()*20 + 130));
                dm.addEntry(entry, 0);
                tvTemp.setText("温度：" + (int) entry.getY() + "℃");
                cookChart.highlightValue(entry.getX(), entry.getY(), 0);
                mHandler.postDelayed(runnable, 2000L);

            }

        };
        //添加第一个点
        Entry entry = new Entry(0, (float) (Math.random()*20 + 130));
        entryList.add(entry);
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, true);
//        dm.setScaled(entryList);
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_chart), entryList, true, false);
        tvFire.setText("火力：" + "档");
        tvTemp.setText("温度：" + (int) entry.getY() + "℃");
        dm.setHilightColor();
        MarkViewAdd mv = new MarkViewAdd(this, cookChart.getXAxis().getValueFormatter());
        mv.setMarkerViewAddListener(new MarkViewAdd.MarkerViewAddListener() {
            @Override
            public void onMarkerViewAdd() {
                //添加标记
//                Entry entry = new Entry(curTime, (float) (Math.random()*20 + 130));
//
//                stepList.add(entry);
//                dm.addEntry(entry, 1);
            }
        });
        mv.setChartView(cookChart);
        cookChart.setMarker(mv);
        cookChart.highlightValue(entry.getX(), entry.getY(), 0);
        //第二条线
//        dm.initLineDataSet("", Color.TRANSPARENT, stepList, false);
        mHandler.post(runnable);
    }

    //创作结束提示
    private void stopCook() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_creation_hint);
        iDialog.setOKText(R.string.pan_stop_creation);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束创作
                if (v.getId() == R.id.tv_ok) {
                    //保存曲线
                    startActivity(CurveSaveActivity.class);
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}