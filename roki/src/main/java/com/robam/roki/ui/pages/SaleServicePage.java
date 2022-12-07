package com.robam.roki.ui.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.robam.common.ui.HeadPage;
import com.robam.common.ui.UIService;
import com.robam.roki.R;

public class SaleServicePage extends HeadPage {
    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_saleservice;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setStateBarDrawable(getContext().getDrawable(R.drawable.roki_shape_bg_main));
        ImageView ivBack = findViewById(R.id.img_back);
        setOnClickListener(R.id.img_back);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            UIService.popBack(view);
        }
    }

}
