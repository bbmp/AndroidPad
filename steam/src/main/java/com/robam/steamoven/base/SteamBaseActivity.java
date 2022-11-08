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
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.activity.MainActivity;


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
            SteamCommandHelper.sendCommand(QualityKeys.lightSwitch,-100);
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

    public SteamOven getSteamOven(){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                return (SteamOven) device;
            }
        }
        return null;
    }

    /**
     * 获取设备状态
     * @param steamOven
     * @return
     */
    public boolean checkDeviceState(SteamOven steamOven){
        if(steamOven.doorState == 1){//关闭门
            return false;
        }
        if(steamOven.waterBoxState == 0){//关闭水箱

        }
        return true;
    }

    /**
     * 回到主页
     */
    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
