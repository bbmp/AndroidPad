package com.robam.ventilator.base;

import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.ventilator.R;

public abstract class VentilatorBasePage extends HeadPage {

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
}
