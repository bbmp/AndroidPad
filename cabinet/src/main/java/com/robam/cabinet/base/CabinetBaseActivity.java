package com.robam.cabinet.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.cabinet.R;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.cabinet.ui.dialog.LockDialog;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;

import java.util.Map;

public abstract class CabinetBaseActivity extends BaseActivity {
    private IDialog ilockDialog;

    protected boolean lock = false;
    public static final int LOCK_FLAG = 8888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CabinetActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left, R.id.ll_right_center);
    }
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
    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right_center) {//童锁处理
            //ToastUtils.show(getContext(),"解锁了",Toast.LENGTH_SHORT);
            //判断当前模式 ：该解锁还是上锁
            Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriLock_Req);
            map.put(CabinetConstant.SteriLock,lock?0:1);
            CabinetCommonHelper.sendCommonMsgForLiveData(map,LOCK_FLAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CabinetActivityManager.getInstance().removeActivity(this);
    }

    //锁屏
    private void screenLock() {
        TextView tvRightCenter = findViewById(R.id.tv_right_center);
        tvRightCenter.setTextColor(getResources().getColor(R.color.cabinet_lock));
        ImageView ivRightCenter = findViewById(R.id.iv_right_center);
        ivRightCenter.setImageResource(R.drawable.cabinet_screen_lock);
        if (null == ilockDialog) {
            ilockDialog = new LockDialog(this);
            ilockDialog.setCancelable(false);
            ilockDialog.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort(getContext(), R.string.cabinet_unlock_hint);
                }
            });
            LinearLayout linearLayout = ilockDialog.getRootView().findViewById(R.id.ll_right_center);
            ClickUtils.setLongClick(new Handler(), linearLayout, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ilockDialog.dismiss();
                    //解锁
                    tvRightCenter.setTextColor(getResources().getColor(R.color.cabinet_white));
                    ivRightCenter.setImageResource(R.drawable.cabinet_screen_unlock);
                    return true;
                }
            });
        }

        ilockDialog.show();
    }


    private long lastTouchMil;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(lock){
            boolean isTouchAble = lockTouchArea(ev);
            LogUtils.e("caTouch "+isTouchAble);
            if(isTouchAble){
                return super.dispatchTouchEvent(ev);
            }
            if(System.currentTimeMillis() - lastTouchMil >= 3000){
                ToastUtils.show(this,R.string.cabinet_unlock_hint, Toast.LENGTH_LONG);
                lastTouchMil = System.currentTimeMillis();
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否为童锁可相应区域
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

        if(x >= location[0] &&
                x <= location[0] + touchAbleView.getWidth() &&
           y >= location[1] &&
           y <= location[1] + touchAbleView.getHeight()){
            return true;
        }
        return false;
    }

    public void setLock(boolean lock){
        this.lock = lock;
        View iconView = findViewById(R.id.iv_right_center);
        View tvView = findViewById(R.id.tv_right_center);
        if(iconView == null || !(iconView instanceof ImageView) || tvView == null || !(tvView instanceof TextView)){
            return;
        }
        HomeCabinet.getInstance().lock = lock;
        ((ImageView) iconView).setImageResource(lock ? R.drawable.cabinet_screen_lock : R.drawable.cabinet_screen_unlock);
        ((TextView) tvView).setTextColor(getResources().getColor(lock?R.color.cabinet_lock:R.color.cabinet_white));
    }


}
