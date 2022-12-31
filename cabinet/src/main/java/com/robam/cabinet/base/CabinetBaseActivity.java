package com.robam.cabinet.base;

import android.content.Intent;
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
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetWaringEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.EventConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.cabinet.ui.activity.MainActivity;
import com.robam.cabinet.ui.activity.MatchNetworkActivity;
import com.robam.cabinet.ui.activity.WaringActivity;
import com.robam.cabinet.ui.dialog.LockDialog;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public abstract class CabinetBaseActivity extends BaseActivity {
    private IDialog ilockDialog;

    protected boolean lock = false;
    public static final int LOCK_FLAG = 8888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CabinetActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left);
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
        View rightCenter = findViewById(R.id.ll_right_center);
        if(rightCenter == null){
            return;
        }
        rightCenter.setVisibility(View.VISIBLE);
        rightCenter.setOnClickListener(v -> {
            LogUtils.i("rightCenter  onClick....");
            if(isPowerOff()){
                return;
            }
            if(isWaring()) {
                return;
            }
            if(lock){//本身是锁状态
                if(touchDownTimeMil != 0 && System.currentTimeMillis() - touchDownTimeMil > 1000){
                    touchDownTimeMil = System.currentTimeMillis();
                    ToastUtils.showShort(getContext(), R.string.cabinet_unlock_hint);
                }
                return;
            }
            LogUtils.i("rightCenter  onClick timeSpacing ....."+(System.currentTimeMillis() - touchDownTimeMil));
            if(touchDownTimeMil != 0 && System.currentTimeMillis() - touchDownTimeMil > 1000){
                touchDownTimeMil = System.currentTimeMillis();
                return;
            }
            touchDownTimeMil = System.currentTimeMillis();
            LogUtils.i("rightCenter  onClick lock .....");
            Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriLock_Req);
            map.put(CabinetConstant.CABINET_LOCK,1);
            CabinetCommonHelper.sendCommonMsg(map);
        });

    }

    public void setRight(String value) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(value);
    }


    /**
     * 是否已关机
     * @return
     */
    private boolean isPowerOff(){
        Cabinet cabinet = getCabinet();
        if(cabinet == null){
            ToastUtils.showLong(this,R.string.cabinet_off_line);
            return true;
        }
        if(cabinet.workMode == 0){
            ToastUtils.showLong(this,R.string.cabinet_power_off);
            return true;
        }
        return false;
    }

    private boolean isWaring(){
        Cabinet cabinet = getCabinet();
        if(cabinet == null){
            return true;
        }
        int waringCode = cabinet.faultId;
        if(waringCode != CabinetWaringEnum.E255.getCode() && CabinetWaringEnum.match(waringCode).getCode() != CabinetWaringEnum.E255.getCode()){{
            ToastUtils.showLong(this,R.string.cabinet_waring_prompt);
            return true;
        }}
        return false;
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CabinetActivityManager.getInstance().removeActivity(this);
        unLockHandler.removeCallbacks(unLockRunnable);
        unLockHandler.removeCallbacksAndMessages(null);
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
    private int mLastMotionX;
    private int mLastMotionY;
    private int TOUCH_MAX = 50;
    private Handler unLockHandler = new Handler();
    private long touchDownTimeMil;
    private final Runnable unLockRunnable = () -> {
        if(isPowerOff()){
            return;
        }
        if(isWaring()) {
            return;
        }
        LogUtils.i("rightCenter unLockRunnable .....");
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriLock_Req);
        map.put(CabinetConstant.CABINET_LOCK,0);
        CabinetCommonHelper.sendCommonMsg(map);
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            touchDownTimeMil = System.currentTimeMillis();
        }
        if(lock){
            boolean isTouchAble = lockTouchArea(ev);
            if(isTouchAble){
                int x = (int)ev.getX();
                int y = (int)ev.getY();
                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mLastMotionX = x;
                        mLastMotionY = y;
                        unLockHandler.postDelayed(unLockRunnable,2000);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Math.abs(mLastMotionX - x) > TOUCH_MAX || Math.abs(mLastMotionY - y) > TOUCH_MAX){
                            unLockHandler.removeCallbacks(unLockRunnable);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        unLockHandler.removeCallbacks(unLockRunnable);
                        break;
                }
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
        HomeCabinet.getInstance().lock = lock;
        View iconView = findViewById(R.id.iv_right_center);
        View tvView = findViewById(R.id.tv_right_center);
        if(iconView == null || !(iconView instanceof ImageView) || tvView == null || !(tvView instanceof TextView)){
            return;
        }
        ((ImageView) iconView).setImageResource(lock ? R.drawable.cabinet_screen_lock : R.drawable.cabinet_screen_unlock);
        ((TextView) tvView).setTextColor(getResources().getColor(lock?R.color.cabinet_lock:R.color.cabinet_white70));
    }

    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到告警页面
     * @param waringCode
     * @return
     */
    protected boolean toWaringPage(int waringCode){
        if(waringCode != CabinetWaringEnum.E255.getCode() && CabinetWaringEnum.match(waringCode).getCode() != CabinetWaringEnum.E255.getCode()){
            //跳转到告警页面
            Intent intent = new Intent(this, WaringActivity.class);
            intent.putExtra(Constant.WARING_CODE,waringCode);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void showWaring(int waringCode){
        if(waringCode != EventConstant.WARING_CODE_NONE){
            CabinetWaringEnum match = CabinetWaringEnum.match(waringCode);
            if(match.getCode() != CabinetWaringEnum.E255.getCode()){
                runOnUiThread(() -> {
//                    Intent intent = new Intent(CabinetBaseActivity.this, WaringActivity.class);
//                    intent.putExtra(Constant.WARING_CODE,waringCode);
//                    startActivity(intent);
                    ToastUtils.showLong(this, match.getPromptContentRes());
                });
            }
        }
    }

    public Cabinet getCabinet(){
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                return (Cabinet) device;
            }
        }
        return null;
    }

    /**
     * 提示消毒柜门是否关闭
     * @return
     */
    public boolean checkDoorState(){
        Cabinet cabinet = getCabinet();
        if(cabinet == null){
            ToastUtils.showLong(this,R.string.cabinet_off_line);
            return false;
        }
        if(cabinet.doorLock == 0){
            ToastUtils.showLong(this,R.string.cabinet_waring_e1_content);
            return false;
        }
        return true;
    }

    /**
     * 调整到离线页面
     * @return
     */
    public boolean toOffLinePage(Cabinet cabinet){
        if(isOffLine(cabinet)){
            Intent intent = new Intent();
            intent.setClass(getContext(), MatchNetworkActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    protected boolean isOffLine(Cabinet cabinet){
        return (cabinet.queryNum > 2 || cabinet.status == Device.OFFLINE) ? true :false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //startDeviceStateTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopDeviceStateTask();
    }

//    private Timer timer;
//    protected void startDeviceStateTask(){
//        if(timer != null){
//            timer.cancel();
//        }
//        timer  = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Cabinet cabinet = getCabinet();
//                toOffLinePage(cabinet);
//            }
//        },1000,5000);
//    }
//
//    public void stopDeviceStateTask(){
//        if(timer != null){
//            timer.cancel();
//        }
//    }

}
