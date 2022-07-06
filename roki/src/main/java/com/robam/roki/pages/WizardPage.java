package com.robam.roki.pages;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.robam.common.ui.HeadPage;
import com.robam.roki.R;
import com.robam.roki.adapter.ExtPageAdapter;
import com.robam.roki.ui.UIService;

import java.util.ArrayList;
import java.util.List;

public class WizardPage extends HeadPage {
    private ViewPager pager;;

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_wizard;
    }

    @Override
    protected void initView() {
        pager = findViewById(R.id.pager);
        List<View> views = buildViews();
        ExtPageAdapter adapter = new ExtPageAdapter(getContext(), views);
        pager.setAdapter(adapter);

    }

    @Override
    protected void initData() {

    }

    List<View> buildViews() {
        List<View> views = new ArrayList<View>();
        View view1 = LayoutInflater.from(getContext()).inflate(
                R.layout.roki_view_layout_wizard, null);
        View view2 = LayoutInflater.from(getContext()).inflate(
                R.layout.roki_view_layout_wizard, null);
        View view3 = LayoutInflater.from(getContext()).inflate(
                R.layout.roki_view_layout_wizard, null);

        views.add(view1);
        views.add(view2);
        views.add(view3);

        Button btStart = view3.findViewById(R.id.txtStroll);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIService.postPage(view, R.id.action_webpage);
            }
        });
        return views;
    }
}
