package com.robam.ventilator.ui.pages;

import android.content.Intent;
import android.serialport.helper.SerialPortHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.skin.SkinDisplayUtils;
import com.robam.common.ui.helper.CenterSnapHelper;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.ui.helper.ScaleLayoutManager;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ScreenUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.ui.activity.DateSettingActivity;
import com.robam.ventilator.ui.activity.LoginPhoneActivity;
import com.robam.ventilator.ui.activity.SimpleModeActivity;
import com.robam.ventilator.ui.activity.SmartSettingActivity;
import com.robam.ventilator.ui.activity.WifiConnectActivity;
import com.robam.ventilator.ui.activity.WifiSettingActivity;
import com.robam.ventilator.ui.adapter.RvMainFunctonAdapter;
import com.robam.ventilator.ui.adapter.RvStringAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends VentilatorBasePage {
    /**
     * 主功能
     */
    private RecyclerView recyclerView;
    private RvMainFunctonAdapter rvFunctionAdapter;
    /**
     * 系统设置
     */
    private RecyclerView rvLeft;
    /**
     * 产品中心
     */
    private RecyclerView rvRight;
    private List<VenFunBean> funList = new ArrayList<>();
    //系统设置menu
    private List<String> settingList = Arrays.asList("personal_center",
            "date_setting",
            "wifi_connect",
            "reset",
            "screen_brightness",
            "sale_service",
            "about_product",
            "smart_setting",
            "simple_mode");
    private RvStringAdapter settingAdapter;
    private TextView tvPerformance, tvComfort;
    private Group group;
    private DrawerLayout drawerLayout;
    private LinearLayout llSetting, llProducts;

    public static HomePage newInstance() {
        return new HomePage();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_home;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.rv_fun);
        tvPerformance = findViewById(R.id.tv_performance);
        tvComfort = findViewById(R.id.tv_comfort);
        group = findViewById(R.id.ventilator_group);
        drawerLayout = findViewById(R.id.drawer_layout);
        llSetting = findViewById(R.id.ll_drawer_left);
        llProducts = findViewById(R.id.ll_drawer_right);
        rvLeft = findViewById(R.id.rv_left);
        rvRight = findViewById(R.id.rv_right);

        tvPerformance.setSelected(true);

        rvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRight.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                //禁止滚动
                return false;
            }
        });
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_45)));
        showCenter();
        setOnClickListener(R.id.tv_performance, R.id.tv_comfort, R.id.ll_drawer_left, R.id.ll_drawer_right);

        //禁止手勢滑動
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //抽屉滑动式回调的方法
                LogUtils.e("x" + drawerView.getWidth());
                LogUtils.e("---onDrawerSlide---"+slideOffset);
                if (drawerView.getId() == R.id.drawer_left)
                    llSetting.setX(slideOffset * drawerView.getWidth());
                else {
                    int width = ScreenUtils.getWidthPixels(getContext());
                    llProducts.setX(width - slideOffset * drawerView.getWidth() - SkinDisplayUtils.dp2px(getContext(), 35));
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //抽屉打开时会回调的方法
                LogUtils.i("---onDrawerOpened---");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                LogUtils.i("---onDrawerClosed---");
                drawerLayout.setVisibility(View.INVISIBLE);

                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //抽屉的状态改变时会回调的方法
                LogUtils.i("---onDrawerStateChanged---");
            }
        });
    }

    @Override
    protected void initData() {
        funList.add(new VenFunBean(1, "fun1", "logo_roki", "mode1", "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", "mode2", "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", "mode3", "into"));
        funList.add(new VenFunBean(1, "fun4", "logo_roki", "mode4", "into"));
        rvFunctionAdapter = new RvMainFunctonAdapter();
        recyclerView.setAdapter(rvFunctionAdapter);
        rvFunctionAdapter.setList(funList);
        rvFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                startActivity(new Intent(getContext(), SmartSettingActivity.class));
            }
        });
        settingAdapter = new RvStringAdapter();
        rvLeft.setAdapter(settingAdapter);
        settingAdapter.setList(settingList);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_performance) {
            if (!tvPerformance.isSelected()) {
                tvPerformance.setSelected(true);
                tvComfort.setSelected(false);
                group.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_comfort) {
            if (!tvComfort.isSelected()) {
                tvPerformance.setSelected(false);
                tvComfort.setSelected(true);
                group.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_drawer_left) {
            open(Gravity.LEFT);
        } else if (id == R.id.ll_drawer_right) {
            open(Gravity.RIGHT);
        }
    }

    public void open(int gravity) {
        //判断当前
        if (drawerLayout.isDrawerOpen(gravity)) {
            drawerLayout.closeDrawer(gravity);
        }
        else {
            drawerLayout.setVisibility(View.VISIBLE);
            if (gravity == Gravity.LEFT)
                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            if (gravity == Gravity.RIGHT)
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            drawerLayout.openDrawer(gravity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
    }
}