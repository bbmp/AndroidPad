package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
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
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.IModeSelect;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamEnum;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.pages.ModeSelectPage;
import com.robam.steamoven.ui.pages.SteamSelectPage;
import com.robam.steamoven.ui.pages.TempSelectPage;
import com.robam.steamoven.ui.pages.TimeSelectPage;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    private TabLayout.Tab preSelectTab = null;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();


        tabLayout = findViewById(R.id.tabLayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(!isClickAble(tab)){
                    if(preSelectTab.getId() != tab.getId()){
                        tabLayout.selectTab(preSelectTab);
                    }
                    return;
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
        MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s - directive_offset){
                case MsgKeys.setDeviceAttribute_Req:
                    toWorkPage();
                    break;
                case DIRECTIVE_OFFSET_AUX_MODEL:
                    toAxuWorkPage();
                    break;
            }
        });


    }

    private boolean isClickAble(TabLayout.Tab tab){
        TabLayout.Tab tabAt = tabLayout.getTabAt(0);
        TextView tv = tabAt.getCustomView().findViewById(R.id.tv_mode);
        if(SteamModeEnum.matchCode(tv.getText().toString()) == SteamConstant.ZHIKONGZHENG){//澎湃蒸
            return tab.getId() != 2;
        }
        return true;
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

        if(modes.get(0).funCode != SteamEnum.AUX.fun){
            showLeftCenter();
            setRight(R.string.steam_makeAnAppointment);
        }

        needSetResult =  getIntent().getBooleanExtra(Constant.NEED_SET_RESULT,false);
        preSegment =  getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);

        int checkIndex = getPreCheckIndex();
        if (null != modes && modes.size() > 0) {
            //默认模式
            ModeBean defaultBean = getCurModeBean(checkIndex);//modes.get(checkIndex != -1 ? checkIndex:0);
            curModeBean = defaultBean;
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

            //下温度
            downTempTab = tabLayout.newTab();
            downTempTab.setId(3);
            View downView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_temp, null);
            TextView downTemp = downView.findViewById(R.id.tv_mode);
            downTemp.setText(defaultBean.defTemp + "");
            downTempTab.setCustomView(downView);
            tabLayout.addTab(downTempTab);
            downTempSelectPage = new TempSelectPage(downTempTab, defaultBean);
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



    @Override
    public void onClick(View view) {
        if (R.id.ll_left == view.getId()) {
            finish();
        }else if(R.id.btn_start == view.getId()){
            if(needSetResult){
                this.startSetResult();
            }else{
                startWork();
            }
        }else if(R.id.ll_right == view.getId()){
            Intent intent = new Intent(this,AppointmentActivity.class);
            intent.putExtra(Constant.SEGMENT_DATA_FLAG,getResult());
            startActivity(intent);
        }
    }

    private void startWork(){
        MultiSegment result = getResult();
        if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),result.code)){
            return;
        }
        if(SteamModeEnum.EXP.getMode() == result.code){
            SteamCommandHelper.sendCommandForExp(result,0,MsgKeys.setDeviceAttribute_Req+directive_offset);
        }else{
            if(SteamModeEnum.isAuxModel(result.code)){
                SteamCommandHelper.startModelWork(result,DIRECTIVE_OFFSET_AUX_MODEL+directive_offset);
            }else{
                SteamCommandHelper.startModelWork(result,MsgKeys.setDeviceAttribute_Req+directive_offset);
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
                    if (mode == SteamConstant.XIANNENZHENG || mode == SteamConstant.YIYANGZHENG || mode == SteamConstant.GAOWENZHENG || mode == SteamConstant.ZHIKONGZHENG) { //蒸模式

                        timeSelectPage.updateTimeTab(modeBean);
                        upTempSelectPage.updateTempTab(modeBean);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.VISIBLE); //时间
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                        //((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE); //上温度
                       if(curModeBean.code == SteamModeEnum.ZHIKONGZHENG.getMode()){
                           //上温度不可点击
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
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);  //上温度
                        }else{
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE); //下温度
                            ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);  //上温度
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




}
