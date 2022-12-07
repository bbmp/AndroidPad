package com.robam.cabinet.base;

import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.Observer;

import com.robam.cabinet.R;
import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.HeadPage;

public abstract class CabinetBasePage extends HeadPage {

//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }
    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }
    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        ImageView ivWifi = findViewById(R.id.iv_center);
        //监听网络连接状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ivWifi.setVisibility(View.VISIBLE);
                else
                    ivWifi.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showRightCenter(){


        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
        //监听网络连接状态

    }

}
