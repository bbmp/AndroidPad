package com.robam.steamoven.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.ModeConstant;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.ui.pages.ModeSelectPage;
import com.robam.steamoven.ui.pages.TimeSelectPage;

import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends SteamBaseActivity implements ModeSelectPage.IModeSelect {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();

    //模式选择， 温度和时间
    private TabLayout.Tab modeTab, tempTab, timeTab;
    //加湿烤
    private TabLayout.Tab steamTab;

    private List<ModeBean> modes;

    private TimeSelectPage tempSelectPage, timeSelectPage;

    private ModeSelectPage steamFragment;

    private SelectPagerAdapter selectPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRight();

        tabLayout = findViewById(R.id.tabLayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

    }


    @Override
    protected void initData() {
        FuntionBean funtionBean = (FuntionBean) getIntent().getParcelableExtra(Constant.FUNTION_BEAN);
        //当前功能
        SteamOven.getInstance().workType = (short) funtionBean.funtionCode;
        //功能下的模式
        modes =  LitePal.where("funCode = ?", funtionBean.funtionCode + "").find(ModeBean.class);

        //
        if (null != modes && modes.size() > 0) {
            int index = 0;
            //默认模式
            ModeBean defaultBean = modes.get(0);
            modeTab = tabLayout.newTab();
            modeTab.setId(index++);
            View modeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_mode, null);
            TextView tvMode = modeView.findViewById(R.id.tv_mode);
            tvMode.setText(defaultBean.name);
            modeTab.setCustomView(modeView);
            tabLayout.addTab(modeTab);
            Fragment modeFragment = new ModeSelectPage(modeTab, modes, this);
            fragments.add(new WeakReference<>(modeFragment));

            if (SteamOven.getInstance().workType == ModeConstant.MODE_JIASHI_BAKE) {
                //加湿烤
                steamTab = tabLayout.newTab();
                steamTab.setId(index++);
                View steamView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_mode, null);

                steamTab.setCustomView(steamView);
                tabLayout.addTab(steamTab);
                steamFragment = new ModeSelectPage(steamTab, modes,this);

                fragments.add(new WeakReference<>(steamFragment));
            }

            tempTab = tabLayout.newTab();
            tempTab.setId(index++);
            View tempView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_temp, null);
            TextView tvTemp = tempView.findViewById(R.id.tv_mode);
            tvTemp.setText(defaultBean.defTemp + "");
            tempTab.setCustomView(tempView);
            tabLayout.addTab(tempTab);
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = defaultBean.minTemp; i <= defaultBean.maxTemp; i++) {
                tempList.add(i + "");
            }
            tempSelectPage = new TimeSelectPage(tempTab, 0, defaultBean);
            fragments.add(new WeakReference<>(tempSelectPage));

            timeTab = tabLayout.newTab();
            timeTab.setId(index++);
            View timeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_time, null);
            TextView tvTime = timeView.findViewById(R.id.tv_mode);
            tvTime.setText(defaultBean.defTime + "");
            timeTab.setCustomView(timeView);
            tabLayout.addTab(timeTab);
            ArrayList<String> timeList = new ArrayList<>();
            for (int i = defaultBean.minTime; i <= defaultBean.maxTime; i++) {
                timeList.add(i + "");
            }
            timeSelectPage = new TimeSelectPage(timeTab, 1, defaultBean);
            fragments.add(new WeakReference<>(timeSelectPage));
//添加设置适配器
            selectPagerAdapter = new SelectPagerAdapter(getSupportFragmentManager());
            noScrollViewPager.setAdapter(selectPagerAdapter);
            noScrollViewPager.setOffscreenPageLimit(fragments.size());

        }

//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(noScrollViewPager));
//        noScrollViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);

    }





    @Override
    public void onClick(View view) {
        if (R.id.ll_left == view.getId()) {
            finish();
        } else if (R.id.ll_title_item5 == view.getId()) {

        } else if (R.id.btn_start == view.getId()) {

        }
    }

    /**
     * 根据当前模式设置温度和时间
     */
    private void initTimeParams(int mode) {
        if (null != modes) {
            for (ModeBean modeBean: modes) {
                if (mode == modeBean.code) {

                    ArrayList<String> tempList = new ArrayList<>();
                    for (int i = modeBean.minTemp; i <= modeBean.maxTemp; i++) {
                        tempList.add(i + "");
                    }
                    tempSelectPage.setList(tempList, modeBean.defTemp - modeBean.minTemp);
                    ArrayList<String> timeList = new ArrayList<>();
                    for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
                        timeList.add(i + "");
                    }
                    timeSelectPage.setList(timeList, modeBean.defTime - modeBean.minTime);

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
            return fragments.get(position).get();
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
