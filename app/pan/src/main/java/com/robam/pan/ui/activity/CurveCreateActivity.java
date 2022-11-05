package com.robam.pan.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.Device;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MarkViewAdd;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.common.constant.PanConstant;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.CreateCurveStartRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线创作中
public class CurveCreateActivity extends PanBaseActivity {
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private TextView tvFire, tvTemp, tvTime;

    private TextView tvTimeUnit;
    private ImageView ivStop;

    private IDialog stopDialog;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;
    private ArrayList<Entry> entryList = new ArrayList<>();  //创作列表
    private ArrayList<Entry> stepList = new ArrayList<>(); //标记列表
    private List<Highlight> highlights = new ArrayList<>();
    private Stove stove;
    private Pan pan;
    private int stoveId;
    private long curveId;
    //曲线详情
    private PanCurveDetail panCurveDetail;


    private IPublicStoveApi iPublicStoveApi = ModulePubliclHelper.getModulePublic(IPublicStoveApi.class,
            IPublicStoveApi.STOVE_PUBLIC);

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        if (null != getIntent()) {
            stoveId = getIntent().getIntExtra(StoveConstant.EXTRA_STOVE_ID, IPublicStoveApi.STOVE_LEFT);
            panCurveDetail = (PanCurveDetail) getIntent().getSerializableExtra(PanConstant.EXTRA_CURVE_DETAIL);
        }
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        tvTimeUnit = findViewById(R.id.tv_time_unit);
        ivStop = findViewById(R.id.iv_stop_create);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.pan_no_curve_data)); //没有数据时显示的文字
        setOnClickListener(R.id.ll_left, R.id.iv_stop_create);
    }

    @Override
    protected void initData() {

        //查找锅和灶
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan)
                pan = (Pan) device;
            else if (device instanceof Stove)
                stove = (Stove) device;
        }
        if (null == pan || null == stove) //锅或灶不存在
            finish();
        //创建曲线
        if (null == panCurveDetail)
            createCurveStart();
        else
            continueCreateCurve();
        //监听开火状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s) && device instanceof Stove && curTime > 0) { //当前灶且创建已开始
                        Stove stove = (Stove) device;
                        //开火提示状态
                        if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_CLOSE) { //左灶已关火
                            //跳转保存
                            saveCurve(false);
                        } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_CLOSE) { //右灶已关火
                            //跳转保存
                            saveCurve(false);
                        } else if (stove.status == Device.OFFLINE) {

                        }

                        break;
                    } else if (device.guid.equals(s) && device instanceof Pan && curTime > 0) { //检查锅状态锅
                        Pan pan = (Pan) device;
                        if (pan.status == Device.OFFLINE) { //锅已离线

                        }
                    }
                }
            }
        });
    }
    //继续创建曲线
    private void continueCreateCurve() {
        tvTimeUnit.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.VISIBLE);
        curveId = panCurveDetail.curveCookbookId;

        runnable = new Runnable() {

            @Override

            public void run() {

                if (pan.mode != 1) {
                    if (curTime > 0) { //锅停止工作
                        //跳转保存
                        saveCurve(true);
                        return;
                    }
                    PanAbstractControl.getInstance().queryAttribute(pan.guid); //查询锅状态
                    mHandler.postDelayed(runnable, 1000L);
                    return;
                }
                PanAbstractControl.getInstance().queryAttribute(pan.guid); //查询锅状态
                if (null != iPublicStoveApi)
                    iPublicStoveApi.queryAttribute(stove.guid); //查询灶状态

                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime) + "min");
                Entry entry = new Entry(curTime, (float) pan.panTemp);
                dm.addEntry(entry, 0);
                if (stoveId == IPublicStoveApi.STOVE_RIGHT)
                    tvFire.setText("火力：" + stove.rightLevel + "档");
                else
                    tvFire.setText("火力：" + stove.leftLevel + "档");
                tvTemp.setText("温度：" + (int) entry.getY() + "℃");
