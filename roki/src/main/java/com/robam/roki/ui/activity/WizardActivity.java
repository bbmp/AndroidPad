package com.robam.roki.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;

import com.robam.common.activity.BaseActivity;
import com.robam.roki.R;
import com.robam.roki.adapter.ExtPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class WizardActivity extends BaseActivity {
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_wizard;
    }

    @Override
    protected void initView() {
        pager = findViewById(R.id.pager);

    }

    @Override
    protected void initData() {
        List<View> views = buildViews();
        ExtPageAdapter adapter = new ExtPageAdapter(this, views);
        pager.setAdapter(adapter);
    }
    List<View> buildViews() {
        List<View> views = new ArrayList<View>();
        View view1 = LayoutInflater.from(this).inflate(
                R.layout.roki_view_layout_wizard, null);
        View view2 = LayoutInflater.from(this).inflate(
                R.layout.roki_view_layout_wizard, null);
        View view3 = LayoutInflater.from(this).inflate(
                R.layout.roki_view_layout_wizard, null);

        views.add(view1);
        views.add(view2);
        views.add(view3);

        Button btStart = view3.findViewById(R.id.txtStroll);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(HomeActivity.class);
            }
        });
        return views;
    }
}