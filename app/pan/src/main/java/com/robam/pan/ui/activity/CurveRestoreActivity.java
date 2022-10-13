package com.robam.pan.ui.activity;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.utils.LogUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.common.constant.PanConstant;
import com.robam.pan.device.HomePan;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvStep2Adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//曲线还原,
public class CurveRestoreActivity extends PanBaseActivity {
    private RecyclerView rvStep;
    private TextView tvStop;
    //步骤
    RvStep2Adapter rvStep2Adapter;
    //当前步骤
    private TextView tvStep;
    //火力
    private TextView tvFire;
    //温度
    private TextView tvTemp;

    //曲线详情
    PanCurveDetail panCurveDetail;

    private IDialog stopDialog;

    private Handler mHandler = new Handler();
    private Runnable runnable;
    private LinearLayoutManager linearLayoutManager;

    int curStep = 0;
    int curTime = 0; //当前时间
    private LineChart cookChart;
    private DynamicLineChartManager dm;
    private Map<String, String> params = null;

    private Stove stove;
    private Pan pan;

    ArrayList<Entry> restoreList = new ArrayList<>();  //还原列表

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        if (null != getIntent())
            panCurveDetail = (PanCurveDetail) getIntent().getSerializableExtra(PanConstant.EXTRA_CURVE_DETAIL);
        if (null == panCurveDetail) //曲线详情为空
            finish();
        rvStep = findViewById(R.id.rv_step);
        tvStop = findViewById(R.id.tv_stop_cook);
        tvStep = findViewById(R.id.tv_cur_step);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.pan_no_curve_data)); //没有数据时显示的文字
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        linearLayoutManager = new LinearLayoutManager(this);
        rvStep.setLayoutManager(linearLayoutManager);
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_30)));
        rvStep2Adapter = new RvStep2Adapter();
        rvStep.setAdapter(rvStep2Adapter);
        //关闭动画,防止闪烁
        ((SimpleItemAnimator)rvStep.getItemAnimator()).setSupportsChangeAnimations(false);
        setOnClickListener(R.id.ll_left, R.id.tv_stop_cook);

    }

    @Override
    protected void initData() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan)
                pan = (Pan) device;
            else if (device instanceof Stove)
                stove = (Stove) device;
        }
        if (null != panCurveDetail) {

            try {
                //温度参数
                params = new Gson().fromJson(panCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
                ArrayList<Entry> entryList = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String[] data = entry.getValue().split("-");
                    entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
                }
                dm = new DynamicLineChartManager(cookChart, this);
                dm.setLabelCount(1, 1);
                dm.setAxisLine(true, false);
                dm.setGridLine(false, false);
                dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_white_40), entryList, true, true);
                //添加第一个点
                restoreList.add(entryList.get(0));
                dm.initLineDataSet("", getResources().getColor(R.color.pan_chart), restoreList, true, false);
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                params = null;
            }
            if (null != panCurveDetail.stepList) {
                ArrayList<CurveStep> curveSteps = new ArrayList<>();
                curveSteps.addAll(panCurveDetail.stepList);
                //处理时长
                int i = 0;
                for (i=0; i<curveSteps.size() - 1; i++) {
                    curveSteps.get(i).needTime = (int) (Float.parseFloat(curveSteps.get(i+1).markTime) - Float.parseFloat(curveSteps.get(i).markTime));
                }
                //最后一步
                curveSteps.get(i).needTime = (int) (panCurveDetail.needTime - Float.parseFloat(curveSteps.get(i).markTime));
                //步骤
                rvStep2Adapter.setList(curveSteps);
                Countdown();
            }
        }
    }
    //启动倒计时
    private void Countdown() {

        runnable = new Runnable() {

            @Override

            public void run() {
                //判断是否结束
                if (curStep >= rvStep2Adapter.getData().size()) {
                    //还原结束
                    //去烹饪结束
                    Intent intent = new Intent();
                    if (null != panCurveDetail) {
                        intent.putExtra(PanConstant.EXTRA_CURVE_DETAIL, panCurveDetail);
                        //关火
                        closeFire();
                    }
                    intent.setClass(CurveRestoreActivity.this, RestoreCompleteActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                CurveStep curveStep = rvStep2Adapter.getData().get(curStep);
                //曲线绘制
                try {
                    curTime++; //总时间
                    if (params.containsKey(curTime + "")) {
                        String[] data = params.get(curTime + "").split("-");
                        restoreList.add(new Entry(curTime, pan.panTemp));//温度
                        cookChart.invalidate();
                        if (null != stove) {
                            if (panCurveDetail.stoveId == IPublicStoveApi.STOVE_LEFT) //左灶
                                tvFire.setText("火力：" + stove.leftLevel + "档");
                            else
                                tvFire.setText("火力：" + stove.rightLevel + "档");
                        }
                        if (null != pan)
                            tvTemp.setText("温度：" + pan.panTemp + "℃");
                    }
                } catch (Exception e) {}

                if (curveStep.needTime > 0) {
                    curveStep.elapsedTime++;

                    if (curveStep.elapsedTime == curveStep.needTime) {
                        nextStep();
                    }
                    rvStep2Adapter.notifyItemChanged(curStep);

                }

                mHandler.postDelayed(runnable, 1000L);

            }

        };
        //第一个点
        try {
            if (params.containsKey("0")) {
                String[] data = params.get(curTime + "").split("-");
                if (null != stove) {
                    if (panCurveDetail.stoveId == IPublicStoveApi.STOVE_LEFT) //左灶
                        tvFire.setText("火力：" + stove.leftLevel + "档");
                    else
                        tvFire.setText("火力：" + stove.rightLevel + "档");
                }
                if (null != pan)
                    tvTemp.setText("温度：" + pan.panTemp + "℃");
            }
        } catch (Exception e) {}
        mHandler.postDelayed(runnable, 1000L);
    }

    //关火操作
    private void closeFire() {
        IPublicStoveApi iPublicStoveApi = ModulePubliclHelper.getModulePublic(IPublicStoveApi.class,
                IPublicStoveApi.STOVE_PUBLIC);
        //关火
        if (null != iPublicStoveApi) {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove) {
                    iPublicStoveApi.setAttribute(device.guid, (byte) panCurveDetail.stoveId, (byte) 0x00, (byte) StoveConstant.STOVE_CLOSE);
                    break;
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.tv_stop_cook) {
            //停止烹饪
            stopCook();
        }
    }

    //停止烹饪提示
    private void stopCook() {
        if (null == stopDialog) {
            stopDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
            stopDialog.setCancelable(false);
            stopDialog.setContentText(R.string.pan_stop_cook_hint);
            stopDialog.setOKText(R.string.pan_stop_cook);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    //停止烹饪
                    if (v.getId() == R.id.tv_ok) {
                        //关闭炉头
                        if (null != panCurveDetail) {
                            closeFire();
                        }
                        //回首页
                        startActivity(MainActivity.class);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stopDialog.show();
    }

    //切換步驟
    private void nextStep() {
        curStep++; //下一步
        rvStep2Adapter.setCurStep(curStep);
        //滑动和置顶
        linearLayoutManager.scrollToPositionWithOffset(curStep, 0);
        rvStep2Adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCountDown();
        if (null != stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
    }

    private void closeCountDown() {
        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}