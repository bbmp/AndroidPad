package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.utils.CurveDataUtil;
import com.robam.steamoven.utils.MultiSegmentUtil;
import com.robam.steamoven.utils.TextSpanUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//一体机多段
public class MultiWorkActivity extends SteamBaseActivity {

    public static final String TAG = "MultiWorkActivity";

    //段数父容器
    private ViewGroup optContentParentView;

    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  CUR_ITEM_VIEW_COUNT = 3;
    public static final int  DATA_KEY = R.id.multi_opt;

    private ImageView pauseCookView;
    private ImageView continueCookView;
    private TextView cookDurationView;
    private ViewGroup curCookInfoViewGroup;
    private LineChart cookChart;
    private DynamicLineChartManager dm;
    private int preTotalTime;
    private int directive_offset = 19000000;
    private static final int DIRECTIVE_OFFSET_END = 10;
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;
    private static final int DIRECTIVE_OFFSET_WORK_FINISH = 60;
    private static final int DIRECTIVE_OFFSET_GO_HOME = 80;
    private static final int WORK_COMPLETE_CODE = 301;
    private static final int ADD_TIME_MIN_TIME = 1000 * 6;//加时最大等待时长

    private static final int DEVICE_IDLE_DUR = 1000 * 60 * 10;//页面空闲最大时长
    private static final int DEVICE_IDLE_SHOW = (int) (1000 * 60 * 0.1f);//页面空闲最大时长
    private int sourceId = 0;//0 - 多段设置 ； 1 - 曲线
    private boolean isInitiativeEnd = false;//是否主动结束
    private long workTimeMS = System.currentTimeMillis();
    private boolean isPageHide = false;//页面是否已经不显示
    private int workMode;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_multi_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        //showRightCenter();
        optContentParentView = findViewById(R.id.multi_work_model_list);
        pauseCookView = findViewById(R.id.multi_work_pause);
        continueCookView = findViewById(R.id.multi_work_start);
        cookDurationView = findViewById(R.id.multi_work_total);
        curCookInfoViewGroup = findViewById(R.id.multi_work_cur_info);
        cookChart = findViewById(R.id.cook_chart);
        initOptViewTag();
        setOnClickListener(R.id.multi_work_pause,R.id.multi_work_start);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    if(toRemandPage(steamOven,getSegmentModeCode(steamOven))){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            updateViewInfo(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            //goHome();
                            if(multiSegments != null && multiSegments.get(0).recipeId != 0){
                                goHome();
                            }else{
                                toCurveSavePage();
                            }
                            break;
                    }

                }
            }
        });

