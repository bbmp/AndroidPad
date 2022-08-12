package com.robam.dishwasher.base;

import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.dishwasher.R;

public abstract class DishWasherBasePage extends HeadPage {
    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
}
