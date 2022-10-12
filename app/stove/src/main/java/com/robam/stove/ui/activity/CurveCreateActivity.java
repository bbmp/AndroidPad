package com.robam.stove.ui.activity;

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
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MarkViewAdd;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//曲线创作
public class CurveCreateActivity extends StoveBaseActivity {
    private ImageView ivStop;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private TextView tvFire, tvTemp, tvTime;

    private IDialog stopDialog;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;
    private ArrayList<Entry> entryList = new ArrayList<>();  //创作列表
    private ArrayList<Entry> stepList = new ArrayList<>(); //标记列表
    private Stove stove;
    private Pan pan;
    private int stoveId;

    private IPublicPanApi iPublicPanApi = ModulePubliclHelper.getModulePublic(IPublicPanApi.class, IPublicPanApi.PAN_PUBLIC);
    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_create;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent())
            stoveId = getIntent().getIntExtra(StoveConstant.stoveId, IPublicStoveApi.STOVE_LEFT);
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.stove_no_curve_data)); //没有数据时显示的文字
        setOnClickListener(R.id.ll_left, R.id.iv_stop_create);
        //监听开火状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s) && device.guid.equals(HomeStove.getInstance().guid) && device instanceof Stove) { //当前灶
                        Stove stove = (Stove) device;
                        //开火提示状态
                        if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_CLOSE) { //左灶已关火

                        } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_CLOSE) { //右灶已关火

                        }

                        break;
                    }
                }
            }
        });
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

        if (null != iPublicPanApi) {
            //启动记录
            Map params = new HashMap();
            params.put(PanConstant.KEY2, new byte[] {(byte) stoveId, (byte) PanConstant.start});
            iPublicPanApi.setInteractionParams(pan.guid, params);
            startCreate();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        //停止创建曲线
        if (id == R.id.ll_left || id == R.id.iv_stop_create) {
            stopCook();
        }
    }

    //开始创建
    private void startCreate() {
        List<Highlight> highlights = new ArrayList<>();

        runnable = new Runnable() {

            @Override

            public void run() {

                curTime += 2;
                tvTime.setText(DateUtil.secForMatTime3(curTime));
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
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.stove_chart), entryList, true, false);
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
        dm.initLineDataSet("", getResources().getColor(R.color.stove_chart), stepList, false);
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
                        Entry entry = entryList.get(entryList.size() - 1); //最后一个点
                        highlights.add(0, new Highlight(entry.getX(), entry.getY(), 1, highlights.size())); //第二条线highlight,加前面

                        cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
                        dm.addEntry(entry, 1);

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
        mHandler.post(runnable);
    }

    //创作结束提示
    private void stopCook() {
        if (null == stopDialog) {
            stopDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            stopDialog.setCancelable(false);
            stopDialog.setContentText(R.string.stove_stop_creation_hint);
            stopDialog.setCancelText(R.string.stove_cancel);
            stopDialog.setOKText(R.string.stove_stop_cook);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    //结束创作
                    if (v.getId() == R.id.tv_ok) {
                        //关火
                        StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, (byte) stoveId, (byte) 0x00, (byte) StoveConstant.STOVE_CLOSE);
                        //停止记录
                        Map params = new HashMap();
                        params.put(PanConstant.KEY2, new byte[] {(byte) stoveId, (byte) PanConstant.stop});
                        iPublicPanApi.setInteractionParams(pan.guid, params);
                        //保存曲线
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(StoveConstant.EXTRA_ENTRY_LIST, entryList);
                        intent.putParcelableArrayListExtra(StoveConstant.EXTRA_STEP_LIST, stepList);
                        intent.setClass(CurveCreateActivity.this, CurveSaveActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stopDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);

        if (null == stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
    }
}