//        MqttDirective.getInstance().getDirective().observe(this, s -> {
//            switch (s - directive_offset){
//                case DIRECTIVE_OFFSET_END:
//                case DIRECTIVE_OFFSET_GO_HOME:
//                    //goHome();
//                    break;
//                case DIRECTIVE_OFFSET_WORK_FINISH:
//                    toCurveSavePage();
//                    break;
//            }
//        });
    }

    /**
     * 更新页面展示 (?多段模式下 - 返回的是所有段落数据，还是当前工作段落数据)
     * @param steamOven
     */
    private void updateViewInfo(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
                toCurveSavePage();
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                goHome();
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                if(steamOven.mode != multiSegments.get(0).code){//工作模式已切换
                    goHome();
                    return;
                }
                dismissAllDialog();
                showDialog = false;
                boolean cookState = ((steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT) || (steamOven.workState ==  SteamStateConstant.WORK_STATE_WORKING));
                boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
                preTotalTime = getTotalTime(steamOven);
                setOptViewsState(steamOven,cookState,isPreHeat);
                //改变段落数据显示状态
                updateSegmentInfo(steamOven);
                break;
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
                //dealWorkFinish(steamOven);
                toAddTimePage(steamOven);
                break;
        }
    }



    private void updateSegmentInfo(SteamOven steamOven){
        int maxCount = steamOven.sectionNumber >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : steamOven.sectionNumber;
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,MultiSegmentUtil.getCurSegment(steamOven,i+1));
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setVisibility(View.INVISIBLE);
        }
        switch (steamOven.sectionNumber){
            case 0:
            case 1:
                workMode = steamOven.mode;
                break;
            case 2:
                workMode = steamOven.mode2;
                break;
            default:
                workMode = steamOven.mode3;
        }
    }








    /**
     * 获取剩余运行时间
     * @param steamOven
     * @return
     */
    private int getTotalTime(SteamOven steamOven){
        if(steamOven == null){
            return 0;
        }
        int totalTime = 0;
        int startIndex = steamOven.curSectionNbr;
        if(startIndex <= 0){
            startIndex = 1;
        }
        for(int i = startIndex ; i <= CUR_ITEM_VIEW_COUNT;i++){
            if(i == 1){
                totalTime += steamOven.restTimeH * 256 + steamOven.restTime;//设置的工作时间 (秒)
            }else if(i == 2){
                totalTime += steamOven.restTimeH2 * 256 + steamOven.restTime2;//设置的工作时间 (秒)
            }else if(i == 3){
                totalTime += steamOven.restTimeH3 * 256 + steamOven.restTime3;//设置的工作时间 (秒)
            }
        }
        return totalTime;
    }




    /**
     * 初始化每个可用ItemView 的 TAG值，方便后期直接通过TAG来查找ItemView
     */
    private void initOptViewTag(){
        for(int i = 0; i < optContentParentView.getChildCount();i++){
            String indexTag = i +"";
            optContentParentView.getChildAt(i).setTag(indexTag);
            optContentParentView.getChildAt(i).setTag(DATA_KEY,null);
            optContentParentView.getChildAt(i).setOnClickListener(view -> {
//                if(!checkSegmentState(view)){
//                    Toast.makeText(getContext(),"请设置前面内容",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if(isWorking()){
//                    Toast.makeText(getContext(),R.string.steam_cook_setting_prompt,Toast.LENGTH_LONG).show();
//                    return;
//                }
//                //调整到模式设置页面 ： 第一段 - 默认调整到专业模式下的 - 营养蒸； 第二段 - 默认跳转到考模式下的 - 烘焙 ； 第三段 - 默认调整到炸模式 - 空气炸
//                MultiWorkActivity.this.backSettingPage();
            });
        }
    }



    private boolean checkSegmentState(View view){
        String tag = (String) view.getTag();
        int indexTag = Integer.parseInt(tag);
        if(indexTag <= multiSegments.size()){
            return true;
        }
        return false;
    }


    private void initOptContent(){
        for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setTag(DATA_KEY,null);



            TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
            temperatureView.setText("");

            TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
            modelView.setText("");


            TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
            durationView.setText("");


        }
    }

    /**
     * 显示多端信息
     * @param multiSegments
     */
    private void setOptContent(List<MultiSegment> multiSegments){
        if(multiSegments.size() == 0){
            initOptContent();
            return;
        }

        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,multiSegments.get(i));
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setVisibility(View.INVISIBLE);
        }
    }

    private void setOptItemContent(ViewGroup itemGroup,MultiSegment multiSegmentBean){
        itemGroup.setVisibility(View.VISIBLE);
        itemGroup.setTag(DATA_KEY,multiSegmentBean);

        boolean isWork = multiSegmentBean.isCooking() || multiSegmentBean.isPause();
        int textColor = getResources().getColor(isWork ? R.color.steam_white: R.color.steam_mode_b0);

        View view = itemGroup.findViewById(R.id.multi_work_item_index);
        view.setVisibility(isWork ? View.VISIBLE:View.INVISIBLE);

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        temperatureView.setTextColor(textColor);
        //String temperature =  multiSegmentBean.defTemp +"°c";
        temperatureView.setText(TextSpanUtil.getSpan(multiSegmentBean.defTemp,Constant.UNIT_TEMP));

        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        modelView.setTextColor(textColor);
        String model = multiSegmentBean.model;
        modelView.setText(model+"");

        TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
        durationView.setTextColor(textColor);
        //String duration = multiSegmentBean.duration +"min";
        durationView.setText(TextSpanUtil.getSpan(multiSegmentBean.duration*60,Constant.UNIT_TIME_MIN));
    }



    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        sourceId = getIntent().getIntExtra(Constant.WORK_FROM,0);
        setOptContent(multiSegments);
        //cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
        //setOptViewsState()
        if(multiSegments != null && multiSegments.size() > 0){
            workMode = multiSegments.get(multiSegments.size() -1).code;
        }
        preTotalTime = getTotalTime();
        SteamOven steamOven = getSteamOven();
        if(steamOven != null){
            boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
            if(isPreHeat){
                cookDurationView.setText(R.string.steam_preheating);
            }else{
                cookDurationView.setText( TextSpanUtil.getSpan(preTotalTime,Constant.UNIT_TIME_MIN));
            }
        }else{
            cookDurationView.setText(TextSpanUtil.getSpan(preTotalTime,Constant.UNIT_TIME_MIN));
        }
        getCookingData(getSteamOven().guid);
        initLineChart();
    }

    /**
     * 获取剩余运行时间
     * @return
     */
    private int getTotalTime(){
        int totalTime = 0;
        for(MultiSegment segment:multiSegments){
            if(segment.isFinish()){
                continue;
            }
            totalTime += segment.workRemaining;
        }
        return totalTime;
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            showStopWorkDialog();
        }else if(id == R.id.multi_work_pause){//暂停工作
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                ToastUtils.showLong(this, R.string.steam_offline);
                return;
            }
            if(!SteamCommandHelper.checkSteamState(this,steamOven,getSegmentModeCode(steamOven))){
                return;
            }
            SteamCommandHelper.sendWorkCtrCommand(false);
        }else if(id==R.id.multi_work_start){//继续工作
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                ToastUtils.showLong(this, R.string.steam_offline);
                return;
            }
            if(!SteamCommandHelper.checkSteamState(this,steamOven,getSegmentModeCode(steamOven))){
                return;
            }
            SteamCommandHelper.sendWorkCtrCommand(true);
        }
    }

    private int getSegmentModeCode(SteamOven steamOven){
        switch (steamOven.sectionNumber){
            case 2:
                return steamOven.mode2;
            case 3:
                return steamOven.mode3;
            default:
                return steamOven.mode;
        }
    }


    private void backSettingPage(){
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
        setResult(RESULT_OK,result);
        finish();
    }

    SteamCommonDialog endDialog;

    /**
     * 展示结束弹窗
     */
    private void showStopWorkDialog(){
        endDialog = new SteamCommonDialog(this);
        endDialog.setContentText(R.string.steam_work_multi_back_message);
        endDialog.setOKText(R.string.steam_finish_now);
        endDialog.setListeners(v -> {
            endDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
               //goHome();
                //sendEndWorkCommand();
                isInitiativeEnd = true;
                SteamCommandHelper.sendEndWorkCommand(11);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        endDialog.show();
    }


    /**
     *
     * @param cookState true -- 烹饪状态;false -- 暂停烹饪状态
     */
    private void setOptViewsState(SteamOven steamOven,boolean cookState,boolean isPre){
        pauseCookView.setVisibility(cookState?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        curCookInfoViewGroup.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        if(cookState){
            if(isPre){
                cookDurationView.setText(R.string.steam_preheating);
            }else{
                cookDurationView.setText(TextSpanUtil.getSpan(preTotalTime, Constant.UNIT_TIME_MIN));
            }
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
            //SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                return;
            }
            MultiSegment curSegment = MultiSegmentUtil.getCurSegment(steamOven, steamOven.curSectionNbr);
            TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
            TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
            TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);
            curModel.setText(curSegment.model);
            curTemp.setText(TextSpanUtil.getSpan(curSegment.defTemp,Constant.UNIT_TEMP));
            curDuration.setText(TextSpanUtil.getSpan(curSegment.workRemaining,Constant.UNIT_TIME_MIN));
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("MultiActivity onActivityResult " + resultCode);
        if(resultCode == RESULT_OK){
            if(requestCode == WORK_COMPLETE_CODE){
                return;
            }
            dealResult(requestCode,data);
            //setDelBtnState(multiSegments.size() > 0 ? true:false);
        }
    }

    private boolean isAddTime = false;
    private long addTimeMil;
    private boolean isWaringAddTimeSuccess(){
        if(isAddTime && System.currentTimeMillis() - addTimeMil  <= ADD_TIME_MIN_TIME) {
            return  true;
        }
        isAddTime = false;
        return false;
    }



    private void dealResult(int requestCode, Intent data){
//        if(multiSegments.size() > requestCode){//修改当前历史
//            MultiSegment resultData  = data.getParcelableExtra("resultData");
//            multiSegments.remove(requestCode);
//            multiSegments.add(requestCode,resultData);
//        }else{//添加新对象
//            MultiSegment resultData  = data.getParcelableExtra("resultData");
//            multiSegments.add(resultData);
//        }
        multiSegments = data.getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
    }

    private long curveId = 0;

    private void showWorkEndDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_complete);
        steamCommonDialog.setOKText(R.string.steam_common_step_complete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){

            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }



    //数据集合
    private List entryList = new ArrayList<Entry>();
    private int maxYValue = 250;

    /**
     * 初始化LineChart
     */
    private void initLineChart(){
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, false);
        dm.setAxisMaximum(maxYValue);

    }

    //从0开始
    private int curTime = 0;
    private boolean isDestroy = false;
    private Runnable runnable;
    private Handler mHandler = new Handler();
    private void startCreateCurve(){
        runnable = () -> {
            if(isDestroy){
                return;
            }
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                mHandler.postDelayed(runnable, 2000L);
                return;
            }
            if (steamOven.powerState == 0) {
                SteamAbstractControl.getInstance().queryAttribute(steamOven.guid); //查询一体机状态
                mHandler.postDelayed(runnable, 2000L);
                return;
            }

            if (steamOven.workState != SteamStateConstant.WORK_STATE_PREHEAT &&
                    steamOven.workState != SteamStateConstant.WORK_STATE_WORKING) {
                mHandler.postDelayed(runnable, 2000L);
                return;
            }
            if(isPageHide){
                return;
            }

            SteamAbstractControl.getInstance().queryAttribute(steamOven.guid); //查询一体机状态
            curTime += 2;
            //tvTime.setText(DateUtil.secForMatTime3(curTime) + "min");
            Entry entry = new Entry(curTime, (float) steamOven.curTemp);
            if(maxYValue < steamOven.curTemp + 50){
                maxYValue = steamOven.curTemp + 50;
                dm.setAxisMaximum(maxYValue);
            }
            //Entry entry = new Entry(curTime, (float) Math.random()*250);
            dm.addEntry(entry, 0);
            //highlights.set(highlights.size() - 1, new Highlight(entry.getX(), entry.getY(), 0)); //标记highlight只能加最后，maskview中坐标会覆盖
            //cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
            mHandler.postDelayed(runnable, 2000L);
        };
        dm.setAxisMaximum(maxYValue);
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart_multi), entryList, true, false);
        cookChart.notifyDataSetChanged();
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
    }

    private Handler dataHandler = new Handler();
    private int errorCount = 0;
    protected void getCookingData(final String guid) {
        CloudHelper.getCurveBookForDevice(this, guid, GetCurveDetailRes.class,
                new RetrofitCallback<GetCurveDetailRes>() {
                    @Override
                    public void onSuccess(GetCurveDetailRes getDeviceParamsRes) {
                        dataHandler.removeCallbacksAndMessages(null);
                        if(errorCount <= 2 && !isDestroyed() && (getDeviceParamsRes == null || getDeviceParamsRes.payload == null)){
                            errorCount++;
                            dataHandler.postDelayed(()->{getCookingData(guid);},1000);
                            return;
                        }
                        try {
                            parserCureData(getDeviceParamsRes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            startCreateCurve();
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        //initLineChart();
                        startCreateCurve();
                    }
                });
    }

    /**
     * 解析曲线温度数据
     * @param getDeviceParamsRes
     * @throws JSONException
     */
    private void parserCureData(GetCurveDetailRes getDeviceParamsRes) throws JSONException {
        if(getDeviceParamsRes != null && getDeviceParamsRes.payload != null){
            curveId = getDeviceParamsRes.payload.curveCookbookId;
        }
        if(getDeviceParamsRes == null || getDeviceParamsRes.payload == null || getDeviceParamsRes.payload.temperatureCurveParams == null){
            //initLineChart();
            initStartTimerAndList();
            startCreateCurve();
            return;
        }

        JSONObject jsonObject = new JSONObject(getDeviceParamsRes.payload.temperatureCurveParams);
        Iterator<String> keys = jsonObject.keys();
        if(keys == null || !keys.hasNext()){
            //initLineChart();
            initStartTimerAndList();
            startCreateCurve();
            return;
        }
        List<String> keyList = new LinkedList<>();
        while (keys.hasNext()){
            String key = keys.next();
            if(keyList.contains(key)){
                continue;
            }
            keyList.add(key);
            String value = jsonObject.getString(key);
            String temp = value.split("-")[0];
            if(temp == null || temp.trim().length() == 0){
                break;
            }
            if(maxYValue < Integer.parseInt(temp.trim())){
                maxYValue = Integer.parseInt(temp.trim());
            }
            Entry entry = new Entry(Integer.parseInt(key),Integer.parseInt(temp.trim()));
            entryList.add(entry);
        }
        Collections.sort(entryList,  (Entry bean1, Entry bean2) -> {
            if (bean1.getX() > bean2.getX()) {
                return 1;
            } else if (bean1.getX() == bean2.getX()) {
                return 0;
            } else {
                return -1;
            }
        });
        initStartTimerAndList();
        //initLineChart();
        //dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
        startCreateCurve();
    }

    SteamCommonDialog finishDialog;
    private boolean showDialog = false;
    private void showWorkFinishDialog(){
        if(showDialog || (finishDialog != null && finishDialog.isShow())){
            return;
        }
        showDialog = true;
        finishDialog = new SteamCommonDialog(this);
        finishDialog.setContentText(R.string.steam_work_complete);
        finishDialog.setOKText(R.string.steam_common_step_complete);
        finishDialog.setCancelText(R.string.steam_go_home);
        finishDialog.setListeners(v -> {
            finishDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//完成
                //去往烹饪结束页面
                sendWorkFinishCommand(DIRECTIVE_OFFSET_WORK_FINISH);
            }else if(v.getId() == R.id.tv_cancel) {//回到主页
                sendWorkFinishCommand(DIRECTIVE_OFFSET_GO_HOME);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        finishDialog.show();
    }

    /**
     * 工作结束，发送命令
     */
    private void sendWorkFinishCommand(int code){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 2);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + code);
    }

    /**
     * 跳转到曲线保存界面
     */
    private void toCurveSavePage(){
        CurveDataUtil.initList((ArrayList<Entry>) entryList);
        Intent intent = new Intent(this,CurveSaveActivity.class);
        intent.putExtra(Constant.CURVE_ID,curveId);
        intent.putExtra(Constant.CARVE_NAME,"多段模式");
        startActivity(intent);
        finish();
    }


    private void dismissAllDialog(){
        if(finishDialog != null && finishDialog.isShow()){
            finishDialog.dismiss();
        }
//        if(steamCommonDialog != null && steamCommonDialog.isShow()){
//            steamCommonDialog.dismiss();
//        }
    }

    @Override
    public void goHome() {
        dismissAllDialog();
        super.goHome();
    }

    private void initStartTimerAndList(){
        if(entryList.size() > 0){
            curTime = (int) ((Entry)entryList.get(entryList.size() -1)).getX();
        }
        if(entryList.size() == 0){
            SteamOven steamOven = getSteamOven();
            if(steamOven != null){
                Entry entry = new Entry(1, steamOven.curTemp);
                entryList.add(entry);
            }
            curTime = 1;
        }
    }

    private void dealWorkFinish(SteamOven steamOven){
        if(showDialog){
            return;
        }
        showWorkFinishDialog();
    }

    /**
     * 烹饪完成
     * @param steamOven
     */
    private void toAddTimePage(SteamOven steamOven){
        CurveDataUtil.initList((ArrayList<Entry>) entryList);
        Intent intent = new Intent(this,WorkCompleteActivity.class);
        intent.putExtra(Constant.RECIPE_ID,0);
        intent.putExtra(Constant.CURVE_ID,curveId);
        intent.putExtra(Constant.WORK_MODE,workMode);
        intent.putExtra(Constant.CARVE_NAME,"多段烹饪");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPageHide = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPageHide = false;
    }
}
