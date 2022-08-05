package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.ui.adapter.RvMainFunctonAdapter;

import java.util.ArrayList;
import java.util.List;

public class SimpleModeActivity extends VentilatorBaseActivity {
    private RecyclerView rvSimple;
    private RvMainFunctonAdapter rvMainFunctonAdapter;
    private List<VenFunBean> funList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_simple_mode;
    }

    @Override
    protected void initView() {
        rvSimple = findViewById(R.id.rv_simple_mode);
        rvSimple.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvSimple.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_40)));
        showCenter();
        setOnClickListener(R.id.tv_exit_simple_mode);
    }

    @Override
    protected void initData() {
        funList.add(new VenFunBean(1, "fun1", "logo_roki", "mode1", "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", "mode2", "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", "mode3", "into"));
        funList.add(new VenFunBean(1, "fun4", "logo_roki", "mode4", "into"));
        funList.add(new VenFunBean(1, "fun5", "logo_roki", "mode5", "into"));
        rvMainFunctonAdapter = new RvMainFunctonAdapter();
        rvSimple.setAdapter(rvMainFunctonAdapter);
        rvMainFunctonAdapter.setList(funList);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_exit_simple_mode)
            finish();
    }
}