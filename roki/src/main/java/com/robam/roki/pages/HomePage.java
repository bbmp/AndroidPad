package com.robam.roki.pages;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.HeadPage;
import com.robam.common.view.NoScrollViewPager;
import com.robam.roki.R;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends HeadPage {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_home;
    }

    @Override
    protected void initView() {

        tabLayout = findViewById(R.id.tabLayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        TabLayout.Tab deviceTab = tabLayout.newTab();

        View deviceView = LayoutInflater.from(getContext()).inflate(R.layout.roki_view_home_tab_item, null);
        ImageView deviceImage = deviceView.findViewById(R.id.imgTab);
        deviceImage.setImageResource(R.drawable.roki_home_tab_device_selector);
        TextView deviceTv = deviceView.findViewById(R.id.txtTab);
        deviceTv.setText(R.string.roki_home_tab_device);
        deviceTab.setCustomView(deviceView);
        tabLayout.addTab(deviceTab);

        TabLayout.Tab recipeTab = tabLayout.newTab();

        View recipeView = LayoutInflater.from(getContext()).inflate(R.layout.roki_view_home_tab_item, null);
        ImageView recipeImage = recipeView.findViewById(R.id.imgTab);
        recipeImage.setImageResource(R.drawable.roki_home_tab_recipe_selector);
        TextView recipeTv = recipeView.findViewById(R.id.txtTab);
        recipeTv.setText(R.string.roki_home_tab_recipe);
        recipeTab.setCustomView(recipeView);
        tabLayout.addTab(recipeTab);

        TabLayout.Tab mineTab = tabLayout.newTab();

        View mineView = LayoutInflater.from(getContext()).inflate(R.layout.roki_view_home_tab_item, null);
        ImageView mineImage = mineView.findViewById(R.id.imgTab);
        mineImage.setImageResource(R.drawable.roki_home_tab_personal_selector);
        TextView mineTv = mineView.findViewById(R.id.txtTab);
        mineTv.setText(R.string.roki_home_tab_personal);
        mineTab.setCustomView(mineView);
        tabLayout.addTab(mineTab);

        fragments.add(new HomeDevicePage());
        fragments.add(new HomeRecipePage());
        fragments.add(new HomeMinePage());
        //添加设置适配器
        noScrollViewPager.setAdapter(new HomePagerAdapter(getParentFragmentManager()));
//        //把TabLayout与ViewPager关联起来
//        tabLayout.setupWithViewPager(noScrollViewPager);
        noScrollViewPager.setOffscreenPageLimit(3);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(noScrollViewPager));
        noScrollViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        noScrollViewPager.setCurrentItem(1);
    }

    @Override
    protected void initData() {

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
