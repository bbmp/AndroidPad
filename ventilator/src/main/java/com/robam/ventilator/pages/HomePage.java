package com.robam.ventilator.pages;

import com.robam.common.ui.HeadPage;
import com.robam.ventilator.R;

public class HomePage extends HeadPage {


    public static HomePage newInstance() {
        return new HomePage();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


}