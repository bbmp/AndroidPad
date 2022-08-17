package com.robam.ventilator.ui.pages;

import android.content.Intent;

import com.robam.common.ui.HeadPage;
import com.robam.common.utils.PreferenceUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.ui.activity.HomeActivity;
import com.robam.ventilator.ui.activity.WifiSettingActivity;

//这里一般作为主入口进入
public class WelcomePage extends VentilatorBasePage {
    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_welcome;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        boolean first = PreferenceUtils.getBool(getActivity(), VentilatorConstant.EXTRA_FIRST, false);
        if (!first) {
            //首次进入
            Intent intent = new Intent(getActivity(), WifiSettingActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, true);
            startActivity(intent);
            PreferenceUtils.setBool(getActivity(), VentilatorConstant.EXTRA_FIRST, true);
        } else {
            //跳转到首页
            HomeActivity.start(getActivity());
        }
    }
}
