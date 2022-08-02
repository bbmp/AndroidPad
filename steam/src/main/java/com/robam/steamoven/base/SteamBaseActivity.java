package com.robam.steamoven.base;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.base.action.BackgroundImgAction;

public abstract class SteamBaseActivity extends BaseActivity implements BackgroundImgAction {
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

    public void hideItem3() {
        if (findViewById(R.id.ll_title_item3) != null) {
            findViewById(R.id.ll_title_item3).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 隐藏title5
     */
    public void hideItem5() {
        if (findViewById(R.id.ll_title_item5) != null) {
            findViewById(R.id.ll_title_item5).setVisibility(View.INVISIBLE);
        }
    }

    @Nullable
    @Override
    public ImageView getBgImg() {
        if (obtainBgImg(getContentView()) != null) {
            return obtainBgImg(getContentView());
        }
        return null;
    }
}
