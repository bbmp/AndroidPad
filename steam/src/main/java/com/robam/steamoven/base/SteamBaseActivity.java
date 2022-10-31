package com.robam.steamoven.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.steamoven.R;

import java.util.Map;

public abstract class SteamBaseActivity extends BaseActivity {
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListener(R.id.ll_left);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }
    public void showLeftCenter() {
        findViewById(R.id.ll_left_center).setVisibility(View.VISIBLE);
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

//    public void showRightCenter() {
//        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
//    }

    public void showRightCenter(){
        View rightCenter = findViewById(R.id.ll_right_center);
        if(rightCenter == null){
            return;
        }
        rightCenter.setVisibility(View.VISIBLE);
        //setOnClickListener(R.id.ll_right_center);
        rightCenter.setOnClickListener(v->{
            //照明样式切换
        });
//        rightCenter.setOnLongClickListener(v->{
//            Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherChildLock);
//            map.put(DishWasherConstant.StoveLock,lock?0:1);
//            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,LOCK_FLAG);
//
//            setLock(!lock);
//            return true;
//        });
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }
    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        findViewById(R.id.ll_right).setVisibility(View.INVISIBLE);
    }
}
