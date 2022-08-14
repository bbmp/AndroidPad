package com.robam.steamoven.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
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
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamOvenModeEnum;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.pages.ModeSelectPage;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends SteamBaseActivity {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect2;
    /**
     * 指示器
     */
    private RecyclerView rvDot;
    /**
     * 功能
     */
    private FuntionBean funBean;
    /**
     * 指示器adapter
     */
    private RvDotAdapter rvDotAdapter;


    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    /**
     * 功能下所有模式
     */
    private List<ModeBean> modes;
    /**
     * 选中的模式
     */
    private ModeBean modeBean;
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
        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setId(0);
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab, null);
        tab1.setCustomView(view1);
        tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = tabLayout.newTab();
        tab2.setId(1);
        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab, null);
        tab2.setCustomView(view2);
        tabLayout.addTab(tab2);

        TabLayout.Tab tab3 = tabLayout.newTab();
        tab3.setId(2);
        View view3 = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab, null);
        tab3.setCustomView(view3);
        tabLayout.addTab(tab3);

        TabLayout.Tab tab4 = tabLayout.newTab();
        tab4.setId(3);
        View view4 = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab, null);
        tab4.setCustomView(view4);
        tabLayout.addTab(tab4);

        fragments.add(new ModeSelectPage());
        fragments.add(new ModeSelectPage());
        fragments.add(new ModeSelectPage());
        fragments.add(new ModeSelectPage());
//添加设置适配器
        noScrollViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
//        //把TabLayout与ViewPager关联起来
//        tabLayout.setupWithViewPager(noScrollViewPager);
        noScrollViewPager.setOffscreenPageLimit(3);

//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(noScrollViewPager));
        noScrollViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);
        tabLayout.selectTab(tab1);
    }




    @Override
    protected void initData() {
        FuntionBean funtionBean = (FuntionBean) getIntent().getParcelableExtra(Constant.FUNTION_BEAN);
        //当前模式
        SteamOven.getInstance().workMode = (short) funtionBean.funtionCode;
    }





    @Override
    public void onClick(View view) {
        if (R.id.ll_left == view.getId()) {
            finish();
        } else if (R.id.ll_title_item5 == view.getId()) {

        } else if (R.id.btn_start == view.getId()) {

        }
    }

    class HomePagerAdapter extends FragmentStatePagerAdapter {


        public HomePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
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
