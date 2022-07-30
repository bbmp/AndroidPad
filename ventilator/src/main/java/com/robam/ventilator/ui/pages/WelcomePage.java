package com.robam.ventilator.ui.pages;

import android.content.Intent;

import com.robam.common.ui.HeadPage;
import com.robam.ventilator.R;
import com.robam.ventilator.ui.activity.HomeActivity;

//这里一般作为主入口进入
public class WelcomePage extends HeadPage {
    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_welcome;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //跳转到首页
        HomeActivity.start(getActivity());

    }
}
