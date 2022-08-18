package com.robam.steamoven.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.robam.steamoven.ui.pages.RecipeClassifyPage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//一体机菜谱
public class RecipeActivity extends SteamBaseActivity {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    //分类
    private List<String> classifyList = new ArrayList<>();
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        tabLayout = findViewById(R.id.tablayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                noScrollViewPager.setCurrentItem(tab.getId(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initData() {
//for test
        classifyList.add("肉禽");
        classifyList.add("水产品");
        classifyList.add("主食");
        classifyList.add("甜品");
        classifyList.add("果蔬");
        classifyList.add("牛奶");
        classifyList.add("肉禽");
        classifyList.add("肉禽");
        classifyList.add("肉禽");
        classifyList.add("肉禽");
        for (int i = 0; i< classifyList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setId(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab, null);
            TextView classify = view.findViewById(R.id.tv_classify);
            classify.setText(classifyList.get(i));
            tab.setCustomView(view);
            tabLayout.addTab(tab);

            Fragment recipeClassifyPage = new RecipeClassifyPage();
            Bundle bundle = new Bundle();
            bundle.putInt("classify", i);
            recipeClassifyPage.setArguments(bundle);
            fragments.add(new WeakReference<>(recipeClassifyPage));
        }
        //添加设置适配器
        noScrollViewPager.setAdapter(new RecipeClassifyPagerAdapter(getSupportFragmentManager()));

        noScrollViewPager.setOffscreenPageLimit(classifyList.size());
    }
    class RecipeClassifyPagerAdapter extends FragmentStatePagerAdapter {


        public RecipeClassifyPagerAdapter(@NonNull FragmentManager fm) {
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
