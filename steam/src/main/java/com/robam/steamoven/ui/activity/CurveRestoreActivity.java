package com.robam.steamoven.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.constant.DialogConstant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.factory.SteamDialogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurveRestoreActivity extends SteamBaseActivity {
    //曲线详情
    private SteamCurveDetail steamCurveDetail;
    //
    private IDialog stopDialog, completeDialog;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private float lastMark = 0; //最后点
    private TextView tvFire, tvTemp, tvTime;
    private LineChart cookChart;
    private DynamicLineChartManager dm;
    private Map<String, String> params = null;
    private ArrayList<Entry> restoreList = new ArrayList<>();  //还原列表

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent())
            steamCurveDetail = (SteamCurveDetail) getIntent().getSerializableExtra(SteamConstant.EXTRA_CURVE_DETAIL);
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data)); //没有数据时显示的文字
    }

    @Override
    protected void initData() {
        if (null != steamCurveDetail) {
            Map<String, String> params = null;
            try {
                params = new Gson().fromJson(steamCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
                ArrayList<Entry> entryList = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String[] data = entry.getValue().split("-");
                    entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
                }
                dm = new DynamicLineChartManager(cookChart, this);
                dm.setLabelCount(5, 5);
                dm.setAxisLine(true, false);
                dm.setGridLine(false, true);
                dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_white_40), entryList, true, true);
                //添加第一个点
                restoreList.add(entryList.get(0));
                dm.initLineDataSet("", getResources().getColor(R.color.steam_chart), restoreList, true, false);
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                params = null;
            }
            if (null != params)
                startRestore(params);
        }
    }

    //开始还原
    private void startRestore(Map<String, String> params) {
        //监听设备变化
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime));
                //曲线绘制
                try {
                    if (params.containsKey(curTime + "")) {
                        String[] data = params.get(curTime + "").split("-");
                        restoreList.add(new Entry(curTime, Float.parseFloat(data[0])));//温度
                        cookChart.invalidate();
                        tvFire.setText("火力：" + data[1] + "档");
                        tvTemp.setText("温度：" + data[0] + "℃");
                    }
                } catch (Exception e) {}

                if (curTime >= lastMark) {
                    //工作结束
                    //提示烹饪完成
                    workComplete();
                    return;
                }
            }
        });

        Iterator<String> iterator = params.keySet().iterator();
        String last = "0";
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        lastMark = Float.parseFloat(last);  //最后点时间
        runnable = new Runnable() {

            @Override

            public void run() {
                //查询设备状态
                SteamAbstractControl.getInstance().queryAttribute(HomeSteamOven.getInstance().guid);
                mHandler.postDelayed(runnable, 2000L);
            }

        };
        //第一个点
        try {
            tvTime.setText("1");
            if (params.containsKey("0")) {
                String[] data = params.get("0").split("-");
                tvFire.setText("火力：" + data[1] + "档");
                tvTemp.setText("温度：" + data[0] + "℃");
            }
        } catch (Exception e) {}
        //开始工作
        SteamAbstractControl.getInstance().startWork();
        //每隔两s查询
        mHandler.postDelayed(runnable, 2000);
    }

    //还原结束提示
    private void workComplete() {
        if (null == completeDialog) {
            completeDialog = SteamDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STEAM_COMMON);
            completeDialog.setContentText(R.string.steam_work_complete);
            completeDialog.setCancelable(false);
            completeDialog.setContentText(R.string.steam_work_complete_add_time);
            completeDialog.setOKText(R.string.steam_work_complete_complete);
            completeDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    //回首页
                    startActivity(MainActivity.class);
                }
            }, R.id.tv_ok);
        }
        completeDialog.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            stopCook();
        }
    }
    //结束烹饪提示
    private void stopCook() {
        if (null == stopDialog) {
            stopDialog = SteamDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STEAM_COMMON);
            stopDialog.setContentText(R.string.steam_stop_cook_hint);
            stopDialog.setOKText(R.string.steam_work_end);
            stopDialog.setCancelable(false);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok)
                        finish();
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stopDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
        if (null != completeDialog && completeDialog.isShow())
            completeDialog.dismiss();
    }
}