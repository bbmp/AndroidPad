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
import com.robam.steamoven.ui.IModeSelect;
import com.robam.steamoven.ui.pages.ModeSelectPage;
import com.robam.steamoven.ui.pages.TimeSelectPage;

import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends SteamBaseActivity implements IModeSelect {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();

    //模式选择， 温度和时间
    private TabLayout.Tab modeTab, tempTab, timeTab;

    private List<ModeBean> modes;

    private TimeSelectPage tempSelectPage, timeSelectPage;

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
            //默认模式
            ModeBean defaultBean = modes.get(0);
            modeTab = tabLayout.newTab();
            modeTab.setId(0);
            View modeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_mode, null);
            TextView tvMode = modeView.findViewById(R.id.tv_mode);
            tvMode.setText(defaultBean.name);
            modeTab.setCustomView(modeView);
            tabLayout.addTab(modeTab);
            Fragment modeFragment = new ModeSelectPage(modeTab, this);
            Bundle modeBundle = new Bundle();
            ArrayList<String> modeList = new ArrayList<>();
            for (ModeBean modeBean: modes) {
                modeList.add(modeBean.name);
            }
            modeBundle.putStringArrayList("mode", modeList);
            modeFragment.setArguments(modeBundle);
            fragments.add(new WeakReference<>(modeFragment));

            tempTab = tabLayout.newTab();
            tempTab.setId(1);
            View tempView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_temp, null);
            TextView tvTemp = tempView.findViewById(R.id.tv_mode);
            tvTemp.setText(defaultBean.defTemp + "");
            tempTab.setCustomView(tempView);
            tabLayout.addTab(tempTab);
            tempSelectPage = new TimeSelectPage(tempTab, "temp", defaultBean.name,this);
//            Bundle tempBundle = new Bundle();
//            ArrayList<String> tempList = new ArrayList<>();
//            for (int i = defaultBean.minTemp; i<=defaultBean.maxTemp; i++) {
//                tempList.add(i + "");
//            }
//            tempBundle.putStringArrayList("mode", tempList);
//            tempSelectPage.setArguments(tempBundle);
            fragments.add(new WeakReference<>(tempSelectPage));

            timeTab = tabLayout.newTab();
            timeTab.setId(2);
            View timeView = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_time, null);
            TextView tvTime = timeView.findViewById(R.id.tv_mode);
            tvTime.setText(defaultBean.defTime + "");
            timeTab.setCustomView(timeView);
            tabLayout.addTab(timeTab);
            timeSelectPage = new TimeSelectPage(timeTab, "time", defaultBean.name, this);
//            Bundle timeBundle = new Bundle();
//            ArrayList<String> timeList = new ArrayList<>();
//            for (int i = defaultBean.minTime; i<=defaultBean.maxTime; i++) {
//                timeList.add(i + "");
//            }
//            timeBundle.putStringArrayList("mode", timeList);
//            timeSelectPage.setArguments(timeBundle);
            fragments.add(new WeakReference<>(timeSelectPage));
//添加设置适配器
            noScrollViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
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
    private void initTimeParams(String type, String name) {
        if (null != modes) {
            for (ModeBean modeBean: modes) {
                if (name.equals(modeBean.name)) {
                    if ("mode".equals(type) || "temp".equals(type)) {
                        ArrayList<String> tempList = new ArrayList<>();
                        for (int i = modeBean.minTemp; i <= modeBean.maxTemp; i++) {
                            tempList.add(i + "");
                        }
                        tempSelectPage.setList(tempList, modeBean.defTemp - modeBean.minTemp);
                    }
                    if ("mode".equals(type) || "time".equals(type)) {
                        ArrayList<String> timeList = new ArrayList<>();
                        for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
                            timeList.add(i + "");
                        }
                        timeSelectPage.setList(timeList, modeBean.defTime - modeBean.minTime);
                    }
                    break;
                }
            }
        }
    }


    @Override
    public void updateTab(String type, String tabString) {
        if (modeTab != null) {
            //模式变更，温度和时间值也要变更
            initTimeParams(type, tabString);
        }
    }

    class HomePagerAdapter extends FragmentStatePagerAdapter {


        public HomePagerAdapter(@NonNull FragmentManager fm) {
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
