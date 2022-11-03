package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.ComnConstant;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CurveStep;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//一体机多段
public class MultiWorkActivity extends SteamBaseActivity {

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

    private boolean isStart = false;
    private static final int REQUEST_CODE_SETTING  = 321;


    private LineChart cookChart;
    private DynamicLineChartManager dm;

    private int preTotalTime;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_multi_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
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
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                            updateViewInfo(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            goHome();
                            break;
                    }

                }
            }
        });
    }

    /**
     * 更新页面展示 (?多段模式下 - 返回的是所有段落数据，还是当前工作段落数据)
     * @param steamOven
     */
    private void updateViewInfo(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                boolean cookState = ((steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT) || (steamOven.workState ==  SteamStateConstant.WORK_STATE_WORKING));
                preTotalTime = getTotalTime(steamOven);
                setOptViewsState(cookState);
                //改变段落数据显示状态
                updateSegmentInfo(steamOven);
                break;
            case SteamStateConstant.WORK_STATE_LEISURE:
                break;
        }




        //cookDurationView.setText(getSpan(getTotalTime(steamOven),Constant.UNIT_TIME_MIN));
        //更改开始/暂停按钮显示状态

        //绘制曲线
    }



    private void updateSegmentInfo(SteamOven steamOven){
        int maxCount = steamOven.sectionNumber >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : steamOven.sectionNumber;
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,getCurSegment(steamOven,i+1));
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setVisibility(View.INVISIBLE);
        }
    }

    private MultiSegment getCurSegment(SteamOven steamOven,int index){
        MultiSegment segment = new MultiSegment();
        if(steamOven.sectionNumber == index){
            segment.setCookState(MultiSegment.COOK_STATE_START);
        }
        switch (index){
            case 0:
            case 1:
                segment.code = steamOven.mode;
                segment.model = SteamModeEnum.match(steamOven.mode);
                segment.defTemp = steamOven.setUpTemp;
                segment.downTemp = steamOven.setDownTemp;
                int time = steamOven.setTimeH * 256 + steamOven.setTime;//设置的工作时间 (秒)
                segment.duration = time/60 + (time%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam;
                int restTime = steamOven.restTime * 256 + steamOven.restTimeH;//设置的工作时间 (秒)
                segment.workRemaining = restTime;
                break;
            case 2:
                segment.code = steamOven.mode2;
                segment.model = SteamModeEnum.match(steamOven.mode2);
                segment.defTemp = steamOven.setUpTemp2;
                segment.downTemp = steamOven.setDownTemp2;
                int time2 = steamOven.setTimeH2 * 256 + steamOven.setTime2;//设置的工作时间 (秒)
                segment.duration = time2/60 + (time2%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam2;
                int restTime2 = steamOven.restTime2 * 256 + steamOven.restTimeH2;//设置的工作时间 (秒)
                segment.workRemaining = restTime2;
                break;
            case 3:
                segment.code = steamOven.mode3;
                segment.model = SteamModeEnum.match(steamOven.mode3);
                segment.defTemp = steamOven.setUpTemp3;
                segment.downTemp = steamOven.setDownTemp3;
                int time3 = steamOven.setTimeH3 * 256 + steamOven.setTime3;//设置的工作时间 (秒)
                segment.duration = time3/60 + (time3%60 == 0 ? 0 : 1);
                segment.steam = steamOven.steam3;
                int restTime3 = steamOven.restTime3 * 256 + steamOven.restTimeH3;//设置的工作时间 (秒)
                segment.workRemaining = restTime3;
                break;
        }
        return segment;
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
                totalTime += steamOven.setTimeH * 256 + steamOven.setTime;//设置的工作时间 (秒)
            }else if(i == 2){
                totalTime += steamOven.setTimeH2 * 256 + steamOven.setTime2;//设置的工作时间 (秒)
            }else if(i == 3){
                totalTime += steamOven.setTimeH3 * 256 + steamOven.setTime3;//设置的工作时间 (秒)
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
        temperatureView.setText(getSpan(multiSegmentBean.defTemp,Constant.UNIT_TEMP));

        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        modelView.setTextColor(textColor);
        String model = multiSegmentBean.model;
        modelView.setText(model+"");

        TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
        durationView.setTextColor(textColor);
        //String duration = multiSegmentBean.duration +"min";
        durationView.setText(getSpan(multiSegmentBean.duration*60,Constant.UNIT_TIME_MIN));
    }



    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
        cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
        //setOptViewsState()
        preTotalTime = getTotalTime();
        cookDurationView.setText(getSpan(preTotalTime,Constant.UNIT_TIME_MIN));
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
//            if(isWorking()){
//                showStopWorkDialog();
//                return;
//            }
//            this.backSettingPage();
            showStopWorkDialog();
        }else if(id == R.id.multi_work_pause){//暂停工作
            this.setOptViewsState(false);
        }else if(id==R.id.multi_work_start){//继续工作
            this.setOptViewsState(true);
        }
    }

    private void backSettingPage(){
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
        setResult(RESULT_OK,result);
        finish();
    }

    /**
     * 展示结束弹窗
     */
    private void showStopWorkDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
               goHome();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    /**
     * 跳转到主页
     */
    private void goHome() {
        Intent intent = new Intent(MultiWorkActivity.this,MainActivity.class);
        startActivity(intent);
    }


    /**
     * 是否正在烹饪
     * @return
     */
    private boolean isWorking(){
        return pauseCookView.getVisibility() == View.VISIBLE;
    }


    /**
     *
     * @param cookState true -- 烹饪状态;false -- 暂停烹饪状态
     */
    private void setOptViewsState(boolean cookState){
        pauseCookView.setVisibility(cookState?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        curCookInfoViewGroup.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        if(cookState){
            cookDurationView.setText(getSpan(preTotalTime, Constant.UNIT_TIME_MIN));
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                return;
            }
            MultiSegment curSegment = getCurSegment(steamOven, steamOven.sectionNumber);
            TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
            TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
            TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);
            curModel.setText(curSegment.model);
            curTemp.setText(getSpan(curSegment.defTemp,Constant.UNIT_TEMP));
            curDuration.setText(getSpan(curSegment.workRemaining,Constant.UNIT_TIME_MIN));
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("MultiActivity onActivityResult " + resultCode);
        if(resultCode == RESULT_OK){
            dealResult(requestCode,data);
            //setDelBtnState(multiSegments.size() > 0 ? true:false);
        }
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

    private long curveId = 3432;
    //曲线详情
    private SteamCurveDetail panCurveDetail;
    //获取曲线详情
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, curveId, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.payload) {
                    panCurveDetail = getCurveDetailRes.payload;
                    //这里用了曲线名
                    //tvRecipeName.setText(panCurveDetail.name);

                    List<CurveStep> curveSteps = new ArrayList<>();
                    if (null != panCurveDetail.stepList) {
                        curveSteps.addAll(panCurveDetail.stepList);
                        //tvStartCook.setVisibility(View.VISIBLE);
                    }
                    //rvStep3Adapter.setList(curveSteps);
                    //画曲线
                    drawCurve(panCurveDetail);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    //曲线绘制
    private void drawCurve(SteamCurveDetail panCurveDetail) {
        Map<String, String> params = null;
        try {
            String[] data = new String[3];
            params = new Gson().fromJson(panCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            ArrayList<Entry> entryList = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                data = entry.getValue().split("-");
                entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
            }

            dm = new DynamicLineChartManager(cookChart, this);
            dm.setLabelCount(5, 5);
            dm.setAxisLine(true, false);
            dm.setGridLine(false, false);
            dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
            cookChart.notifyDataSetChanged();
            //绘制步骤标记
            List<CurveStep> stepList = panCurveDetail.stepList;
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
            //tvFire.setText("火力：" + data[1] + "档");
            //tvTemp.setText("温度：" + data[0] + "℃");
            //tvTime.setText("时间：" + TimeUtils.secToMinSecond(panCurveDetail.needTime));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            params = null;
        }
    }

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

    /**
     * 获取温度/时间样式
     * @param value 当 unit 是 Constant.UNIT_TEMP 时 ，value 时温度值；
     *              当 unit 是 Constant.UNIT_TIME_MIN，value 是时间值，单位秒
     * @param unit  Constant.UNIT_TIME_MIN 或 Constant.UNIT_TEMP
     * @return
     */
    private SpannableString getSpan(int value, String unit){
        if(unit.equals(Constant.UNIT_TIME_MIN)){
            String time = TimeUtils.secToHourMinUp(value);
            SpannableString spannableString = new SpannableString(time);
            int pos = time.indexOf(Constant.UNIT_TIME_H);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.8f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = time.indexOf(Constant.UNIT_TIME_MIN);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.8f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }else{
            SpannableString spannableString = new SpannableString(value+unit);
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(new RelativeSizeSpan(0.8f), (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(superscriptSpan,  (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

    }






}
