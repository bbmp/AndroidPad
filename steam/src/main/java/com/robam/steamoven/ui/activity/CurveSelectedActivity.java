package com.robam.steamoven.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.ui.view.MarkViewStep;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastInsUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CookingCurveQueryRes;
import com.robam.steamoven.bean.CurveData;
import com.robam.steamoven.bean.CurveStep;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.adapter.RvStep3Adapter;
import com.robam.steamoven.utils.MqttSignal;
import com.robam.steamoven.utils.MultiSegmentUtil;
import com.robam.steamoven.utils.SkipUtil;
import com.robam.steamoven.utils.SteamPageData;
import com.robam.steamoven.utils.TempAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//曲线选中进入
public class CurveSelectedActivity extends SteamBaseActivity {
    private long curveId;
    //曲线步骤
    private RvStep3Adapter rvStep3Adapter;

    private RecyclerView rvStep;
    //曲线名字
    private TextView tvCurveName;
    //开始烹饪
    private TextView tvStartCook;
    //曲线详情
    private CurveData steamCurveDetail;
    //
    private LineChart cookChart;

    private DynamicLineChartManager dm;

    private int directive_offset = 17100000;

    private MqttSignal mqttSignal;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_curve_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        if (null != getIntent())
            curveId = getIntent().getLongExtra(SteamConstant.EXTRA_CURVE_ID, -1);
        rvStep = findViewById(R.id.rv_step);
        tvCurveName = findViewById(R.id.tv_recipe_name);
        tvCurveName.setOnClickListener(this);
        findViewById(R.id.name_change_opt).setOnClickListener(this);
        tvStartCook = findViewById(R.id.tv_start_cook);
        cookChart = findViewById(R.id.cook_chart);
        cookChart.setNoDataText(""); //没有数据时显示的文字

        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_15)));
        rvStep3Adapter = new RvStep3Adapter();
        rvStep.setAdapter(rvStep3Adapter);

        setOnClickListener(R.id.tv_start_cook);

//        MqttDirective.getInstance().getDirective().observe(this, s -> {
//            switch (s - directive_offset){
//                case MsgKeys.setDeviceAttribute_Req:
//                    toWorkPage();
//                    break;
//            }
//        });
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
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            toWorkPage2(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }
                }
            }
        });

        SteamPageData.getInstance().getBsData().observe(this,bsData->{
            if(bsData == null){
                return;
            }
            if(StringUtils.isNotEmpty(bsData.content)){
                tvCurveName.setText(bsData.content);
            }
        });
    }

    private void toWorkPage2(SteamOven steamOven){
        if(steamOven.mode == 0){
            return;
        }
        SkipUtil.toWorkPage(steamOven,this);
    }

    /**
     * 跳转到指定业务页面
     * @param steamOven
     */
    private void toWorkPage(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE:
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT://预约页面
                HomeSteamOven.getInstance().orderTime = steamOven.orderLeftTime;
                Intent appointIntent = new Intent(this,AppointingActivity.class);
                MultiSegment segment = MultiSegmentUtil.getSkipResult(steamOven);
                segment.workRemaining = steamOven.orderLeftTime;
                appointIntent.putExtra(Constant.SEGMENT_DATA_FLAG, segment);
                startActivity(appointIntent);
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                //辅助模式工作页面
                if(SteamModeEnum.isAuxModel(steamOven.mode)){
                    Intent intent = new Intent(this,AuxModelWorkActivity.class);
                    intent.putExtra(Constant.SEGMENT_DATA_FLAG,MultiSegmentUtil.getSkipResult(steamOven));
                    startActivity(intent);
                    return;
                }

                Intent intent;
                List<MultiSegment> list;
                if(steamOven.sectionNumber >= 2){
                    //多段工作页面
                    intent = new Intent(this, MultiWorkActivity.class);
                    list = getMultiWorkResult(steamOven);
                }else{
                    //基础模式工作页面
                    intent = new Intent(this, ModelWorkActivity.class);
                    list = new ArrayList<>();
                    list.add(MultiSegmentUtil.getSkipResult(steamOven));
                }
                intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取多段数据集合
     * @return
     */
    private List<MultiSegment> getMultiWorkResult(SteamOven steamOven){
        List<MultiSegment> multiSegments = new ArrayList<>();
        for(int i = 0;i < steamOven.sectionNumber;i++){
            multiSegments.add(MultiSegmentUtil.getCurSegment(steamOven,i+1));
        }
        return multiSegments;
    }

    /**
     * 跳转到工作页
     */
    private void toWorkPage(){
        List<MultiSegment> multiSegment = getMultiSegment();
        if(multiSegment.size() >= 2){
            Intent intent = new Intent(this,MultiWorkActivity.class);
            intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegment);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this,ModelWorkActivity.class);
            intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegment);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void initData() {
        getCurveDetail();
        mqttSignal = new MqttSignal();
    }

    //曲线详情
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, curveId, CookingCurveQueryRes.class, new RetrofitCallback<CookingCurveQueryRes>() {
            @Override
            public void onSuccess(CookingCurveQueryRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.data) {
                    steamCurveDetail = getCurveDetailRes.data;
                    tvCurveName.setText(steamCurveDetail.name+"");
                    //绘制曲线
                    drawCurve(steamCurveDetail);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_start_cook) {
            sendWorkCommand();
        } else if (id == R.id.ll_left) { //返回
            finish();
        }else if(id == R.id.name_change_opt){
            goNameChangePage();
        }else if(id == R.id.tv_recipe_name){
            goNameChangePage();
        }

    }

    private void goNameChangePage(){
        if(steamCurveDetail == null || steamCurveDetail.curveCookbookId == 0){
            ToastInsUtils.showLong(this,R.string.steam_curve_info_error_prompt);
            return;
        }
        Intent intent = new Intent(this,CurveNameChangeActivity.class);
        intent.putExtra(SteamConstant.EXTRA_CURVE_ID,steamCurveDetail.curveCookbookId);
        startActivity(intent);
    }


