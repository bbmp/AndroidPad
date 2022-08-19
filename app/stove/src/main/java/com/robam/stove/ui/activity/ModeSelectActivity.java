package com.robam.stove.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.ModeBean;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveModeEnum;
import com.robam.stove.ui.pages.ModeSelectPage;
import com.robam.stove.ui.pages.TimeSelectPage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends StoveBaseActivity implements IModeSelect {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();

    //模式选择， 和时间
    private TabLayout.Tab modeTab, timeTab;

    private List<ModeBean> modes = new ArrayList<>();

    private ModeSelectPage modeSelectPage;

    private TimeSelectPage timeSelectPage;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

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
        modes.addAll(StoveModeEnum.getModeList());
        //默认模式
        ModeBean defaultBean = modes.get(0);
        Stove.getInstance().workMode = defaultBean.code;
        modeTab = tabLayout.newTab();
        modeTab.setId(0);
        View modeView = LayoutInflater.from(getContext()).inflate(R.layout.stove_view_layout_tab_mode, null);
        TextView tvMode = modeView.findViewById(R.id.tv_mode);
        tvMode.setText(defaultBean.name);
        modeTab.setCustomView(modeView);
        tabLayout.addTab(modeTab);

        modeSelectPage = new ModeSelectPage(modeTab, modes,this);
        fragments.add(new WeakReference<>(modeSelectPage));

        timeTab = tabLayout.newTab();
        timeTab.setId(1);
        View timeView = LayoutInflater.from(getContext()).inflate(R.layout.stove_view_layout_tab_time, null);
        TextView tvTime = timeView.findViewById(R.id.tv_mode);
        tvTime.setText(defaultBean.defTime + "");
        timeTab.setCustomView(timeView);
        tabLayout.addTab(timeTab);
        timeSelectPage = new TimeSelectPage(timeTab, this);

        fragments.add(new WeakReference<>(timeSelectPage));

        //添加设置适配器
        noScrollViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        noScrollViewPager.setOffscreenPageLimit(fragments.size());
    }

    /**
     * 根据当前模式设置温度和时间
     */
    private void initTimeParams(int mode) {
        if (null != modes) {
            for (ModeBean modeBean: modes) {
                if (mode == modeBean.code) {

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