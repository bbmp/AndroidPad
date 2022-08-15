package com.robam.dishwasher.base;

import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.dishwasher.R;

public abstract class DishWasherBasePage extends HeadPage {
    public void showFloat() {
        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
}
