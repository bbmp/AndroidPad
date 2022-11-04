package com.robam.stove.ui.activity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurveRestoreActivity extends StoveBaseActivity {
    //曲线详情
    private StoveCurveDetail stoveCurveDetail;
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

    private Stove stove;
    private Pan pan;
    private int stoveId;

    private IPublicPanApi iPublicPanApi = ModulePubliclHelper.getModulePublic(IPublicPanApi.class, IPublicPanApi.PAN_PUBLIC);

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent()) {
            stoveId = getIntent().getIntExtra(StoveConstant.EXTRA_STOVE_ID, IPublicStoveApi.STOVE_LEFT);
            stoveCurveDetail = (StoveCurveDetail) getIntent().getSerializableExtra(StoveConstant.EXTRA_CURVE_DETAIL);
        }
        if (null == stoveCurveDetail)  //曲线详情为空
            finish();
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(getResources().getString(R.string.stove_no_curve_data)); //没有数据时显示的文字
        setOnClickListener(R.id.ll_left);
        //监听开火状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s) && device.guid.equals(HomeStove.getInstance().guid) && device instanceof Stove && curTime > 0) { //当前灶且还原开始
                        Stove stove = (Stove) device;
                        //开火提示状态
                        if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_CLOSE) { //左灶已关火
                            //还原结束
                            workComplete(false);
                        } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_CLOSE) { //右灶已关火
                            //还原结束
                            workComplete(false);
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

    @Override
    protected void initData() {
        //查找锅和灶
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan)
                pan = (Pan) device;
            else if (device instanceof Stove)
                stove = (Stove) device;
        }

        if (null != stoveCurveDetail) {
            Map<String, String> params = null;
            try {
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
                    while (Integer.parseInt(entry.getKey()) >= i) { //补点
                        String[] data = entry.getValue().split("-");
                        entryList.add(new Entry(i, Float.parseFloat(data[0]))); //时间和温度
                        i += 2;
                    }

                }
                while (i <= stoveCurveDetail.needTime && null != entry) { //最后少的点
                    String[] data = entry.getValue().split("-");
                    entryList.add(new Entry(i, Float.parseFloat(data[0]))); //时间和温度
                    i += 2;
                }
                dm = new DynamicLineChartManager(cookChart, this);
                dm.setLabelCount(5, 5);
                dm.setAxisLine(true, false);
                dm.setGridLine(false, true);
                dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.stove_white_40), entryList, true, true);
                //添加第一个点
                restoreList.add(new Entry(curTime, pan.panTemp));
                dm.initLineDataSet("", getResources().getColor(R.color.stove_chart), restoreList, true, false);
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                params = null;
            }
            if (null != params) {
               setInteraction();

               startRestore(params);
            }
        }
    }
    //设置锅互动参数
    private void setInteraction() {
        if (null != iPublicPanApi) {
            Map params = new LinkedHashMap();
            params.put(PanConstant.KEY5, new byte[] {(byte) stoveId}); //更换炉头id
            params.put(PanConstant.KEY4, new byte[]{(byte) stoveId, 0, 0, 0, 0, (byte) PanConstant.start}); //曲线还原启动
            iPublicPanApi.setInteractionParams(pan.guid, params);
        }
    }
    //开始还原
    private void startRestore(Map<String, String> params) {

//        Iterator<String> iterator = params.keySet().iterator();
//        String last = "0";
//        while (iterator.hasNext()) {
//            last = iterator.next();
//        }
        lastMark = stoveCurveDetail.needTime;  //最后点时间
        runnable = new Runnable() {

            @Override

            public void run() {
                if (pan.msgId == MsgKeys.POT_INTERACTION_Req) {
                    setInteraction();  //设置互动参数
                    mHandler.postDelayed(runnable, 1000L);
                    return;
                }
                if (pan.mode != 3 && null != iPublicPanApi) { //锅不是曲线还原模式

                    if (curTime > 0) //已经开始,中途停止
                        curTime++;

                    iPublicPanApi.queryAttribute(pan.guid); //查询锅状态
                    mHandler.postDelayed(runnable, 1000L);
                    return;
                }

                if (curTime >= lastMark) {
                    //工作结束
                    //提示烹饪完成
                    workComplete(true);
                    return;
                }
                if ((curTime % 2) == 0) {
                    if (null != iPublicPanApi)
                        iPublicPanApi.queryAttribute(pan.guid); //查询锅状态

                    StoveAbstractControl.getInstance().queryAttribute(stove.guid); //查询灶状态
                }
                //曲线绘制
                try {
                    curTime++;
                    tvTime.setText(DateUtil.secForMatTime3(curTime) + "min");
//                    if (params.containsKey(curTime + "")) {
//                        String[] data = params.get(curTime + "").split("-");
                        restoreList.add(new Entry(curTime, pan.panTemp));//温度
                        cookChart.invalidate();
                        if (null != stove) {
                            if (stoveId == IPublicStoveApi.STOVE_LEFT) //左灶
                                tvFire.setText("火力：" + stove.leftLevel + "档");
                            else
                                tvFire.setText("火力：" + stove.rightLevel + "档");
                        }
                        if (null != pan)
                            tvTemp.setText("温度：" + pan.panTemp + "℃");
//                    }
                } catch (Exception e) {}


                mHandler.postDelayed(runnable, 1000L);

            }

        };
        //第一个点
        try {
            tvTime.setText("1min");
//            if (params.containsKey("0")) {
//                String[] data = params.get("0").split("-");
                if (null != stove) {
                    if (stoveId == IPublicStoveApi.STOVE_LEFT) //左灶
                        tvFire.setText("火力：" + stove.leftLevel + "档");
                    else
                        tvFire.setText("火力：" + stove.rightLevel + "档");
                }
                if (null != pan)
                    tvTemp.setText("温度：" + pan.panTemp + "℃");
//            }
        } catch (Exception e) {}
        mHandler.postDelayed(runnable, 1000);
    }
    //还原结束提示
    private void workComplete(boolean closeFire) {
        curTime = 0; //工作结束
        closeFire(closeFire);

        if (null == completeDialog) {
            completeDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMPLETE);
            completeDialog.setCancelable(false);
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

    private void closeFire(boolean closeFire) {
        //关火
        if (closeFire)
            StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, (byte) stoveId, (byte) 0x00, (byte) StoveConstant.STOVE_CLOSE);

        //停止记录
        if (null != iPublicPanApi) {
            Map params = new HashMap();
            params.put(PanConstant.KEY4, new byte[]{(byte) stoveId, 0, 0, 0, 0, (byte) PanConstant.stop});
            params.put(PanConstant.KEY6, new byte[]{(byte) PanConstant.MODE_CLOSE_FRY}); //停止搅拌
            iPublicPanApi.setInteractionParams(pan.guid, params);
        }
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
            stopDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            stopDialog.setCancelable(false);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        //关火
                        closeFire(true);

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

        if (null != stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
        if (null != completeDialog && completeDialog.isShow())
            completeDialog.dismiss();
    }
}