package com.robam.roki.pages;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import com.robam.common.ui.HeadPage;
import com.robam.roki.R;
import com.robam.roki.ui.UIService;

public class SaleServicePage extends HeadPage {
    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_saleservice;
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
