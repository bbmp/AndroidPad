package com.robam.ventilator.pages;

import android.content.Intent;
import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.stream.ui.activity.MainActivity;
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
        setOnClickListener(R.id.tv_ven);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_ven)
        {
            Intent intent = new Intent();
            intent.setClass(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}