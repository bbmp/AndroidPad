package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CurveStep;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.http.CloudHelper;
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

    private TextView preHeadTv;



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
        setOnClickListener(R.id.multi_work_pause,R.id.multi_work_start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showWorkFinishDialog();
            }
        },5000);
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
        setOptViewsState(true);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            if(isWorking()){
                showStopWorkDialog();
                return;
            }
            this.backSettingPage();
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

    private void showStopWorkDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                Intent intent = new Intent(ModelWorkActivity.this,MainActivity.class);
                startActivity(intent);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
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
        MultiSegment segment = multiSegments.get(0);
        segment.setCookState(cookState?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        if(cookState){
            cookDurationView.setText("55" +"°c");
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            preHeadTv.setVisibility(segment.isPreheat()?View.VISIBLE:View.INVISIBLE);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
        }
        preHeadTv.setVisibility((segment.isPreheat() && segment.isCooking())?View.VISIBLE:View.INVISIBLE);
        TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
        TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
        TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);

        curModel.setText(segment.model);
        curTemp.setText(segment.defTemp +"°c");
        curDuration.setText(segment.duration+"min");
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
                steamCommonDialog.dismiss();
                //切换到烹饪结束状态
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

}