//                cookChart.highlightValue(entry.getX(), entry.getY(), 0);
                highlights.set(highlights.size() - 1, new Highlight(entry.getX(), entry.getY(), 0)); //标记highlight只能加最后，maskview中坐标会覆盖
                cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
                mHandler.postDelayed(runnable, 2000L);

            }

        };

        drawCurve();
    }
    //曲线绘制
    private void drawCurve() {
        Map<String, String> params = null;
        try {
            String[] data = new String[3];
            params = new Gson().fromJson(panCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());

            for (Map.Entry<String, String> entry : params.entrySet()) {
                data = entry.getValue().split("-");
                entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
                curTime = (int) Float.parseFloat(entry.getKey());
            }

            dm = new DynamicLineChartManager(cookChart, this);
            dm.setLabelCount(5, 5);
            dm.setAxisLine(true, false);
            dm.setGridLine(false, true);
            dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_chart), entryList, true, false);
            cookChart.notifyDataSetChanged();
            if (panCurveDetail.headId == IPublicStoveApi.STOVE_RIGHT)
                tvFire.setText("火力：" + stove.rightLevel + "档");
            else
                tvFire.setText("火力：" + stove.leftLevel + "档");
            tvTemp.setText("温度：" + pan.panTemp + "℃");

            MarkViewAdd mv = new MarkViewAdd(this, cookChart.getXAxis().getValueFormatter());
            mv.setChartView(cookChart);
            cookChart.setMarker(mv);

            Entry lastEntry = entryList.get(entryList.size() - 1); //最后一个点
            highlights.add(new Highlight(lastEntry.getX(), lastEntry.getY(), 0));  //标记步骤highlight
            cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
            //第二条线
            dm.initLineDataSet("", getResources().getColor(R.color.pan_chart), stepList, false);
            cookChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                }

                @Override
                public void onChartGestureMove(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {
                    if (null != mv) {
                        Rect rect = new Rect((int) mv.drawingPosX, (int) mv.drawingPosY, (int) mv.drawingPosX + mv.getWidth(), (int) mv.drawingPosY + mv.getHeight());
                        if (cookChart.isDrawMarkersEnabled() && cookChart.valuesToHighlight() && rect.contains((int) me.getX(), (int) me.getY())) {
                            LogUtils.e("click" + Thread.currentThread().getName());
                            addStep();

                        }
                    }
                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });
            //绘制步骤标记
            if (null != panCurveDetail.stepList) {

                for (CurveStep step : panCurveDetail.stepList) {
                    Entry entry = new Entry(Float.parseFloat(step.markTime), step.markTemp);
                    stepList.add(entry);
                    highlights.add(0, new Highlight(entry.getX(), entry.getY(), 1, highlights.size())); //第二条线highlight,加前面

                    cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
                    dm.addEntry(entry, 1);
                }
            }
            mHandler.post(runnable);

        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            params = null;
        }
    }

    //153设置互动参数
    private void setInteraction() {
        //启动记录
        pan.msgId = MsgKeys.POT_INTERACTION_Req;
        Map params = new HashMap();
        params.put(PanConstant.KEY2, new byte[] {(byte) stoveId, (byte) PanConstant.start});
        params.put(PanConstant.KEY5, new byte[] {(byte) stoveId}); //更换炉头id
        PanAbstractControl.getInstance().setInteractionParams(pan.guid, params);
    }

    private void createCurveStart() {
        //创建曲线记录开始请求
        CloudHelper.createCurveStart(this, AccountInfo.getInstance().getUser().getValue().id, pan.guid, stoveId, CreateCurveStartRes.class, new RetrofitCallback<CreateCurveStartRes>() {
            @Override
            public void onSuccess(CreateCurveStartRes createCurveStartRes) {
                if (null != createCurveStartRes && createCurveStartRes.rc == 0) {
                    tvTimeUnit.setVisibility(View.VISIBLE);
                    ivStop.setVisibility(View.VISIBLE);
                    curveId = createCurveStartRes.payload;
                    //设置互动参数
                    setInteraction();

                    startCreate();
                }
            }

            @Override
            public void onFaild(String err) {
                ToastUtils.showShort(CurveCreateActivity.this, R.string.pan_connect_failed);
                finish();
            }
        });
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
                if (pan.msgId == MsgKeys.POT_INTERACTION_Req) {
                    setInteraction();  //设置互动参数
                    mHandler.postDelayed(runnable, 1000L);
                    return;
                }
                if (pan.mode != 1) {
                    if (curTime > 0) { //锅停止工作
                        //跳转保存
                        saveCurve(true);
                        return;
                    }
                    PanAbstractControl.getInstance().queryAttribute(pan.guid); //查询锅状态
                    mHandler.postDelayed(runnable, 1000L);
                    return;
                }
                PanAbstractControl.getInstance().queryAttribute(pan.guid); //查询锅状态
                if (null != iPublicStoveApi)
                    iPublicStoveApi.queryAttribute(stove.guid); //查询灶状态

                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime) + "min");
                Entry entry = new Entry(curTime, (float) pan.panTemp);
                dm.addEntry(entry, 0);
                if (stoveId == IPublicStoveApi.STOVE_RIGHT)
                    tvFire.setText("火力：" + stove.rightLevel + "档");
                else
                    tvFire.setText("火力：" + stove.leftLevel + "档");
                tvTemp.setText("温度：" + (int) entry.getY() + "℃");