//    private void toRestorePage(){
//        //曲线还原
//        Intent intent = new Intent();
//        if (null != steamCurveDetail){
//            intent.putExtra(SteamConstant.EXTRA_CURVE_DETAIL, steamCurveDetail);
//        }
//        intent.setClass(this, CurveRestoreActivity.class);
//        startActivity(intent);
//        finish();
//    }


    /**
     * 去往工作页面
     */
    private void sendWorkCommand(){
        if(steamCurveDetail == null || steamCurveDetail.curveCookbookId == 0){
            ToastInsUtils.showLong(this,R.string.steam_curve_info_error_prompt);
            return;
        }
        List<MultiSegment> multiSegment = getMultiSegment();
        if(multiSegment == null || multiSegment.size() == 0){
            Toast.makeText(this,R.string.steam_curve_exception_data,Toast.LENGTH_LONG).show();
            finish();
        }
        for(int i = 0;i < multiSegment.size();i++){
            if(toRemainPage(getSteamOven(),multiSegment.get(i).code)){
                return;
            }
        }
        if(steamCurveDetail.needTime < 60){
            ToastInsUtils.showLong(this,R.string.steam_curve_not_work);
            return;
        }
        if(multiSegment.size() >= 2){
            SteamCommandHelper.sendMultiWork(this,multiSegment,directive_offset+ MsgKeys.setDeviceAttribute_Req);
        }else{
            SteamCommandHelper.startModelWork(multiSegment.get(0),null,MsgKeys.setDeviceAttribute_Req+directive_offset);
        }
    }


    /**
     * 获取工作模式对象列表
     */
    private List<MultiSegment> getMultiSegment() {
        try{
            List<MultiSegment> multiSegmentEnums = new ArrayList<>();
            String curveSettingParams = steamCurveDetail.curveSettingParams;
            if(StringUtils.isEmail(curveSettingParams)){
                return null;
            }

            JSONArray jsonArray = new JSONArray(curveSettingParams);
            if(jsonArray.length() == 0){
                return null;
            }
            for(int i = 0;i < jsonArray.length();i++){
                multiSegmentEnums.add(getSegment(jsonArray.getJSONObject(i)));
            }
            multiSegmentEnums.get(0).setWorkModel(MultiSegment.COOK_STATE_PREHEAT);
            multiSegmentEnums.get(0).setCookState(MultiSegment.COOK_STATE_START);
            return multiSegmentEnums;
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
        }
        return null;
    }

    /**
     * 获取模式对象
     * @param object
     * @return
     */
    private MultiSegment getSegment(JSONObject object) throws JSONException {
        MultiSegment segment = new MultiSegment();
        segment.code = object.getInt("mode");
        segment.model = object.getString("modeName");
        segment.duration = object.optInt("setTime")/60;
        segment.defTemp = object.optInt("setUpTemp");
        segment.downTemp = object.optInt("setDownTemp");
        segment.steam = object.optInt("steamVolume");
        segment.workRemaining =segment.duration*60;
        return segment;
    }

    //曲线绘制
    private void drawCurve(CurveData steamCurveDetail) {
        try {
            if(steamCurveDetail == null || StringUtils.isEmpty(steamCurveDetail.temperatureCurveParams)){
                cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
                cookChart.requestLayout();
                return;
            }
            Map<String, String> params = new Gson().fromJson(steamCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            ArrayList<Entry> entryList = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String[] data = entry.getValue().split("-");
                entryList.add(new Entry(Float.parseFloat(entry.getKey()), Float.parseFloat(data[0]))); //时间和温度
            }

            dm = new DynamicLineChartManager(cookChart, this);
            dm.setLabelCount(5, 5);
            dm.setAxisLine(true, false);
            dm.setGridLine(false, false);
            dm.setYAxiFormat(new TempAxisValueFormatter(0));
            dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
            cookChart.setTouchEnabled(false);
            if(entryList.size() == 0){
                cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
            }
            cookChart.notifyDataSetChanged();
            //绘制步骤标记
//            List<CurveStep> stepList = steamCurveDetail.stepList;
//            if (null != stepList) {
//                MarkViewStep mv = new MarkViewStep(this, cookChart.getXAxis().getValueFormatter());
//                mv.setChartView(cookChart);
//                cookChart.setMarker(mv);
//                List<Highlight> highlights = new ArrayList<>();
//                int dataIndex = 1;
//                for (CurveStep step : stepList) {
//                    highlights.add(new Highlight(Float.parseFloat(step.markTime), step.markTemp, 0, dataIndex));
//                    dataIndex++;
//                }
//                cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
//            }
            //最后一点
//            tvFire.setText("火力：" + data[1] + "档");
//            tvTemp.setText("温度：" + data[0] + "℃");
//            tvTime.setText("时间：" + TimeUtils.secToMinSecond(steamCurveDetail.needTime));
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mqttSignal.pageHide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqttSignal.pageShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttSignal.clear();
    }

    private  boolean toRemainPage(SteamOven steamOven,int curModelCode){
        if(steamOven == null){
            showRemindPage(R.string.steam_offline,false,-1,false,false);
            return true;
        }
        boolean needWater = SteamModeEnum.needWater(curModelCode);
        int promptResId = SteamCommandHelper.getRunPromptResId(steamOven, curModelCode, SteamModeEnum.needWater(curModelCode),true);
        if(promptResId != -1){
            showRemindPage(promptResId,needWater,curModelCode,true,false);
            return true;
        }
        return false;
    }

}