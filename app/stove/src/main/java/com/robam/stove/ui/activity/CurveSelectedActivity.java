package com.robam.stove.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.bean.CurveStep;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetCurveDetailRes;
import com.robam.stove.ui.adapter.RvStep3Adapter;
import com.robam.stove.ui.dialog.SelectStoveDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线选中，烹饪曲线其他进入
public class CurveSelectedActivity extends StoveBaseActivity {
    //曲线id
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

    private IDialog openDialog;
    private SelectStoveDialog selectStoveDialog;
    private int stoveId;

    private Stove stove;
    private Pan pan;

    private IPublicPanApi iPublicPanApi = ModulePubliclHelper.getModulePublic(IPublicPanApi.class, IPublicPanApi.PAN_PUBLIC);

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent()) {
            curveid = getIntent().getLongExtra(StoveConstant.EXTRA_CURVE_ID, -1);
        }
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
        CloudHelper.getCurvebookDetail(this, curveid, "", GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
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

    //炉头选择
    private void selectStove() {
        //检查锅是否有连接
        if (isPanOffline())
            return;
        //检查灶是否连接
        if (isStoveOffline())
            return;
        //查找锅和灶
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan)
                pan = (Pan) device;
            else if (device instanceof Stove)
                stove = (Stove) device;
        }
        if (null == pan || null == stove)
            return;
        //炉头选择提示
        if (null == selectStoveDialog) {
            selectStoveDialog = new SelectStoveDialog(this);
            selectStoveDialog.setCancelable(false);

            selectStoveDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.view_left) {

                        openFire(IPublicStoveApi.STOVE_LEFT); //左灶
                    } else if (id == R.id.view_right) {

                        openFire(IPublicStoveApi.STOVE_RIGHT); //右灶
                    }
                }
            }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        }
        //检查炉头状态
        selectStoveDialog.checkStoveStatus();
        selectStoveDialog.show();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == PanConstant.MSG_CURVE_RESTORE) {//曲线还原
                if (pan.msgId == MsgKeys.POT_CURVETEMP_Req) {  //设置灶参数
                    iPublicPanApi.setCurveStoveParams(pan.guid, 0, stoveId, stoveCurveDetail.curveStageParams, stoveCurveDetail.temperatureCurveParams); //设置灶参数

                    handler.sendEmptyMessageDelayed(PanConstant.MSG_CURVE_RESTORE, 1000);
                } else if (pan.msgId == MsgKeys.POT_CURVEElectric_Req) {

                    iPublicPanApi.setCurvePanParams(pan.guid, 0, stoveCurveDetail.smartPanModeCurveParams); //设置锅参数

                    handler.sendEmptyMessageDelayed(PanConstant.MSG_CURVE_RESTORE, 1000);
                } else if (pan.msgId == MsgKeys.POT_INTERACTION_Req) { //设置锅互动参数

                    //开火提示状态
                    if (null != openDialog && openDialog.isShow()) {
                        if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_WORKING) { //左灶已点火
                            openDialog.dismiss();
                            startRestore();

                            return;
                        } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_WORKING) { //右灶已点火
                            openDialog.dismiss();
                            startRestore();

                            return;
                        }
                    }

                    handler.sendEmptyMessageDelayed(PanConstant.MSG_CURVE_RESTORE, 1000);
                }
            }
        }
    };

    //设置曲线还原参数
    private void setParams() {
        if (null != stoveCurveDetail && null != iPublicPanApi) {
            pan.msgId = MsgKeys.POT_CURVEElectric_Req; //设置锅参数

            iPublicPanApi.setCurvePanParams(pan.guid, 0, stoveCurveDetail.smartPanModeCurveParams); //设置锅参数

            handler.sendEmptyMessageDelayed(PanConstant.MSG_CURVE_RESTORE, 1000);
        }
    }

    //点火提示
    private void openFire(int stove) {
        setParams();  //设置锅参数

        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && device.guid.equals(HomeStove.getInstance().guid)) {
                if (null == openDialog) {
                    openDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
                    openDialog.setCancelable(false);
                    openDialog.setListeners(new IDialog.DialogOnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handler.removeCallbacksAndMessages(null);
                        }
                    }, R.id.full_dialog);
                }

                if (stove == IPublicStoveApi.STOVE_LEFT) {
                    openDialog.setContentText(R.string.stove_open_left_hint);
                    //进入工作状态
                    //选择左灶
                    stoveId = IPublicStoveApi.STOVE_LEFT;

                } else {
                    openDialog.setContentText(R.string.stove_open_right_hint);
                    //选择右灶
                    stoveId = IPublicStoveApi.STOVE_RIGHT;
                }
                openDialog.show();
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_start_cook) {
            //选择炉头
            if (null == stoveCurveDetail || null == stoveCurveDetail.temperatureCurveParams || null == stoveCurveDetail.curveStageParams) {
                ToastUtils.showShort(this, R.string.stove_no_curve_data);
                return;
            }
            selectStove();
        } else if (id == R.id.ll_left) { //返回
            finish();
        }

    }
    //跳转曲线还原
    private void startRestore() {
        //曲线还原
        Intent intent = new Intent();
        if (null != stoveCurveDetail) {
            intent.putExtra(StoveConstant.EXTRA_STOVE_ID, stoveId); //选中哪个炉头
            intent.putExtra(StoveConstant.EXTRA_CURVE_DETAIL, stoveCurveDetail);
        }
        intent.setClass(this, CurveRestoreActivity.class);
        startActivity(intent);
        finish();
    }

    //曲线绘制
    private void drawCurve(StoveCurveDetail stoveCurveDetail) {
        Map<String, String> params = null;
        try {
            String[] data = new String[3];
            params = new Gson().fromJson(stoveCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());

            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) { //删除超出时间的点
                if (Integer.parseInt(iterator.next().getKey()) > stoveCurveDetail.needTime)
                    iterator.remove();
            }

            iterator = params.entrySet().iterator();
            int i = 0;
            ArrayList<Entry> entryList = new ArrayList<>();
            Map.Entry<String, String> entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next();
                while (Integer.parseInt(entry.getKey()) >= i) {  //补点
                    data = entry.getValue().split("-");
                    entryList.add(new Entry(i, Float.parseFloat(data[0]))); //时间和温度
                    i += 2;
                }

            }
            while (i <= stoveCurveDetail.needTime && null != entry) { //最后少的点
                data = entry.getValue().split("-");
                entryList.add(new Entry(i, Float.parseFloat(data[0]))); //时间和温度
                i += 2;
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