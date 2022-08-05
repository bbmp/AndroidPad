package com.robam.ventilator.ui.pages;

import android.content.Intent;
import android.serialport.helper.SerialPortHelper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.skin.SkinDisplayUtils;
import com.robam.common.ui.helper.CenterSnapHelper;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.ui.helper.ScaleLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

public class HomePage extends VentilatorBasePage {
    private RecyclerView recyclerView;
    private RvMainFunctonAdapter rvFunctionAdapter;
    private List<VenFunBean> funList = new ArrayList<>();
    private TextView tvPerformance, tvComfort;
    private Group group;

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
        tvPerformance.setSelected(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_45)));
        showCenter();
        setOnClickListener(R.id.tv_performance, R.id.tv_comfort);
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
    }
}