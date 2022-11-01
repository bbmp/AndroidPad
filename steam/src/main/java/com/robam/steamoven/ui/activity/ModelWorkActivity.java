package com.robam.steamoven.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.mqtt.MsgKeys;
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
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamOverTimeDialog;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//一体机多段
public class ModelWorkActivity extends SteamBaseActivity {


    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  DATA_KEY = R.id.multi_opt;

    private ImageView pauseCookView;
    private ImageView continueCookView;
    private TextView cookDurationView;
    private ViewGroup curCookInfoViewGroup;
    private boolean isStart = false;
    private LineChart cookChart;
    private DynamicLineChartManager dm;

    private TextView preHeadTv;//预热文本
    private EditText finishTv;
    private View finishOptView;
    private TextView finishGoHomeTv;
    private TextView finishSaveCurve;
    private ImageView steamIv;
    private TextView steamTv;
    private int directive_offset = 11000000;
    private static final int DIRECTIVE_OFFSET_END = 10;

    private int preValue;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_model_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        pauseCookView = findViewById(R.id.multi_work_pause);
        continueCookView = findViewById(R.id.multi_work_start);
        cookDurationView = findViewById(R.id.multi_work_total);
        curCookInfoViewGroup = findViewById(R.id.multi_work_cur_info);
        cookChart = findViewById(R.id.cook_chart);
        preHeadTv = findViewById(R.id.model_work_prompt);
        finishTv = findViewById(R.id.multi_work_finish_prompt);
        finishOptView = findViewById(R.id.multi_work_finish_opt);
        finishGoHomeTv = findViewById(R.id.finish_go_home);
        finishSaveCurve = findViewById(R.id.finish_save_curve);
        steamIv = findViewById(R.id.multi_work_ic_steam);
        steamTv = findViewById(R.id.multi_work_tv_steam);
        setOnClickListener(R.id.multi_work_pause,R.id.multi_work_start,R.id.multi_work_finish_opt,R.id.finish_go_home,R.id.finish_save_curve);
        MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s - directive_offset){
                case DIRECTIVE_OFFSET_END:
                    goHome();
                    break;
            }
        });

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
                            updateViews(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            goHome();
                            break;
                    }

                }
            }
        });
    }


    private void updateViews(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE:
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                updateViewsPreheat(steamOven);
                break;
        }
    }


    /**
     * 预热模式 - 更新视图
     * @param steamOven
     */
    private void updateViewsPreheat(SteamOven steamOven){
        boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
        boolean isWorking = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_WORKING);

        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
        MultiSegment segment = multiSegments.get(0);
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
        int timeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余执行时间
        preValue = timeF;
        if(isWorking){
            if(isPreHeat){
                //TODO(替换成预热当前温度)
                cookDurationView.setText(getSpan("55"+"","°c"));
            }else{
                cookDurationView.setText(getSpan(timeF+"","min"));
            }
        }else{
            if(!isPreHeat){//显示湿度控制View
                //TODO(获取是否处于加湿状态)
                steamIv.setVisibility(View.VISIBLE);
                steamTv.setVisibility(View.INVISIBLE);
            }
            cookDurationView.setText(R.string.steam_cook_in_pause);
            preHeadTv.setVisibility(isPreHeat?View.VISIBLE:View.INVISIBLE);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
        }
        preHeadTv.setVisibility((isPreHeat && isWorking)?View.VISIBLE:View.INVISIBLE);
        TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
        TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
        TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);

        curDuration.setVisibility((isPreHeat && isWorking)?View.INVISIBLE:View.VISIBLE);
        curModel.setText(segment.model);
        curTemp.setText(segment.defTemp+"");
        curDuration.setText(segment.duration+"");
    }

    private SpannableString getSpan(String value,String unit){
        String time = value + unit;
        SpannableString spannableString = new SpannableString(time);
        if(unit.equals("min")){
            int pos = time.indexOf("min");
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(new RelativeSizeSpan(0.5f), value.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(superscriptSpan, value.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }






    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        //展示
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        //TODO(“上线后去掉提示”)
        if(multiSegments.size() == 0){
            Toast.makeText(this,"缺少模式数据",Toast.LENGTH_LONG).show();
        }
        cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
        setOptViewsState(true,true,multiSegments.get(0).duration);
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
            SteamOven steamOven = getSteamOven();
            boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
            this.setOptViewsState(false,isPreHeat,preValue);
        }else if(id==R.id.multi_work_start){//继续工作
            SteamOven steamOven = getSteamOven();
            boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
            this.setOptViewsState(true,isPreHeat,preValue);
        }else if(id==R.id.finish_go_home){//回到主页
            goHome();
        }else if(id==R.id.finish_save_curve){//保存曲线
            //TODO(调用保存曲线接口)
            goHome();
        }else if(id == R.id.multi_work_finish_opt){
            finishTv.setFocusable(true);
            finishTv.setFocusableInTouchMode(true);
            finishTv.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(finishTv,0);
        }
    }

    private void backSettingPage(){
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
        setResult(RESULT_OK,result);
        finish();
    }

    private void showStopWorkDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                endWork();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    /**
     * 结束工作
     */
    private void endWork(){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + DIRECTIVE_OFFSET_END);
    }

    private void changeWorkState(boolean needPause){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        if(needPause){
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_TIME_OUT);//暂停工作
        }else{
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_CONTINUE);//继续工作
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + DIRECTIVE_OFFSET_END);
    }

    private void goHome(){
        Intent intent = new Intent(ModelWorkActivity.this,MainActivity.class);
        startActivity(intent);
    }



    /**
     * 是否正在烹饪
     * @return
     */
    private boolean isWorking(){
        return pauseCookView.getVisibility() == View.VISIBLE;
    }


    private void setOptViewsState(boolean isWorking,boolean isPreHeat,int time){
        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
        MultiSegment segment = multiSegments.get(0);
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        int timeF = time * 60;
        if(isWorking){
            if(isPreHeat){
                //TODO(替换成预热当前温度)
                cookDurationView.setText(getSpan("55"+"","°c"));
            }else{
                cookDurationView.setText(getSpan(timeF+"","min"));
            }
        }else{
            if(!isPreHeat){//显示湿度控制View
                //TODO(获取是否处于加湿状态)
                steamIv.setVisibility(View.VISIBLE);
                steamTv.setVisibility(View.INVISIBLE);
            }
            cookDurationView.setText(R.string.steam_cook_in_pause);
            preHeadTv.setVisibility(isPreHeat?View.VISIBLE:View.INVISIBLE);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
        }
        preHeadTv.setVisibility((isPreHeat && isWorking)?View.VISIBLE:View.INVISIBLE);
        TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
        TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
        TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);

        curDuration.setVisibility((isPreHeat && isWorking)?View.INVISIBLE:View.VISIBLE);
        curModel.setText(segment.model);
        curTemp.setText(segment.defTemp+"");
        curDuration.setText(segment.duration+"");


    }

    /**
     * 是否预热状态
     * TODO(后期改成真实的字段判断)
     * @return
     */
    private boolean isPreheat(){
        return true;
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

    private void showWorkFinishDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setCancelText(R.string.steam_work_complete_add_time);
        steamCommonDialog.setContentText(R.string.steam_work_complete);
        steamCommonDialog.setOKText(R.string.steam_common_step_complete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//完成
                //切换到烹饪结束状态
                setFinishState();
            }else if(v.getId() == R.id.tv_cancel) {//加时
                showOverTimeDialog();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    private void showOverTimeDialog(){
        SteamOverTimeDialog timeDialog = new SteamOverTimeDialog(this);
        timeDialog.setContentText(R.string.steam_cancel);
        timeDialog.setOKText(R.string.steam_confirm);
        timeDialog.setData();
        timeDialog.setListeners(v -> {
            timeDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//确认
                //切换到烹饪结束状态

            }else if(v.getId() == R.id.tv_cancel) {//取消

            }
        },R.id.tv_cancel,R.id.tv_ok);
        timeDialog.show();
    }

    private void setFinishState(){

        //TODO(设置当前模式名称)
        //finishTv.setText();

        finishTv.setVisibility(View.VISIBLE);
        finishOptView.setVisibility(View.VISIBLE);
        finishGoHomeTv.setVisibility(View.VISIBLE);
        finishSaveCurve.setVisibility(View.VISIBLE);


        curCookInfoViewGroup.setVisibility(View.INVISIBLE);
        pauseCookView.setVisibility(View.INVISIBLE);
        continueCookView.setVisibility(View.INVISIBLE);
        cookDurationView.setVisibility(View.INVISIBLE);
        preHeadTv.setVisibility(View.INVISIBLE);

    }

}
