package com.robam.dishwasher.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherWaringEnum;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.manager.AppManager;
import com.robam.dishwasher.ui.activity.WaringActivity;
import com.robam.dishwasher.util.DishWasherCommandHelper;

import java.util.Map;

public abstract class DishWasherBaseActivity extends BaseActivity {

//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }
    //是否启动童锁
    private boolean lock;
    private long lastTouchMil;
    public static final int LOCK_FLAG = 9999;

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

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        LogUtils.i("washer onCreate stack size = " + AppManager.getInstance().getActivityStackSize());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);
        LogUtils.i("washer onDestroy stack size = " + AppManager.getInstance().getActivityStackSize());
    }

    public void showRightCenter(){
        View rightCenter = findViewById(R.id.ll_right_center);
        if(rightCenter == null){
            return;
        }
        rightCenter.setVisibility(View.VISIBLE);
        rightCenter.setOnLongClickListener(v->{
            Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherChildLock);
            map.put(DishWasherConstant.StoveLock,lock?0:1);
            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,LOCK_FLAG);

            setLock(!lock);
            return true;
        });
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(lock){
            boolean isTouchAble = lockTouchArea(ev);
            //LogUtils.e("caTouch "+isTouchAble);
            if(isTouchAble){
                return super.dispatchTouchEvent(ev);
            }
            if(System.currentTimeMillis() - lastTouchMil >= 3000){
                ToastUtils.show(this,R.string.dishwasher_unlock_hint, Toast.LENGTH_LONG);
                lastTouchMil = System.currentTimeMillis();
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否为童锁可触控区域
     * @return
     */
    private boolean lockTouchArea(MotionEvent ev){
        float x = ev.getRawX();
        float y = ev.getRawY();
        View touchAbleView = findViewById(R.id.ll_right_center);
        if(touchAbleView == null){
            return false;
        }
        int[] location = new int[2];
        touchAbleView.getLocationInWindow(location);

        return x >= location[0] &&
                x <= location[0] + touchAbleView.getWidth() &&
                y >= location[1] &&
                y <= location[1] + touchAbleView.getHeight();
    }

    public void setLock(boolean lock){
        this.lock = lock;
        HomeDishWasher.getInstance().lock = lock;
        View iconView = findViewById(R.id.iv_right_center);
        View tvView = findViewById(R.id.tv_right_center);
        if(iconView == null || !(iconView instanceof ImageView) || tvView == null || !(tvView instanceof TextView)){
            return;
        }
        ((ImageView) iconView).setImageResource(lock ? R.drawable.dishwasher_screen_lock : R.drawable.dishwasher_screen_unlock);
        ((TextView) tvView).setTextColor(getResources().getColor(lock?R.color.dishwasher_lock:R.color.dishwasher_white));
    }

    /**
     * 获取当前设备实体
     * @return
     */
    protected DishWasher getCurDevice(){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                DishWasher dishWasher = (DishWasher) device;
                return dishWasher;
            }
        }
        return null;
    }

    protected boolean toWaringPage(int waringCode){
        if(waringCode != DishWasherWaringEnum.E0.getCode()){
            //跳转到告警页面
            Intent intent = new Intent(this, WaringActivity.class);
            intent.putExtra(DishWasherConstant.WARING_CODE,waringCode);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
