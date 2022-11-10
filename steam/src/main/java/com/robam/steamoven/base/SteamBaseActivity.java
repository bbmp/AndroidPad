package com.robam.steamoven.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.ui.activity.MainActivity;
import com.robam.steamoven.ui.activity.WaringActivity;


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
        setOnClickListener(R.id.ll_left);
    }
    public void showLeftCenter() {
        findViewById(R.id.ll_left_center).setVisibility(View.VISIBLE);
        setOnClickListener(R.id.ll_left_center);
    }

    public void hideLeftCenter(){
        findViewById(R.id.ll_left_center).setVisibility(View.INVISIBLE);
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
        View rightCenter = findViewById(R.id.ll_right_center);
        if(rightCenter == null){
            return;
        }
        rightCenter.setVisibility(View.VISIBLE);
        setOnClickListener(R.id.ll_right_center);
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

    public SteamOven getSteamOven(){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                return (SteamOven) device;
            }
        }
        return null;
    }


    /**
     * 回到主页
     */
    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到告警页面
     * @param steamOven
     * @return
     */
    public boolean toWaringPage(SteamOven steamOven){
        if(steamOven.faultCode != 0){
            Intent intent = new Intent(this, WaringActivity.class);
            intent.putExtra(SteamConstant.WARING_CODE,steamOven.faultCode);
            startActivity(intent);
            return true;
        }
        return false;
    }


}
