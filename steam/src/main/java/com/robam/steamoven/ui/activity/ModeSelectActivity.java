package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.IModeSelect;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamEnum;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.pages.ModeSelectPage;
import com.robam.steamoven.ui.pages.SteamSelectPage;
import com.robam.steamoven.ui.pages.TempSelectPage;
import com.robam.steamoven.ui.pages.TimeSelectPage;
import com.robam.steamoven.utils.ModelUtil;
import com.robam.steamoven.utils.SkipUtil;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ModeSelectActivity extends SteamBaseActivity implements IModeSelect {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    //弱引用，防止内存泄漏
    List<WeakReference<Fragment>> fragments = new ArrayList<>();

    //模式选择， 温度和时间
    private TabLayout.Tab modeTab, timeTab;
    //上温度 下温度
    private TabLayout.Tab upTempTab, downTempTab;
    //加湿烤
    private TabLayout.Tab steamTab;

    private List<ModeBean> modes;

    private ModeSelectPage modeSelectPage;

    private TimeSelectPage timeSelectPage;

    //上温度 下温度
    private TempSelectPage upTempSelectPage, downTempSelectPage;

    private SteamSelectPage steamSelectPage;

    private SelectPagerAdapter selectPagerAdapter;

    //是否需要设置result
    private boolean needSetResult = false;
    private MultiSegment preSegment = null;
    private ModeBean curModeBean;

    private int directive_offset = 10000000;
    private static final int DIRECTIVE_OFFSET_AUX_MODEL = 800;
    private static final int START = 11;

    private TabLayout.Tab preSelectTab = null;
    private int sectionResId;
    private TextView btStart;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        btStart = findViewById(R.id.btn_start);
        tabLayout = findViewById(R.id.tabLayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(!isClickAble(tab)){//澎湃蒸，温度不能调节
                    if(preSelectTab.getId() != tab.getId()){
                        tabLayout.selectTab(preSelectTab);
                    }
                    return;
                }
                //暂停之前的滚动
                if(preSelectTab != null && preSelectTab.getId() != tab.getId()){
                    //noScrollViewPager.setCurrentItem(preSelectTab.getId(),false);
                    WeakReference<Fragment> fragmentWeakReference = fragments.get(preSelectTab.getId());
                    Fragment fragment = fragmentWeakReference.get();
                    if(fragment instanceof SteamBasePage){
                        ViewGroup pageView = ((SteamBasePage)fragment).findViewById(R.id.rv_select);
                        RecyclerView recyclerView = pageView.findViewById(R.id.rv_select);
                        recyclerView.stopScroll();
                    }
                }
                preSelectTab = tab;
                //tab选中放大
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.tv_mode);
                textView.setScaleX(1.1f);
                textView.setScaleY(1.1f);
                ImageView imageView = view.findViewById(R.id.iv_select);
                imageView.setVisibility(View.VISIBLE);
                noScrollViewPager.setCurrentItem(tab.getId(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.tv_mode);
                textView.setScaleX(1.0f);
                textView.setScaleY(1.0f);
                ImageView imageView = view.findViewById(R.id.iv_select);
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            toWorkPage(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }
                }
            }
        });
    }


    /**
     * 去往工作页面
     * @param steamOven
     */
    private void toWorkPage(SteamOven steamOven){
        if(steamOven.mode == 0){
            return;
        }
        SkipUtil.toWorkPage(steamOven,this);
    }


    //上次显示Toast 的时间搓
    private long preShowTimeMin;
    /**
     * 判断模式对应的参数是否可调节（澎湃蒸模式下温度不能调节）
     * @param tab
     * @return
     */
    private boolean isClickAble(TabLayout.Tab tab){
        TabLayout.Tab tabAt = tabLayout.getTabAt(0);
        TextView tv = tabAt.getCustomView().findViewById(R.id.tv_mode);
        int modeCode = SteamModeEnum.matchCode(tv.getText().toString());
        boolean isClickAble = true;
        for (ModeBean modeBean: modes) {
            if (modeCode == modeBean.code) {
                int promptResId = 0;
                if (tab.getId() == 2) {
                    isClickAble = modeBean.maxTemp != modeBean.minTemp;
                    promptResId = R.string.steam_temp_prompt;
                } else if (tab.getId() == 4) {
                    isClickAble = modeBean.maxTime != modeBean.minTime;
                    promptResId = R.string.steam_time_prompt;
                }
                if(!isClickAble){
//                    if(modeBean.maxTemp == modeBean.minTemp && modeBean.maxTemp == modeBean.minTemp){
//                        promptResId = R.string.steam_temp_time_prompt;
//                    }
                    if(System.currentTimeMillis() - preShowTimeMin >= 2000){//防止用户多次点击，弹出太多的Toast
                        preShowTimeMin = System.currentTimeMillis();
                        ToastUtils.showLong(ModeSelectActivity.this,promptResId);
                    }
                }
                break;
            }
        }
        return isClickAble;
    }

    /**
     * 跳转到辅助工作页面
     */
    private void toAxuWorkPage(){
        Intent intent = new Intent(this,AuxModelWorkActivity.class);
        intent.putExtra(Constant.SEGMENT_DATA_FLAG,getResult());
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到工作页面
     */
    private void toWorkPage(){
        Intent intent = new Intent(this,ModelWorkActivity.class);
        List<MultiSegment> list = new ArrayList<>();
        list.add(getResult());
        list.get(0).setWorkModel(MultiSegment.COOK_STATE_PREHEAT);
        list.get(0).setCookState(MultiSegment.COOK_STATE_START);
        intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
        startActivity(intent);
        finish();
    }


    private int getPreCheckIndex(){
        if(modes == null || preSegment == null){
            return -1;
        }
        for(int i = 0;i < modes.size();i++){
            if(modes.get(i).code == preSegment.code){
                return i;
            }
        }
        return -1;
    }




    private ModeBean getCurModeBean(int selectIndex){
        if(selectIndex == -1){
            return modes.get(0);
        }
        ModeBean defaultBean = modes.get(selectIndex);
        defaultBean.defTemp = preSegment.defTemp;
        //defaultBean.d = preSegment.defTemp;
        defaultBean.defTime = preSegment.duration;
        defaultBean.defSteam = preSegment.steam;
        return defaultBean;
    }

    @Override
    protected void initData() {
        if (null != getIntent()){
            modes = (ArrayList<ModeBean>) getIntent().getSerializableExtra(SteamConstant.EXTRA_MODE_LIST);
        }else{
            ToastUtils.showLong(this,R.string.steam_model_select_data_prompt);
            finish();
            return;
        }
//        if(modes.get(0).funCode != SteamEnum.AUX.fun){
//           showLeftCenter();
//            setRight(R.string.steam_makeAnAppointment);
//        }
        sectionResId = getIntent().getIntExtra(Constant.SEGMENT_SECTION,-1);
        if(sectionResId != -1){
            findViewById(R.id.section_value).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.section_value)).setText(sectionResId);
        }

        needSetResult =  getIntent().getBooleanExtra(Constant.NEED_SET_RESULT,false);
        preSegment =  getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        int checkIndex = getPreCheckIndex();
        initCurModelList();
        if (null != modes && modes.size() > 0) {
            //默认模式
            ModeBean defaultBean = getCurModeBean(checkIndex);//modes.get(checkIndex != -1 ? checkIndex:0);
            curModeBean = defaultBean;
            if(!needSetResult){
                checkIndex = getModelIndex(ModelUtil.getModelCategoryRecord(curModeBean.funCode),curModeBean);
                if(checkIndex != -1){
                    defaultBean = modes.get(checkIndex);//modes.get(checkIndex != -1 ? checkIndex:0);
                    curModeBean = defaultBean;
                }
            }
            //当前模式
            HomeSteamOven.getInstance().workMode = (short) defaultBean.code;
            //模式
            modeTab = tabLayout.newTab();
            modeTab.setId(0);
            View modeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_mode, null);
            TextView tvMode = modeView.findViewById(R.id.tv_mode);
            tvMode.setText(defaultBean.name);
            modeTab.setCustomView(modeView);
            tabLayout.addTab(modeTab);
            modeSelectPage = new ModeSelectPage(modeTab, modes, this,checkIndex);

            fragments.add(new WeakReference<>(modeSelectPage));


            //蒸汽
            steamTab = tabLayout.newTab();
            steamTab.setId(1);
            View steamView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_steam, null);
            TextView tvSteam = steamView.findViewById(R.id.tv_mode);
            tvSteam.setText(SteamOvenSteamEnum.match(defaultBean.defSteam));
            steamTab.setCustomView(steamView);
            tabLayout.addTab(steamTab);
            steamSelectPage = new SteamSelectPage(steamTab, defaultBean);
            if(modes.get(0).funCode == SteamEnum.STEAM.fun){
                steamSelectPage.setSteamValue(partStreamList);
            }

            fragments.add(new WeakReference<>(steamSelectPage));

            //上温度
            upTempTab = tabLayout.newTab();
            upTempTab.setId(2);
            View upView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_temp, null);
            TextView upTemp = upView.findViewById(R.id.tv_mode);
            upTemp.setText(defaultBean.defTemp + "");
            upTempTab.setCustomView(upView);
            tabLayout.addTab(upTempTab);
            upTempSelectPage = new TempSelectPage(upTempTab, defaultBean);
            fragments.add(new WeakReference<>(upTempSelectPage));
            upTempSelectPage.setModeSelect(new IModeSelect() {
                @Override
                public void updateTab(int curTemp) {
                    if(curModeBean.code == SteamConstant.EXP && downTempSelectPage != null){
                        downTempSelectPage.updateTempTab(getDownTempMode(curModeBean,curTemp));
                    }
                }
            });

            //下温度
            ModeBean downTempMode = getDefaultTempMode(defaultBean);
            if(!needSetResult){
                ModelUtil.ModelRecord modelRecord = ModelUtil.getModelRecord(SteamConstant.EXP);
                if(modelRecord != null && modelRecord.downTemp != 0){
                    downTempMode.defTemp = modelRecord.downTemp;
                }
            }
            downTempTab = tabLayout.newTab();
            downTempTab.setId(3);
            View downView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_temp, null);
            TextView downTemp = downView.findViewById(R.id.tv_mode);
            TextView preTv = downView.findViewById(R.id.tv_mode_pre);
            preTv.setText(R.string.steam_exp_pre_down);
            downTemp.setText(downTempMode.defTemp+"");
            downTempTab.setCustomView(downView);
            tabLayout.addTab(downTempTab);
            downTempSelectPage = new TempSelectPage(downTempTab, downTempMode);
            fragments.add(new WeakReference<>(downTempSelectPage));
            //时间
            timeTab = tabLayout.newTab();
            timeTab.setId(4);
            View timeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_time, null);
            TextView tvTime = timeView.findViewById(R.id.tv_mode);
            tvTime.setText(defaultBean.defTime + "");
            timeTab.setCustomView(timeView);
            tabLayout.addTab(timeTab);
            timeSelectPage = new TimeSelectPage(timeTab, defaultBean);
            fragments.add(new WeakReference<>(timeSelectPage));
            //添加设置适配器
            selectPagerAdapter = new SelectPagerAdapter(getSupportFragmentManager());
            noScrollViewPager.setAdapter(selectPagerAdapter);
            noScrollViewPager.setOffscreenPageLimit(fragments.size());

            preSelectTab = modeTab;
            if(!needSetResult){
                setRight(R.string.steam_makeAnAppointment);
            }
            initOtherViews(defaultBean);
            initTimePrompt();
        }