//                cookChart.highlightValue(entry.getX(), entry.getY(), 0);
                highlights.set(highlights.size() - 1, new Highlight(entry.getX(), entry.getY(), 0)); //标记highlight只能加最后，maskview中坐标会覆盖
                cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
                mHandler.postDelayed(runnable, 2000L);

            }

        };
        //添加第一个点
        Entry entry = new Entry(0, (float) pan.panTemp);
        entryList.add(entry);
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, true);
//        dm.setScaled(entryList);
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.pan_chart), entryList, true, false);
        if (stoveId == IPublicStoveApi.STOVE_RIGHT)
            tvFire.setText("火力：" + stove.rightLevel + "档");
        else
            tvFire.setText("火力：" + stove.leftLevel + "档");
        tvTemp.setText("温度：" + (int) entry.getY() + "℃");
        MarkViewAdd mv = new MarkViewAdd(this, cookChart.getXAxis().getValueFormatter());
        mv.setChartView(cookChart);
        cookChart.setMarker(mv);

        highlights.add(new Highlight(entry.getX(), entry.getY(), 0));  //标记步骤highlight
        cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
        //第二条线
        dm.initLineDataSet("", getResources().getColor(R.color.pan_chart), stepList, false);
        cookChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureMove(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                if (null != mv) {
                    Rect rect = new Rect((int) mv.drawingPosX, (int) mv.drawingPosY, (int) mv.drawingPosX + mv.getWidth(), (int) mv.drawingPosY + mv.getHeight());
                    if (cookChart.isDrawMarkersEnabled() && cookChart.valuesToHighlight() && rect.contains((int) me.getX(), (int) me.getY())) {
                        LogUtils.e("click" + Thread.currentThread().getName());
                        addStep();

                    }
                }
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
        mHandler.postDelayed(runnable, 1000);
        //添加启动标记
        addStep();
    }

    private void addStep() {
        Entry entry = entryList.get(entryList.size() - 1); //最后一个点
        stepList.add(entry);
        highlights.add(0, new Highlight(entry.getX(), entry.getY(), 1, highlights.size())); //第二条线highlight,加前面

        cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
        dm.addEntry(entry, 1);
        //转化成步骤
        List<CurveStep> curveSteps = new ArrayList<>();
        curveSteps.add(new CurveStep(stepList.size(), entry.getX() + "", entry.getY()));

        //更新标记步骤
        CloudHelper.cookingCurveMarkStep(this, curveId, curveSteps, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {

            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    //创作结束提示
    private void stopCook() {
        if (null == stopDialog) {
            stopDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
            stopDialog.setCancelable(false);
            stopDialog.setContentText(R.string.pan_stop_creation_hint);
            stopDialog.setOKText(R.string.pan_stop_creation);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    //结束创作
                    if (v.getId() == R.id.tv_ok) {
                        saveCurve(true);
//                        finish();
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stopDialog.show();
    }
    //保存曲线
    private void saveCurve(boolean closeFire) {
        //关火
        if (closeFire && null != iPublicStoveApi)
            iPublicStoveApi.setAttribute(stove.guid, (byte) stoveId, (byte) 0x00, (byte) StoveConstant.STOVE_CLOSE);
        //停止记录
        Map params = new HashMap();
        params.put(PanConstant.KEY2, new byte[] {(byte) stoveId, (byte) PanConstant.stop});
        params.put(PanConstant.KEY6, new byte[] {(byte) PanConstant.MODE_CLOSE_FRY}); //停止搅拌
        PanAbstractControl.getInstance().setInteractionParams(pan.guid, params);

        if (entryList.size() == 0) {
            finish();   //没有曲线数据
            return;
        }
        //结束步骤
        addStep();
        //保存曲线
        Intent intent = new Intent();
        intent.putExtra(PanConstant.EXTRA_CURVE_ID, curveId);
        intent.putExtra(PanConstant.EXTRA_NEED_TIME, curTime);
        intent.putParcelableArrayListExtra(PanConstant.EXTRA_ENTRY_LIST, entryList);
        intent.putParcelableArrayListExtra(PanConstant.EXTRA_STEP_LIST, stepList);
        intent.setClass(CurveCreateActivity.this, CurveSaveActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);

        if (null != stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
    }
}