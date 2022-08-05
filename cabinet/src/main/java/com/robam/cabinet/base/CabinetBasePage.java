package com.robam.cabinet.base;

import android.view.View;

import com.robam.cabinet.R;
import com.robam.common.ui.HeadPage;

public abstract class CabinetBasePage extends HeadPage {

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }
    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }

}
