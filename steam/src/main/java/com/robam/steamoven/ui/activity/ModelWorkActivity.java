package com.robam.steamoven.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamOverTimeDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//蒸、炸、烤模式工作页面
public class ModelWorkActivity extends SteamBaseActivity {


    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  DATA_KEY = R.id.multi_opt;

    private ImageView pauseCookView;
    private ImageView continueCookView;
    private TextView cookDurationView;
    private ViewGroup curCookInfoViewGroup;
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
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;
    private static final int DIRECTIVE_OFFSET_OVER_TIME = 40;
    private static final int DIRECTIVE_OFFSET_WORK_FINISH = 60;


    //数据集合
    private List entryList = new ArrayList<Entry>();
    private SteamOverTimeDialog timeDialog;
    private float maxYValue = 100;
    private long curveId;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_model_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
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
                case DIRECTIVE_OFFSET_OVER_TIME:
                    //TODO(加时命令发送成功，开启加时状态)
                    break;
                case DIRECTIVE_OFFSET_WORK_FINISH:
                    toCurveSavePage();
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


    //private DynamicLineChartManager dm;
    /**
     * 初始化LineChart
     */
    private void initLineChart(){
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, false);
        dm.setAxisMaximum(maxYValue);
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
        cookChart.notifyDataSetChanged();
    }

    private Runnable runnable;
    private Handler mHandler = new Handler();
    private Handler autoFinishHandler = new Handler();
    private Runnable autoFinishRun = () -> {
        goHome();
    };
    //从0开始
    private int curTime = 0;
    private boolean isDestroy = false;
    private void continueCreateCurve(){
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

            SteamAbstractControl.getInstance().queryAttribute(steamOven.guid); //查询一体机状态
            curTime += 2;
            //tvTime.setText(DateUtil.secForMatTime3(curTime) + "min");
            Entry entry = new Entry(curTime, (float) steamOven.curTemp);
            if(maxYValue < steamOven.curTemp){
                maxYValue = steamOven.curTemp;
                dm.setAxisMaximum(maxYValue+50);
            }
            //Entry entry = new Entry(curTime, (float) Math.random()*250);
            dm.addEntry(entry, 0);
            //highlights.set(highlights.size() - 1, new Highlight(entry.getX(), entry.getY(), 0)); //标记highlight只能加最后，maskview中坐标会覆盖
            //cookChart.highlightValues(highlights.toArray(new Highlight[highlights.size()]));
            mHandler.postDelayed(runnable, 2000L);
        };
        mHandler.post(runnable);
    }

    private Handler dataHandler = new Handler();
    protected void getCookingData(final String guid) {
        //mGuid 暂时写死241
        CloudHelper.getCurveBookForDevice(this, guid, GetCurveDetailRes.class,
                new RetrofitCallback<GetCurveDetailRes>() {
                    @Override
                    public void onSuccess(GetCurveDetailRes getDeviceParamsRes) {
                        dataHandler.removeCallbacksAndMessages(null);
                        if(!isDestroyed() && (getDeviceParamsRes == null || getDeviceParamsRes.payload == null)){
                            dataHandler.postDelayed(()->{getCookingData(guid);},3000);
                            return;
                        }
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
            return;
        }

        JSONObject jsonObject = new JSONObject(getDeviceParamsRes.payload.temperatureCurveParams);
        Iterator<String> keys = jsonObject.keys();
        if(keys == null || !keys.hasNext()){
            //initLineChart();
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
        if(entryList.size() > 0){
            curTime = (int) ((Entry)entryList.get(entryList.size() -1)).getX();
        }
        //initLineChart();
        dm.setAxisMaximum(maxYValue);
        continueCreateCurve();
    }


    private void updateViews(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                updateViewsPreheat(steamOven,false,false);
                break;
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
                //showOverTimeDialog();
                showWorkFinishDialog();
                mHandler.removeCallbacks(runnable);
                mHandler.removeCallbacksAndMessages(null);
                autoFinishHandler.postDelayed(autoFinishRun,1000*60*5);
                break;
        }
    }


    /**
     *  更新视图内容
     * @param steamOven
     * @param changeViewState  是否强制更新视图状态(暂停/开始)
     * @param isPause 是否暂停状态( true - 暂停 ； false - 开始工作)
     */
    private void updateViewsPreheat(SteamOven steamOven,boolean changeViewState,boolean isPause){
        boolean isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
        boolean isWorking = changeViewState ? !isPause : (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_WORKING);
        //蒸汽量
        //val steam: Int = mIDevice.steam.toInt()
        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
        MultiSegment segment = multiSegments.get(0);
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        if(isWorking){
            if(isPreHeat){
                //当前温度
                cookDurationView.setText(getSpan(steamOven.curTemp,Constant.UNIT_TEMP));
            }else{
                int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
                int timeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
                //preValue = timeF;
                cookDurationView.setText(getSpan(timeF*60,Constant.UNIT_TIME_MIN));
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

        //工作模式
        curModel.setText(SteamModeEnum.match(steamOven.mode));
        //设置温度
        int setUpTemp = steamOven.setUpTemp;
        int setDownTemp = steamOven.setDownTemp;
        curTemp.setText(getSpan(steamOven.setUpTemp,Constant.UNIT_TEMP));
        //设置的工作时间 (秒)
        int setTime = steamOven.setTimeH * 256 + steamOven.setTime;
        curDuration.setVisibility((!isPreHeat&&isWorking)?View.INVISIBLE:View.VISIBLE);
        if(curDuration.getVisibility() == View.VISIBLE){
            if(isPreHeat){
                curDuration.setText(getSpan(setTime,Constant.UNIT_TIME_MIN));
            }else{
                int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
                int timeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
                curDuration.setText(getSpan(timeF*60,Constant.UNIT_TIME_MIN));
            }
        }

    }

    /**
     * 获取温度/时间样式
     * @param value
     * @param unit  Constant.UNIT_TIME_MIN 或 Constant.UNIT_TEMP
     * @return
     */
    private SpannableString getSpan(int value,String unit){
        if(unit.equals(Constant.UNIT_TIME_MIN)){
            String time = TimeUtils.secToHourMinUp(value);
            SpannableString spannableString = new SpannableString(time);
            int pos = time.indexOf(Constant.UNIT_TIME_H);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = time.indexOf(Constant.UNIT_TIME_MIN);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }else{
            SpannableString spannableString = new SpannableString(value+unit);
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(new RelativeSizeSpan(0.5f), (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(superscriptSpan,  (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

    }






    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        SteamOven steamOven = getSteamOven();
        if(steamOven == null){
            Toast.makeText(this,"缺少模式数据",Toast.LENGTH_LONG).show();
            return;
        }
        //TODO(“上线后去掉提示”)
        if(multiSegments.size() == 0){
            Toast.makeText(this,"缺少模式数据",Toast.LENGTH_LONG).show();
        }else{
            multiSegments.get(0).workRemaining = multiSegments.get(0).workRemaining == 0 ? multiSegments.get(0).duration : multiSegments.get(0).workRemaining;
            cookChart.setNoDataText(getResources().getString(R.string.steam_no_curve_data));
            initViewInfo(multiSegments.get(0));
        }
        getCookingData(getSteamOven().guid);
        initLineChart();
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
            updateViewsPreheat(getSteamOven(),true,true);
            sendCommand(false);
        }else if(id==R.id.multi_work_start){//继续工作
            updateViewsPreheat(getSteamOven(),true,false);
            sendCommand(true);
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
                sendEndWorkCommand();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }


    /**
     * 工作结束，发送命令
     */
    private void sendWorkFinishCommand(){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 2);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,directive_offset + DIRECTIVE_OFFSET_WORK_FINISH);
    }

    /**
     * 结束工作，发送命令
     */
    private void sendEndWorkCommand(){
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

    private void goHome(){
        if(timeDialog != null && timeDialog.isShow()){
            timeDialog.dismiss();
        }
        Intent intent = new Intent(ModelWorkActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }



    /**
     * 是否正在烹饪
     * @return
     */
    private boolean isWorking(){
        return pauseCookView.getVisibility() == View.VISIBLE;
    }


    private void initViewInfo(MultiSegment segment){
        boolean isPreHeat = segment.isPreheat();
        boolean isWorking = segment.isCooking();
        //蒸汽量
        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        if(isWorking){
            if(isPreHeat){
                //当前温度
                cookDurationView.setText(getSpan(segment.defTemp,Constant.UNIT_TEMP));
            }else{
                cookDurationView.setText(getSpan(segment.duration,Constant.UNIT_TIME_MIN));
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
        //工作模式
        curModel.setText(SteamModeEnum.match(segment.code));
        //设置温度
        curTemp.setText(getSpan(segment.defTemp,Constant.UNIT_TEMP));
        //设置的工作时间 (秒)
        curDuration.setText(getSpan(segment.duration,Constant.UNIT_TIME_MIN));
    }


//    private void setOptViewsState(boolean isWorking,boolean isPreHeat,int value){
//        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
//        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
//        MultiSegment segment = multiSegments.get(0);
//        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
//        if(isWorking){
//            if(isPreHeat){
//                //TODO(替换成预热当前温度)
//                cookDurationView.setText(getSpan(value,Constant.UNIT_TEMP));
//            }else{
//                cookDurationView.setText(getSpan(value,Constant.UNIT_TIME_MIN));
//            }
//        }else{
//            if(!isPreHeat){//显示湿度控制View
//                //TODO(获取是否处于加湿状态)
//                steamIv.setVisibility(View.VISIBLE);
//                steamTv.setVisibility(View.INVISIBLE);
//            }
//            cookDurationView.setText(R.string.steam_cook_in_pause);
//            preHeadTv.setVisibility(isPreHeat?View.VISIBLE:View.INVISIBLE);
//            //设置当前段工作信息 - 后面的时间是否为剩余时长？
//        }
//        preHeadTv.setVisibility((isPreHeat && isWorking)?View.VISIBLE:View.INVISIBLE);
//        TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
//        TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
//        TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);
//
//        curDuration.setVisibility((isPreHeat && isWorking)?View.INVISIBLE:View.VISIBLE);
//        curModel.setText(segment.model);
//        curTemp.setText(segment.defTemp+"");
//        curDuration.setText(segment.duration+"");
//    }

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

    SteamCommonDialog finishDialog;
    private boolean showOverTime = false;
    private void showWorkFinishDialog(){
        if(showOverTime || (finishDialog != null && finishDialog.isShow())){
            return;
        }
        finishDialog = new SteamCommonDialog(this);
        finishDialog.setCancelText(R.string.steam_work_complete_add_time);
        finishDialog.setContentText(R.string.steam_work_complete);
        finishDialog.setOKText(R.string.steam_common_step_complete);
        finishDialog.setListeners(v -> {
            finishDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//完成
                //切换到烹饪结束状态
                //setFinishState();
                sendWorkFinishCommand();
            }else if(v.getId() == R.id.tv_cancel) {//加时
                showOverTime = true;
                showOverTimeDialog();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        finishDialog.show();
    }




    /**
     * 展示加时弹窗
     */
    private void showOverTimeDialog(){
        if(timeDialog != null && timeDialog.isShow()){
            return;
        }
        timeDialog = new SteamOverTimeDialog(this);
        timeDialog.setContentText(R.string.steam_work_complete_add_time);
        timeDialog.setOKText(R.string.steam_work_complete_complete);
        timeDialog.setData();
        timeDialog.setListeners(v -> {
            timeDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//确认加时
                //发送结束请求并跳转到保存曲线界面
                showOverTime = false;
                sendOverTimeCommand(2*60);//TODO(先固定传递两分钟)
                continueCreateCurve();
            }else if(v.getId() == R.id.tv_cancel) {//取消
                sendWorkFinishCommand();
                //toCurveSavePage();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        timeDialog.show();
    }

    /**
     * 跳转到曲线保存界面
     */
    private void toCurveSavePage(){
        Intent intent = new Intent(this,CurveSaveActivity.class);
        intent.putExtra(Constant.CURVE_ID,curveId);
        startActivity(intent);
        finish();
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

    /**
     * 发送加时指令，单位秒
     * @param overTime
     */
    private void sendOverTimeCommand(int overTime){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 3);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_8) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, 1);


        commonMap.put(SteamConstant.addExtraTimeCtrlKey, 32);
        commonMap.put(SteamConstant.addExtraTimeCtrlLength, 1);
        commonMap.put(SteamConstant.addExtraTimeCtrl, overTime);

        if (overTime<=255){
            commonMap.put(SteamConstant.recipeSetMinutes, overTime);
        }else{
            commonMap.put(SteamConstant.addExtraTimeCtrlLength, 2);
            short time = (short)(overTime & 0xff);
            commonMap.put(SteamConstant.addExtraTimeCtrl, time);
            short highTime = (short) ((overTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.addExtraTimeCtrl1, highTime);
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,DIRECTIVE_OFFSET_OVER_TIME+directive_offset);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
        autoFinishHandler.removeCallbacks(autoFinishRun);
        autoFinishHandler.removeCallbacksAndMessages(null);
    }
}
