package com.robam.ventilator.ui.pages;

import android.content.Intent;
import android.serialport.helper.SerialPortHelper;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.ui.HeadPage;
import com.robam.common.ui.helper.CenterSnapHelper;
import com.robam.common.ui.helper.ScaleLayoutManager;
import com.robam.common.ui.helper.ViewPagerLayoutManager;
import com.robam.steam.ui.activity.MainActivity;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFun;
import com.robam.ventilator.ui.adapter.RvFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends HeadPage {
    private RecyclerView recyclerView;
    private RvFunctionAdapter rvFunctionAdapter;
    private ScaleLayoutManager scaleLayoutManager;
    private CenterSnapHelper centerSnapHelper;
    private List<VenFun> funList = new ArrayList<>();

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
        scaleLayoutManager = new ScaleLayoutManager(getContext(), 10);
        centerSnapHelper = new CenterSnapHelper();
        scaleLayoutManager.setInfinite(true);
        scaleLayoutManager.setMinScale(0.6f);
        scaleLayoutManager.setMoveSpeed(0.05f * 10);

        recyclerView.setLayoutManager(scaleLayoutManager);
    }

    @Override
    protected void initData() {
        funList.add(new VenFun("fun1", ""));
        funList.add(new VenFun("fun2", ""));
        funList.add(new VenFun("fun3", ""));
        funList.add(new VenFun("fun4", ""));
        funList.add(new VenFun("fun5", ""));
        rvFunctionAdapter = new RvFunctionAdapter();
        recyclerView.setAdapter(rvFunctionAdapter);
        rvFunctionAdapter.setList(funList);
        centerSnapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
    }
}