package com.robam.stove.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.dialog.IDialog;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.ModeBean;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.pages.ModeSelectPage;
import com.robam.stove.ui.pages.TempSelectPage;
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
    private TabLayout.Tab modeTab, timeTab, tempTab;

    private List<ModeBean> modes = new ArrayList<>();

    private ModeSelectPage modeSelectPage;

    private TimeSelectPage timeSelectPage;
    private TempSelectPage tempSelectPage;

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

        setOnClickListener(R.id.btn_start);
    }

    @Override
    protected void initData() {
        //当前功能的模式
        List<ModeBean> modeBeans = Stove.getInstance().getModeBeans(Stove.getInstance().funCode);
        if (null != modeBeans) {
            modes.addAll(modeBeans);
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

            modeSelectPage = new ModeSelectPage(modeTab, modes, this);
            fragments.add(new WeakReference<>(modeSelectPage));
            //时间tab
            timeTab = tabLayout.newTab();
            timeTab.setId(1);
            View timeView = LayoutInflater.from(getContext()).inflate(R.layout.stove_view_layout_tab_time, null);
            TextView tvTime = timeView.findViewById(R.id.tv_mode);
            tvTime.setText(defaultBean.defTime + "");
            timeTab.setCustomView(timeView);
            tabLayout.addTab(timeTab);
            timeSelectPage = new TimeSelectPage(timeTab, this);

            fragments.add(new WeakReference<>(timeSelectPage));
            //温度tab
            tempTab = tabLayout.newTab();
            tempTab.setId(2);
            View tempView = LayoutInflater.from(getContext()).inflate(R.layout.stove_view_layout_tab_temp, null);
            TextView tvTemp = tempView.findViewById(R.id.tv_mode);
            tvTemp.setText(defaultBean.defTemp + "");
            tempTab.setCustomView(tempView);
            tabLayout.addTab(tempTab);
            tempSelectPage = new TempSelectPage(tempTab,  this);
            fragments.add(new WeakReference<>(tempSelectPage));

            //添加设置适配器
            noScrollViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
            noScrollViewPager.setOffscreenPageLimit(fragments.size());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_start) {
            //选择炉头
            selectStove();
        }
    }

    //炉头选择
    private void selectStove() {
        //炉头选择提示
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.view_left || id == R.id.view_right)
                    openFire();
            }
        }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        iDialog.show();
    }

    //点火提示
    private void openFire() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
        iDialog.setCancelable(false);
        iDialog.show();
    }

    /**
     * 根据当前模式设置温度和时间
     */
    private void initTimeParams(int mode) {
        if (null != modes) {
            for (ModeBean modeBean: modes) {
                if (mode == modeBean.code) {
                    if (mode != StoveConstant.MODE_FRY) {
                        ArrayList<String> timeList = new ArrayList<>();
                        for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
                            timeList.add(i + "");
                        }
                        timeSelectPage.setTimeList(timeList, modeBean.defTime - modeBean.minTime);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
                    } else {

                        ArrayList<String> tempList = new ArrayList<>();
                        for (int i = modeBean.minTemp; i <= modeBean.maxTemp; i++) {
                            tempList.add(i + "");
                        }
                        tempSelectPage.setTempList(tempList, modeBean.defTemp - modeBean.minTemp);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);
                        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
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