//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(noScrollViewPager));
//        noScrollViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);
        if(needSetResult){
            ((TextView)findViewById(R.id.btn_start)).setText(R.string.steam_sure);
        }
        SteamOven steamOven = getSteamOven();
        if(steamOven != null){
            SteamAbstractControl.getInstance().queryAttribute(steamOven.guid);
        }
    }

    private void initCurModelList(){
        if(modes != null && !needSetResult){
            if(modes.size() > 0 && modes.get(0).funCode == 7){
                return;
            }
            for(int i = 0;i < modes.size();i++){
                ModelUtil.ModelRecord modelRecord = ModelUtil.getModelRecord(modes.get(i).code);
                if(modelRecord != null){
                    if(modelRecord.stream != 0){
                        modes.get(i).defSteam =modelRecord.stream;
                    }
                    if(modelRecord.temp != 0){
                        modes.get(i).defTemp =modelRecord.temp;
                    }
                    if(modelRecord.downTemp != 0){
                        modes.get(i).defDownTemp =modelRecord.downTemp;
                    }
                    if(modelRecord.time != 0){
                        modes.get(i).defTime =modelRecord.time;
                    }
                }
            }
        }
    }

    private ModeBean getDefaultTempMode(ModeBean defaultBean){
        ModeBean modeBean =new ModeBean();
        modeBean.defTemp = 160;
        modeBean.minTemp = 80;
        modeBean.maxTemp = 180;
        return modeBean;
    }

    private ModeBean getDownTempMode(ModeBean expMode,int curTemp){
        ModeBean modeBean =new ModeBean();
        int downMinTemp = curTemp - 20;
        int downMaxTemp = curTemp + 20;
        int downMin = downMinTemp < expMode.minTemp ? expMode.minTemp : downMinTemp;
        int dowMax = downMaxTemp > expMode.maxTemp ? expMode.maxTemp : downMaxTemp;
        modeBean.defTemp = downMaxTemp;
        modeBean.minTemp = downMin;
        modeBean.maxTemp = dowMax;
        return modeBean;
    }

    private int getModelIndex(ModelUtil.ModelRecord modelCategoryRecord, ModeBean defaultBean){
        if(modelCategoryRecord == null){
            return -1;
        }
        for(int i = 0;i < modes.size();i++){
            if(modes.get(i).code == modelCategoryRecord.modeCode){
                return  i;
            }
        }
        return -1;
    }







    @Override
    public void onClick(View view) {
        LogUtils.i("ModeSelectActivity onClick ...");
        if (R.id.ll_left == view.getId()) {
            finish();
        }else if(R.id.btn_start == view.getId()){
            if(needSetResult){
                this.startSetResult();
            }else{
                //startWork();
                if(ClickUtils.isFastClick()){
                    return;
                }
                startWorkOrAppointment();
                saveReCode();
            }
        }else if(R.id.ll_right == view.getId()){
            Intent intent = new Intent(this,AppointmentActivity.class);
            intent.putExtra(Constant.SEGMENT_DATA_FLAG,getResult());
            startActivityForResult(intent,Constant.APPOINT_CODE);
        }
    }

    private void saveReCode(){
        if(needSetResult){//多段模式不记录
            return;
        }
        if(modes.size() > 0 && modes.get(0).funCode == 7){//辅助模式不记录
            return;
        }
        MultiSegment result = getResult();
        ModelUtil.saveModelCategoryRecord(result.funCode,result.code,result.steam,result.defTemp,result.downTemp,result.duration);
        ModelUtil.saveModelRecord(result.funCode,result.code,result.steam,result.defTemp,result.downTemp,result.duration);
    }


    private void startWorkOrAppointment(){
        if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),curModeBean.code)){
            return;
        }
        String text = btStart.getText().toString();
        if(getResources().getText(R.string.steam_start_appoint).equals(text)){//预约
            startAppointment();
        }else{//开始工作
            startWork();
        }
    }

    /**
     * 发送预约指令
     */
    private void startAppointment(){
        try {
            TextView timeVt = findViewById(R.id.tv_right);
            MultiSegment multiSegment = getResult();
            multiSegment.workRemaining = (int) getAppointingTimeMin(timeVt.getText().toString()) * 60;
            if(SteamModeEnum.EXP.getMode() == multiSegment.code){
                SteamCommandHelper.sendCommandForExp(multiSegment, null,multiSegment.workRemaining,directive_offset + START);
            }else{
                SteamCommandHelper.sendAppointCommand(multiSegment,multiSegment.workRemaining,directive_offset + START);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送工作指令
     */
    private void startWork(){
        MultiSegment result = getResult();
        if(SteamModeEnum.EXP.getMode() == result.code){
            SteamCommandHelper.sendCommandForExp(result,null,0,MsgKeys.setDeviceAttribute_Req+directive_offset);
        }else{
            if(SteamModeEnum.isAuxModel(result.code)){
                SteamCommandHelper.startModelWork(result,null,DIRECTIVE_OFFSET_AUX_MODEL+directive_offset);
            }else{
                SteamCommandHelper.startModelWork(result,null,MsgKeys.setDeviceAttribute_Req+directive_offset);
            }
        }
    }

    private MultiSegment getResult(){
        MultiSegment segment = new MultiSegment();
        segment.funCode = curModeBean.funCode;
        segment.code = curModeBean.code;
        for(int i = 0;i < tabLayout.getTabCount();i++){
            if(((ViewGroup)tabLayout.getChildAt(0)).getChildAt(i).getVisibility() != View.VISIBLE){
                continue;
            }

            ViewGroup childGroup = (ViewGroup) tabLayout.getTabAt(i).getCustomView();
            TextView valueTv = childGroup.findViewById(R.id.tv_mode); //模式
            if(valueTv != null){
                String value = valueTv.getText().toString();
                //i = 0 模式名称; i = 1 蒸汽量  ;i = 2 上温度; i = 3 下温度 ; i = 4 时长
                switch (i){
                    case 0:
                        segment.model = value;
                        break;
                    case 1:
                        segment.steam = SteamOvenSteamEnum.matchValue(value);
                        break;
                    case 2:
                        segment.defTemp = Integer.parseInt(value);
                        break;
                    case 3:
                        segment.downTemp = Integer.parseInt(value);
                        break;
                    case 4:
                        segment.duration = Integer.parseInt(value);
                        break;
                    default:

                }
            }
        }
        return segment;
    }

    private void startSetResult(){
       Intent result = new Intent();
       result.putExtra(Constant.SEGMENT_DATA_FLAG,getResult());
       setResult(RESULT_OK,result);
       finish();
    }

    /**
     * 根据当前模式设置温度和时间
     */
    private void initTimeParams(int mode) {
        if (null != modes) {
            for (ModeBean modeBean: modes) {
                if (mode == modeBean.code) {  //当前模式
                    curModeBean = modeBean;
                    this.initOtherViews(curModeBean);
                    this.initStreamPageData(curModeBean);
                    //this.initAppointView(curModeBean);
                    if (mode == SteamConstant.XIANNENZHENG || mode == SteamConstant.YIYANGZHENG || mode == SteamConstant.GAOWENZHENG || mode == SteamConstant.ZHIKONGZHENG) { //蒸模式

                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        //((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                       if(curModeBean.code == SteamModeEnum.ZHIKONGZHENG.getMode()){
                           //上温度不可点击
                           steamSelectPage.updateSteamTab(modeBean);
                           ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                           ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE); //蒸汽
                       }else{
                           ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                           ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                       }

                        //若是澎湃蒸 - 需要显示蒸汽大小与时间

                    } else if (mode == SteamConstant.FENGBEIKAO || mode == SteamConstant.FENGSHANKAO || mode == SteamConstant.QIANGSHAOKAO || mode == SteamConstant.EXP
                                || mode == SteamConstant.KUAIRE || mode == SteamConstant.BEIKAO) {   //烤

                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        if(curModeBean.code == SteamModeEnum.EXP.getMode()){
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.VISIBLE); //下温度
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).findViewById(R.id.tv_mode_pre).setVisibility(View.VISIBLE);
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);  //上温度
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).findViewById(R.id.tv_mode_pre).setVisibility(View.VISIBLE);
                        }else{
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).findViewById(R.id.tv_mode_pre).setVisibility(View.GONE);
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);  //上温度
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).findViewById(R.id.tv_mode_pre).setVisibility(View.GONE);
                        }
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                    } else if (mode == SteamConstant.SHOUDONGJIASHIKAO || mode == SteamConstant.JIASHIBEIKAO || mode == SteamConstant.JIASHIFENGBEIKAO) {
                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        steamSelectPage.updateSteamTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);  //上温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE); //蒸汽
                    } else if (mode == SteamConstant.KONGQIZHA) {
                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                    } else if (mode == SteamConstant.FAJIAO || mode == SteamConstant.GANZAO
                            || mode == SteamConstant.BAOWEN || mode == SteamConstant.JIEDONG || mode == SteamConstant.QINGJIE) {
                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                    } else if (mode == SteamConstant.CHUGOU ) {
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.INVISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.INVISIBLE); //上温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                    } else if (mode == SteamConstant.SHAJUN) { //杀菌模式不能调节
                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
