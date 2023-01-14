package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.DynamicLineChartManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveDetailRes;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamErrorDialog;
import com.robam.steamoven.ui.dialog.SteamOverTimeDialog;
import com.robam.steamoven.utils.CurveDataUtil;
import com.robam.steamoven.utils.SteamDataUtil;
import com.robam.steamoven.utils.TextSpanUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//蒸、炸、烤模式工作页面
public class ModelWorkActivity extends SteamBaseActivity {

    public static final String TAG = "ModelWorkActivity";
    private List<MultiSegment> multiSegments = new ArrayList<>();//设置段数据
    private ImageView pauseCookView;//暂停按钮
    private ImageView continueCookView;//继续烹饪按钮
    private TextView cookDurationView;
    private ViewGroup curCookInfoViewGroup;
    private LineChart cookChart;
    private DynamicLineChartManager dm;

    private TextView preHeadTv;//预热文本
    private ImageView steamIv;//加蒸汽按钮
    private TextView steamTv;//加蒸汽文本
    private int directive_offset = 11000000;//指令FLAG
    private static final int DIRECTIVE_OFFSET_END = 10;//主动结束工作
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;//继续烹饪
    private static final int DIRECTIVE_OFFSET_OVER_TIME = 40;//加时
    private static final int DIRECTIVE_OFFSET_WORK_FINISH = 60;//工作结束
    private static final int DIRECTIVE_OFFSET_NONE = -100;//其他

    private static final int DEVICE_IDLE_DUR = 1000 * 60 * 10;//页面空闲最大时长
    private static final int DEVICE_IDLE_SHOW = (int) (1000 * 60 * 0.05f);//页面空闲最大时长
    private static final int ADD_STEAM_MIN_TIME = 60 * 2;//运行时间小于2分钟，不允许加湿
    private static final int ADD_TIME_MIN_TIME = 1000 * 6;//加时最大等待时长
    private List entryList = new ArrayList<Entry>();//数据集合
    private SteamOverTimeDialog timeDialog;//加时弹窗
    private float maxYValue = 250;
    private long curveId;//曲线ID
    private long recipeId = 0;//菜谱ID ； 若菜谱ID非 0 ； 则当前工作模式来源与菜谱
    private long workTimeMS = System.currentTimeMillis();//上次工作更新视图时间毫秒值

    private boolean showDialog = false;
    private SteamCommonDialog finishDialog;//正常烹饪结束弹窗
    private boolean isInitiativeEnd = false;//是否主动结束
    private boolean showRotation = false;//展示旋转按钮
    private boolean showAddStream = false;//展示加蒸汽
    private static final int WORK_COMPLETE_CODE = 301;
    private boolean isPageHide = false;//页面是否已经不显示
    private TextView  setModelTv,setTempTv,setDurationTv,setSteamTv;
    private boolean isAddTime = false;//是否加时
    private Long addTimeMil;//加时指令发起的时间戳
    private int workMode = 0;
    private boolean isPreHeat = false;//是否当前处于预热状态

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
        steamIv = findViewById(R.id.multi_work_ic_steam);
        steamTv = findViewById(R.id.multi_work_tv_steam);


        setModelTv = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
        setSteamTv = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_steam);
        setTempTv = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
        setDurationTv = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);
        setOnClickListener(R.id.multi_work_pause,R.id.multi_work_start,R.id.multi_work_ic_steam);
        //发送指令成功监听
