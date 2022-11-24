package com.robam.steamoven.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CurveStep;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 曲线保存页面
 */
public class CurveSaveActivity extends SteamBaseActivity {


    //设置段数据
    //当前段数（3段）

    private LineChart cookChart;
    private DynamicLineChartManager dm;

    private EditText finishTv;
    private View finishOptView;
    private TextView finishGoHomeTv;
    private TextView finishSaveCurve;

    private int directive_offset = 11000000;
    private static final int DIRECTIVE_OFFSET_END = 10;
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;
    private static final int DIRECTIVE_OFFSET_OVER_TIME = 40;

    //数据集合
    private List entryList = new ArrayList<Entry>();

    private float maxYValue = 0;
    //曲线ID
    private long curveId;

    private GetCurveDetailRes curveDetailRes;
    private String promptText;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_curve_save;
    }

    @Override
    protected void initView() {
        //showLeft();
        showCenter();
        showRightCenter();
        cookChart = findViewById(R.id.cook_chart);
        finishTv = findViewById(R.id.multi_work_finish_prompt);
        finishOptView = findViewById(R.id.multi_work_finish_opt);
        finishGoHomeTv = findViewById(R.id.finish_go_home);
        finishSaveCurve = findViewById(R.id.finish_save_curve);
        setOnClickListener(R.id.multi_work_finish_opt,R.id.finish_go_home,R.id.finish_save_curve);
    }


    //private DynamicLineChartManager dm;
    /**
     * 初始化LineChart
     */
    private void initLineChart(){
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, false);
        dm.setAxisMaximum(maxYValue+50);
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
        cookChart.notifyDataSetChanged();
    }

    //从0开始

    /**
     * 解析曲线温度数据
     * @param getDeviceParamsRes
     * @throws JSONException
     */
    private void parserCureData(GetCurveDetailRes getDeviceParamsRes) throws JSONException {
        this.curveDetailRes = getDeviceParamsRes;
        if(getDeviceParamsRes == null || getDeviceParamsRes.payload == null || getDeviceParamsRes.payload.temperatureCurveParams == null){
            ToastUtils.showLong(this,R.string.steam_curve_no_data);
            cookChart.setNoDataText(getString(R.string.steam_curve_no_data));
            initLineChart();
            return;
        }
        JSONObject jsonObject = new JSONObject(getDeviceParamsRes.payload.temperatureCurveParams);
        Iterator<String> keys = jsonObject.keys();
        if(keys == null || !keys.hasNext()){
            ToastUtils.showLong(this,R.string.steam_curve_no_data);
            cookChart.setNoDataText(getString(R.string.steam_curve_no_data));
            initLineChart();
            return;
        }
        while (keys.hasNext()){
            String key = keys.next();
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
        initLineChart();
    }


    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        curveId = getIntent().getLongExtra(Constant.CURVE_ID,0);
        SteamOven steamOven = getSteamOven();
        if(steamOven == null){
            Toast.makeText(this,"缺少模式数据",Toast.LENGTH_LONG).show();
            return;
        }
        String promptText = getIntent().getStringExtra(Constant.CARVE_NAME);
        if(StringUtils.isNotBlank(promptText)){
            finishTv.setText(promptText);
        }
        if(curveId == 0){
            getCurveByGuid(getSteamOven().guid);
        }else{
            getCurveById();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            goHome();
        }else if(id==R.id.finish_go_home){//回到主页
            goHome();
        }else if(id==R.id.finish_save_curve){//保存曲线
            //saveCurve();
            saveCurveStep();
        }else if(id == R.id.multi_work_finish_opt){
            finishTv.setFocusable(true);
            finishTv.setFocusableInTouchMode(true);
            finishTv.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(finishTv,0);
        }
    }

    long timestamp = System.currentTimeMillis();
    private void saveCurve(){
        curveDetailRes.payload.userId = AccountInfo.getInstance().getUserString();
        curveDetailRes.payload.name = finishTv.getText().toString();
        curveDetailRes.payload.deviceGuid = getSteamOven().guid;
        curveDetailRes.payload.stepList = getCurveStepList();
        if(curveDetailRes.payload.gmtCreate != 0){
            curveDetailRes.payload.needTime = (int) ((timestamp - curveDetailRes.payload.gmtCreate)/1000) +"";
        }

        CloudHelper.saveCurveData(this,  curveDetailRes.payload, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse getCurveDetailRes) {
                saveCurveStep();
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    private void saveCurveStep(){
        curveDetailRes.payload.userId = AccountInfo.getInstance().getUserString();
        curveDetailRes.payload.name = finishTv.getText().toString();
        curveDetailRes.payload.deviceGuid = getSteamOven().guid;
        curveDetailRes.payload.stepList = getCurveStepList();
        if(curveDetailRes.payload.gmtCreate != 0){
            curveDetailRes.payload.needTime = (int) ((timestamp - curveDetailRes.payload.gmtCreate)/1000) +"";
        }
        CloudHelper.saveCurveStepData(this, curveDetailRes.payload, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                goHome();
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }



    private  List<CurveStep> getCurveStepList(){
        List<CurveStep> curveSteps = new ArrayList<>();
        if(entryList.size() <= 2){
            return curveSteps;
        }
        CurveStep startStep = new CurveStep();
        startStep.markTemp = (int) ((Entry)entryList.get(0)).getY();
        startStep.markTime = (int)((Entry)entryList.get(0)).getX() +"";
        startStep.markName = "开始工作";
        startStep.status = 1;
        curveSteps.add(startStep);

        CurveStep endStep = new CurveStep();
        endStep.markTemp = (int) ((Entry)entryList.get(0)).getY();
        endStep.markTime = (int)((Entry)entryList.get(0)).getX() +"";
        endStep.markName = "结束工作";
        endStep.status = 1;
        curveSteps.add(endStep);
        return curveSteps;
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

    private void sendCommand(boolean work){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        if(work){
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_CONTINUE);//继续工作
        }else{
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_TIME_OUT);//暂停工作
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + DIRECTIVE_OFFSET_PAUSE_CONTINUE);
    }


    //获取曲线详情
    private void getCurveById() {
        CloudHelper.getCurvebookDetail(this, curveId, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                try {
                    parserCureData(getCurveDetailRes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    protected void getCurveByGuid(final String guid) {
        //mGuid 暂时写死241
        CloudHelper.getCurveBookForDevice(this, guid, GetCurveDetailRes.class,
                new RetrofitCallback<GetCurveDetailRes>() {
                    @Override
                    public void onSuccess(GetCurveDetailRes getDeviceParamsRes) {
                        try {
                            parserCureData(getDeviceParamsRes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        //initLineChart();
                    }
                });
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
            }else if(v.getId() == R.id.tv_cancel) {//加时

            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }



}
