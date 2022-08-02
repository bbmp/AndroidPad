package com.robam.steamoven.base;

import android.view.View;

import com.robam.common.ui.HeadPage;
import com.robam.steamoven.R;

public abstract class SteamBasePage extends HeadPage {
    /**
     * 隐藏title2
     */
    public void hideItem2() {
        if (findViewById(R.id.ll_title_item2) != null) {
            findViewById(R.id.ll_title_item2).setVisibility(View.INVISIBLE);
        }
//        if (findViewById(R.id.ll_title_item4) != null) {
//            findViewById(R.id.ll_title_item4).setVisibility(View.INVISIBLE);
//        }
    }
}