//        MqttDirective.getInstance().getDirective().observe(this, s -> {
//            switch (s - directive_offset){
//                case DIRECTIVE_OFFSET_END:
//                    LogUtils.e(TAG+"主动结束回到主页");
//                    //goHome();
//                    break;
//                case DIRECTIVE_OFFSET_OVER_TIME:
//                    if(timeDialog != null && timeDialog.isShow()){
//                        timeDialog.dismiss();
//                    }
//                    break;
//                case DIRECTIVE_OFFSET_WORK_FINISH:
//                    if(isPreHeat){
//                        goHome();
//                    }else{
//                        toCurveSavePage();
//                    }
//                    break;
//            }
//        });
        //设备状态监听
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    if(toRemandPage(steamOven)){
                        return;
                    }
                    if(showRotation){
                        setRotationView(steamOven.rotateSwitch == 1);
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            updateViews(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            if(isWaringAddTimeSuccess()){
                                return;
                            }
                            if(isPreHeat){
                                goHome();
                            }else{
                                toCurveSavePage();
                            }
                            break;
                    }
                }
            }
        });
    }

    /**
     * 是否菜谱烹饪
     * @return
     */
    private boolean isRecipeCooking(){
        return recipeId != 0;
    }

    private ImageView iconView;
    private TextView tvView;
    private void setRotationView(boolean rotation){
        if(iconView == null){
            iconView = findViewById(R.id.iv_left_center);
            tvView = findViewById(R.id.tv_left_center);
        }
        if(iconView == null || tvView == null){
            return;
        }
        iconView.setImageResource(rotation ? R.drawable.steam_ic_roate_checked : R.drawable.steam_ic_roate_uncheck);
        tvView.setTextColor(getResources().getColor(rotation?R.color.steam_lock:R.color.steam_white70));
    }


    /**
     * 初始化LineChart
     */
    private void initLineChart(){
        dm = new DynamicLineChartManager(cookChart, this);
        dm.setLabelCount(5, 5);
        dm.setAxisLine(true, false);
        dm.setGridLine(false, false);
        dm.setAxisMaximum(maxYValue);
        //dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
        cookChart.notifyDataSetChanged();
    }

    private Runnable runnable;
    private Handler mHandler = new Handler();
    //从0开始
    private int curTime = 0;
    private void startCreateCurve(){
        runnable = () -> {
            if(isDestroyed()){
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
        dm.initLineDataSet("烹饪曲线", getResources().getColor(R.color.steam_chart), entryList, true, false);
        cookChart.notifyDataSetChanged();
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
    }

    private int errCount  = 0;
    private Handler dataHandler = new Handler();
    protected void getCookingData(final String guid) {
        CloudHelper.getCurveBookForDevice(this, guid, GetCurveDetailRes.class,
                new RetrofitCallback<GetCurveDetailRes>() {
                    @Override
                    public void onSuccess(GetCurveDetailRes getDeviceParamsRes) {
                        dataHandler.removeCallbacksAndMessages(null);
                        if(errCount <= 2 && !isDestroyed() && (getDeviceParamsRes == null || getDeviceParamsRes.payload == null || getDeviceParamsRes.payload.temperatureCurveParams == null)){
                            errCount++;
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
            initStartTimerAndList();
            startCreateCurve();
            return;
        }

        JSONObject jsonObject = new JSONObject(getDeviceParamsRes.payload.temperatureCurveParams);
        Iterator<String> keys = jsonObject.keys();
        if(keys == null || !keys.hasNext()){
            initStartTimerAndList();
            startCreateCurve();
            return;
        }
        List<String> keyList = new LinkedList<>();
        while (keys.hasNext()){
            String key = keys.next();
            if(keyList.contains(key)){//移除相同Key
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
        /*if(entryList.size() > 500){
            entryList = entryList.subList(entryList.size() - 100,entryList.size());
        }*/
        initStartTimerAndList();
        startCreateCurve();
        LogUtils.e("ModelWork list.size "+entryList.size());

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
        }else if(entryList.size() == 1){
            SteamOven steamOven = getSteamOven();
            Entry firstEntry = (Entry) entryList.get(0);
            curTime = (int) (firstEntry.getX() + 2);
            Entry entry = new Entry(curTime, steamOven.curTemp);
            entryList.add(entry);
            curTime = 1;
        }
    }


    private void updateViews(SteamOven steamOven){
        switch (steamOven.workState){
            case SteamStateConstant.WORK_STATE_LEISURE://空闲
                if(isWaringAddTimeSuccess()){
                    return;
                }
                if(isPreHeat){
                    goHome();
                }else{
                    toCurveSavePage();
                }
                break;
            case SteamStateConstant.WORK_STATE_APPOINTMENT:
                goHome();
                break;
            case SteamStateConstant.WORK_STATE_PREHEAT:
            case SteamStateConstant.WORK_STATE_PREHEAT_PAUSE:
            case SteamStateConstant.WORK_STATE_WORKING:
            case SteamStateConstant.WORK_STATE_WORKING_PAUSE:
                workTimeMS = System.currentTimeMillis();
                if(steamOven.mode != multiSegments.get(0).code){//工作模式已切换
                    if(isWaringAddTimeSuccess()){
                        return;
                    }
                    goHome();
                    LogUtils.e(TAG+" updateViews 工作模式已切换 回到主页");
                    return;
                }
                isAddTime = false;
                dismissDialog();
                showDialog = false;
                workMode = steamOven.mode;
                updateViewsPreheat(steamOven,false,false);
                break;
            case SteamStateConstant.WORK_STATE_WORKING_FINISH:
               if(isWaringAddTimeSuccess()){
                   return;
               }
               dealWorkFinish(steamOven);
               break;
        }
    }

    private boolean isWaringAddTimeSuccess(){
        if(isAddTime && System.currentTimeMillis() - addTimeMil  <= ADD_TIME_MIN_TIME) {
            return  true;
        }
        isAddTime = false;
        return false;
    }


    /**
     * 烹饪完成
     * @param steamOven
     */
    private void dealWorkFinish(SteamOven steamOven){
        CurveDataUtil.initList((ArrayList<Entry>) entryList);
        Intent intent = new Intent(this,WorkCompleteActivity.class);
        intent.putExtra(Constant.RECIPE_ID,recipeId);
        intent.putExtra(Constant.CURVE_ID,curveId);
        intent.putExtra(Constant.WORK_MODE,workMode);
        intent.putExtra(Constant.CARVE_NAME,getCurveDefaultName(recipeId));
        startActivity(intent);//WORK_COMPLETE_CODE
    }


    /**
     *  更新视图内容
     * @param steamOven
     * @param changeViewState  是否强制更新视图状态(暂停/开始)
     * @param isPause 是否暂停状态( true - 暂停 ； false - 开始工作)
     */
    private void updateViewsPreheat(SteamOven steamOven,boolean changeViewState,boolean isPause){
        if(steamOven == null){
            return;
        }
        //LogUtils.e("ModelWork name recipeId " + steamOven.recipeId + " modelCode "+steamOven.mode + " name "+getModelName(steamOven.mode,steamOven.recipeId));
        isPreHeat = (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE);
        boolean isWorking = changeViewState ? !isPause : (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT || steamOven.workState == SteamStateConstant.WORK_STATE_WORKING);
        //蒸汽量
        pauseCookView.setVisibility(isWorking?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(isWorking?View.INVISIBLE:View.VISIBLE);
        MultiSegment segment = multiSegments.get(0);
        segment.setCookState(isWorking?MultiSegment.COOK_STATE_START:MultiSegment.COOK_STATE_PAUSE);
        if(isWorking){
            if(isPreHeat){
                //当前温度
                cookDurationView.setText(TextSpanUtil.getSpan(steamOven.curTemp,Constant.UNIT_TEMP));
            }else{
                int outTime = steamOven.restTimeH * 256 + steamOven.restTime;
                int timeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
                //preValue = timeF;
                cookDurationView.setText(TextSpanUtil.getSpan(timeF*60,Constant.UNIT_TIME_MIN));
            }
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            preHeadTv.setVisibility(isPreHeat?View.VISIBLE:View.INVISIBLE);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？
        }
        //加湿显示控制
        preHeadTv.setVisibility((isPreHeat && isWorking)?View.VISIBLE:View.INVISIBLE);
        if(segment.code == SteamConstant.EXP){
            setSteamTv.setText("上"+TextSpanUtil.getSpan(steamOven.setUpTemp,Constant.UNIT_TEMP));
            setTempTv.setText("下"+TextSpanUtil.getSpan(steamOven.setDownTemp,Constant.UNIT_TEMP));
        }else{
            setTempTv.setText(TextSpanUtil.getSpan(steamOven.setUpTemp,Constant.UNIT_TEMP));
        }
        //设置的工作时间 (秒)
        int outTime = (steamOven.restTimeH * 256 + steamOven.restTime);
        if(showAddStream){
            if(outTime <= ADD_STEAM_MIN_TIME || isPreHeat){//预热或者工作剩余时间小于两分钟，不显示加湿
                steamIv.setVisibility(View.INVISIBLE);
                steamTv.setVisibility(steamOven.steamState == 1 ? View.VISIBLE : View.INVISIBLE);
            }else{
                steamIv.setVisibility((isWorking && steamOven.steamState == 2) ? View.VISIBLE : View.INVISIBLE);
                steamTv.setVisibility(steamOven.steamState != 2 ? View.VISIBLE : View.INVISIBLE);
            }
        }
        setDurationTv.setVisibility((!isPreHeat&&isWorking)?View.INVISIBLE:View.VISIBLE);
        if(setDurationTv.getVisibility() == View.VISIBLE){
            if(isPreHeat){
                int setTime = steamOven.setTimeH * 256 + steamOven.setTime;
                int timeF = (int) Math.floor(((setTime + 59f) / 60f));//剩余工作时间
                setDurationTv.setText(TextSpanUtil.getSpan(timeF*60,Constant.UNIT_TIME_MIN));
            }else{
                int timeF = (int) Math.floor(((outTime + 59f) / 60f));//剩余工作时间
                setDurationTv.setText(TextSpanUtil.getSpan(timeF*60,Constant.UNIT_TIME_MIN));
            }
        }
    }


    @Override
    protected void initData() {
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        if(multiSegments == null || multiSegments.size() == 0){
             //Toast.makeText(this,"缺少模式数据", Toast.LENGTH_LONG).show();
            return;
        }
        initOtherViewState(multiSegments.get(0).code);
        recipeId = multiSegments.get(0).recipeId;
        workMode = multiSegments.get(0).code;
        multiSegments.get(0).workRemaining = multiSegments.get(0).workRemaining == 0 ? multiSegments.get(0).duration : multiSegments.get(0).workRemaining;
        initViewInfo(multiSegments.get(0));
        initLineChart();
        SteamOven steamOven = getSteamOven();
        if(steamOven != null){
            getCookingData(steamOven.guid);
        }
    }

    /**
     * 设置旋转烤与加湿标记
     * @param modeCode
     */
    private void initOtherViewState(int modeCode){
        if(SteamModeEnum.isNotRotation(modeCode)){ //菜谱不显示旋转烤
            showRotation = false;
            hideLeftCenter();
        }else{
            showRotation = true;
            showLeftCenter();
        }

        if(SteamModeEnum.isManuallyAddSteam(modeCode)){//菜谱不显示加蒸汽
            showAddStream = true;
        }else{
            showAddStream = false;
        }
        if(isRecipe()){
            showAddStream = false;
            showRotation = false;
            hideLeftCenter();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            showStopWorkDialog();
        }else if(id == R.id.multi_work_pause){//暂停工作
            //updateViewsPreheat(getSteamOven(),true,true);
            SteamOven steamOven = getSteamOven();
            int code = 0;
            if(steamOven != null){
                code = steamOven.mode;
            }
//            if(!SteamCommandHelper.checkSteamState(this,steamOven,code,false)){
//                return;
//            }
            SteamCommandHelper.sendWorkCtrCommand(false);
        }else if(id==R.id.multi_work_start){//继续工作
            //updateViewsPreheat(getSteamOven(),true,false);
            SteamOven steamOven = getSteamOven();
            int code = 0;
            if(steamOven != null){
                code = steamOven.mode;
            }
            if(!SteamCommandHelper.checkSteamState(this,steamOven,code,false)){
                return;
            }
            SteamCommandHelper.sendWorkCtrCommand(true);
        }else if(id == R.id.multi_work_ic_steam){//加湿控制命令
            SteamOven steamOven = getSteamOven();
            int code = 0;
            if(steamOven != null){
                code = steamOven.mode;
            }
            if(!SteamCommandHelper.checkManuallyAddSteamState(this,steamOven,code)){
                return;
            }
            SteamCommandHelper.sendSteamOrRotateCommand(QualityKeys.steamCtrl, (short) 1,DIRECTIVE_OFFSET_NONE);
        }
    }

    SteamCommonDialog steamCommonDialog;
    private void showStopWorkDialog(){
        steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                //sendEndWorkCommand();
                isInitiativeEnd = true;
                SteamCommandHelper.sendEndWorkCommand( directive_offset + DIRECTIVE_OFFSET_END);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }



    @Override
    public void goHome(){
        dismissAllDialog();
        Intent intent = new Intent(ModelWorkActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void dismissDialog(){
        if(timeDialog != null && timeDialog.isShow()){
            timeDialog.dismiss();
        }
        if(recipeWorkFinishDialog != null && recipeWorkFinishDialog.isShow()){
            recipeWorkFinishDialog.dismiss();
        }
        if(finishDialog != null && finishDialog.isShow()){
            finishDialog.dismiss();
        }

    }

    private void dismissAllDialog(){
        if(steamCommonDialog != null && steamCommonDialog.isShow()){
            steamCommonDialog.dismiss();
        }
        dismissDialog();
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
                cookDurationView.setText(TextSpanUtil.getSpan(segment.curTemp,Constant.UNIT_TEMP));
            }else{
                cookDurationView.setText(TextSpanUtil.getSpan(segment.duration,Constant.UNIT_TIME_MIN));
            }
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            preHeadTv.setVisibility(isPreHeat?View.VISIBLE:View.INVISIBLE);
        }
        steamIv.setVisibility(View.INVISIBLE);
        steamTv.setVisibility(View.INVISIBLE);
        preHeadTv.setVisibility((isPreHeat && isWorking)?View.VISIBLE:View.INVISIBLE);
        setDurationTv.setVisibility((isPreHeat && isWorking)?View.INVISIBLE:View.VISIBLE);
        //工作模式
        setModelTv.setText(getModelName(segment.code,recipeId));
        //LogUtils.e("ModelWork name recipeId " + recipeId + " modelCode "+segment.code + " name "+getModelName(segment.code,recipeId));
        //设置温度
        if(isRecipe()){
            setTempTv.setVisibility(View.GONE);
            setSteamTv.setVisibility(View.GONE);
        }else{
            setTempTv.setText(TextSpanUtil.getSpan(segment.defTemp,Constant.UNIT_TEMP));
        }
        //设置的工作时间 (秒)
        setDurationTv.setText(TextSpanUtil.getSpan(segment.duration,Constant.UNIT_TIME_MIN));
        if(segment.code == SteamConstant.EXP){
            setSteamTv.setVisibility(View.VISIBLE);
            setSteamTv.setText("上"+TextSpanUtil.getSpan(segment.defTemp,Constant.UNIT_TEMP));
            setTempTv.setText("下"+TextSpanUtil.getSpan(segment.downTemp,Constant.UNIT_TEMP));
        }else{
            setSteamTv.setVisibility(segment.steam != 0 ? View.VISIBLE:View.GONE);
            if(segment.steam != 0){
                setSteamTv.setText(SteamOvenSteamEnum.match(segment.steam)+"蒸汽");
            }
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == WORK_COMPLETE_CODE){
                //isAddTime = true;
                //addTimeMil = System.currentTimeMillis();
                //continueCreateCurve();
                return;
            }
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



    private void showWorkFinishDialog(){
        if(showDialog || (finishDialog != null && finishDialog.isShow())){
            return;
        }
        showDialog = true;
        finishDialog = new SteamCommonDialog(this);
        finishDialog.setContentText(R.string.steam_work_complete);
        finishDialog.setCancelText(R.string.steam_work_complete_add_time);
        finishDialog.setOKText(R.string.steam_common_step_complete);
        finishDialog.setListeners(v -> {
            finishDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//完成
                //切换到烹饪结束状态
                //SteamCommandHelper.sendEndWorkCommand( directive_offset + DIRECTIVE_OFFSET_WORK_FINISH);
                SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
            }else if(v.getId() == R.id.tv_cancel) {//加时
                //showOverTime = true;
                showOverTimeDialog();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        finishDialog.show();
    }

    SteamErrorDialog recipeWorkFinishDialog;

    /**
     * 菜谱工作结束弹窗
     */
    private void showRecipeWorkFinishDialog(){
        if(showDialog || (recipeWorkFinishDialog != null && recipeWorkFinishDialog.isShow())){
            return;
        }
        showDialog = true;
        recipeWorkFinishDialog = new SteamErrorDialog(this);
        recipeWorkFinishDialog.setContentText(R.string.steam_work_complete);
       // recipeWorkFinishDialog.setCancelText(R.string.steam_work_complete_add_time);
        recipeWorkFinishDialog.setOKText(R.string.steam_common_step_complete);
        recipeWorkFinishDialog.setListeners(v -> {
            recipeWorkFinishDialog.dismiss();
            if(v.getId() == R.id.tv_ok){//完成
                //切换到烹饪结束状态
                //setFinishState();
                //sendWorkFinishCommand(DIRECTIVE_OFFSET_WORK_FINISH);
                SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
            }
        },R.id.tv_ok);
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
        timeDialog.setOKText(R.string.steam_sure);
        timeDialog.setData();
        timeDialog.setCancelable(false);
        timeDialog.setListeners(v -> {

            if(v.getId() == R.id.tv_ok){//确认加时
                //发送结束请求并跳转到保存曲线界面
                //showDialog = false;
                sendOverTimeCommand(Integer.parseInt(timeDialog.getCurValue())*60);
                //continueCreateCurve();
            }else if(v.getId() == R.id.tv_cancel) {//取消
                //sendWorkFinishCommand(DIRECTIVE_OFFSET_WORK_FINISH);
                //timeDialog.dismiss();
                SteamCommandHelper.sendWorkFinishCommand(directive_offset+DIRECTIVE_OFFSET_WORK_FINISH);
                //toCurveSavePage();
            }
        },R.id.tv_cancel,R.id.tv_ok);
        timeDialog.show();
    }

    /**
     * 跳转到曲线保存界面
     */
    private void toCurveSavePage(){
        dismissAllDialog();
        Intent intent = new Intent(this,CurveSaveActivity.class);
        intent.putExtra(Constant.CURVE_ID,curveId);
        intent.putExtra(Constant.CARVE_NAME,getCurveDefaultName(recipeId));
        startActivity(intent);
        finish();
        CurveDataUtil.initList((ArrayList<Entry>) entryList);
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
    protected void onStop() {
        super.onStop();
        isPageHide = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPageHide = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
        dataHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 获取模式名称
     * @param modelCode 模式名称
     * @param recipeId 菜谱ID
     * @return
     */
    private String getModelName(int modelCode,long recipeId){
        if(recipeId != 0){
            SteamOven steamOven = getSteamOven();
            if(steamOven != null){
                return SteamDataUtil.getRecipeData(DeviceUtils.getDeviceTypeId(steamOven.guid),recipeId);
            }
        }else{
            return SteamModeEnum.match(modelCode);
        }
        return "";
    }

    /**
     * 获取曲线默认名称
     * @param recipeId
     * @return
     */
    private String getCurveDefaultName(long recipeId){
        if(multiSegments != null && multiSegments.size() >= 1){
            String modelName = getModelName(multiSegments.get(0).code, recipeId);
            if(StringUtils.isNotBlank(modelName) && recipeId == 0){
                modelName += "模式";
            }
            return modelName;
        }
        return "";
    }

    /**
     * 是否菜谱工作
     * @return
     */
    private boolean isRecipe(){
        return recipeId != 0;
    }



}