//                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setEnabled(false);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
//                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setEnabled(false);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE); //蒸汽
                    }

                    break;
                }
            }
        }
    }

    private void initOtherViews(ModeBean modeBean){
//        if(modeBean.rotate == 1 && !needSetResult){
//            showLeftCenter();
//            setOnClickListener(R.id.ll_left_center);
//        }else{
//            hideLeftCenter();
//        }
        if(modeBean.order == 1 && !needSetResult){
            showRight();
            TextView textView = findViewById(R.id.tv_right);
            if("预约".equals(textView.getText().toString())){
                btStart.setText(R.string.steam_start_cook);
            }else{
                btStart.setText(R.string.steam_start_appoint);
            }
        }else{
            hideRight();
            btStart.setText(R.string.steam_start_cook);
        }
        if(needSetResult){
            btStart.setText(R.string.steam_ok_btn);
        }
    }



    private String[] partStreamList = {"中","大"};
    private String[] allStreamList = {"小","中","大"};
    private void initStreamPageData(ModeBean modeBean){
        if(modeBean.funCode == SteamEnum.STEAM.fun){
            steamSelectPage.setSteamValue(partStreamList);
        }else{
            steamSelectPage.setSteamValue(allStreamList);
        }
    }

    private void initAppointView(ModeBean modeBean){
        if(modeBean.order == 1){
            TextView textView = findViewById(R.id.tv_right);
            if("预约".equals(textView.getText().toString())){
                btStart.setText(R.string.steam_start_cook);
            }else{
                btStart.setText(R.string.steam_start_appoint);
            }
        }else{
            btStart.setText(R.string.steam_start_cook);
        }
    }





    @Override
    public void updateTab(int mode) {
        if (modeTab != null) {
            //模式变更，温度和时间值也要变更
            initTimeParams(mode);
        }
    }

    class SelectPagerAdapter extends FragmentStatePagerAdapter {


        public SelectPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position).get();
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.APPOINT_CODE && resultCode == RESULT_OK){
            String result = data.getStringExtra(Constant.APPOINTMENT_RESULT);
            if(result.equals("")){
                setRight(R.string.steam_makeAnAppointment);
                btStart.setText(R.string.steam_start_cook);
            }else{
                if(result.contains("今日")){
                    result = result.substring("今日".length());
                }
                setRight(result);
                btStart.setText(R.string.steam_start_appoint);
            }

        }
    }

    /**
     * 获取预约执行时间
     * @param timeText
     * @return 预约执行时间（单位：分钟）
     * @throws ParseException
     */
    private long getAppointingTimeMin(String timeText) throws ParseException {
        String time;
        if(timeText.contains("日")){
            time = timeText.substring("次日".length()).trim()+":00";
        }else{
            time = timeText.trim()+":00";
        }
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //HH:24小时制  hh:12小时制
        String curTimeStr = dateFormat.format(curTime);
        String curTimeText = curTimeStr.substring("yyyy-MM-dd".length()).trim();
        if(time.compareTo(curTimeText) > 0){//今日
            String orderTimeStr = curTimeStr.split(" ")[0].trim() + " " + time;
            Date orderTime = dateFormat.parse(orderTimeStr);
            long timeDur = (orderTime.getTime() - curTime.getTime())/60/1000;
            if((orderTime.getTime() - curTime.getTime())% 60 != 0){
                return timeDur + 1;
            }
            return timeDur;
        }else{//次日
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(curTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            String destTime = dateFormat.format(calendar.getTime());
            String orderTimeStr = destTime.split(" ")[0].trim() + " " + time;
            Date orderTime = dateFormat.parse(orderTimeStr);
            long timeDur = (orderTime.getTime() - curTime.getTime())/60/1000;
            if((orderTime.getTime() - curTime.getTime())% 60 != 0){
                return timeDur + 1;
            }
            return timeDur;
        }
    }

    Timer timer;
    /**
     * 初始化定时器
     */
    private void initTimePrompt(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isDestroyed()){
                    btStart.post(() -> {
                        if(!isDestroyed()){
                            setAppointContent();
                        }
                    });
                }
            }
        },5000,5000);
    }

    /**
     * 更改预约显示
     */
    private void setAppointContent() {
        String btValue = btStart.getText().toString();
        if(getResources().getString(R.string.steam_start_appoint).equals(btValue)){
            TextView appointTv =  findViewById(R.id.tv_right);
            String orderTime = appointTv.getText().toString();
            if(orderTime.contains("日")){
                orderTime = orderTime.substring("今日".length());
            }
            if (DateUtil.compareTime(DateUtil.getCurrentTime(DateUtil.PATTERN), orderTime, DateUtil.PATTERN) >= 0) {
                appointTv.setText("次日"+orderTime);
            } else {
                appointTv.setText(orderTime);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer !=  null){
            timer.cancel();
        }
    }
}
