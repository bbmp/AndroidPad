package com.robam.stove.base;

import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.stove.R;

public abstract class StoveBasePage extends HeadPage {
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }
    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }
    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
}
