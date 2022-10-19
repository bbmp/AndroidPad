package com.robam.dishwasher.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.CircleProgressView;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWaherModeBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.factory.DishWasherDialogFactory;

import java.util.HashMap;
import java.util.Map;

public class WorkActivity extends DishWasherBaseActivity {
    /**
     * 进度条
     */
    private CircleProgressView cpgBar;

    private TextView tvTime;
    private TextView tvMode;
    private TextView tvModeCur;
    //当前模式
    private DishWaherModeBean modeBean = null;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        cpgBar = findViewById(R.id.progress);
        tvTime = findViewById(R.id.tv_time);
        tvMode = findViewById(R.id.tv_mode);
        tvModeCur = findViewById(R.id.tv_mode_cur);
        cpgBar.setProgress(0);
        setOnClickListener(R.id.ll_left, R.id.iv_float);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) { //当前锅
                    DishWasher dishWasher = (DishWasher) device;
                    //TODO(更新页面)
                    LogUtils.i("dishwasher . WorkActivity . "+dishWasher);
                    break;
                }
            }
        });
    }

    private int sum = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            sum += 1;
            float progress = sum * 100f / 60;
            cpgBar.setProgress(progress);
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    @Override
    protected void initData() {
        //当前模式
        if (null != getIntent())
            modeBean = (DishWaherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);

        if (null != modeBean) {
            setData(modeBean);

            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    //模式参数设置
    private void setData(DishWaherModeBean modeBean) {
        tvMode.setText(modeBean.name);
        String time = TimeUtils.secToHourMinH(modeBean.time);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTime.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            //工作结束提示
            stopWork();
        } else if (id == R.id.iv_start) {
            //暂停，开始
        }

    }

    //停止工作提示
    private void stopWork() {
        IDialog iDialog = DishWasherDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMMON_DIALOG);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void work() {
        //1、检测洗碗机门 是否关闭 absDishWasher.DoorOpenState == (short) 1 若门没有关闭，则提示

        //2、先切换洗碗机状态至开机

        //3、切换成功后，在设置洗碗机工作模式
        //DishWasherAbstractControl.getInstance().sendCommonMsg(getModelWorkParamMsg(), HomeDishWasher.getInstance().guid);

    }


    private Map<String,Object> getModelWorkParamMsg() {
       Map<String,Object> request = new HashMap<>();
            //msg.putOpt(MsgParams.UserId, getSrcUser());
//        msg.putOpt(DishWasherConstant.DishWasherWorkMode, workMode);
//        msg.putOpt(DishWasherConstant.LowerLayerWasher, bottomWasherSwitch);
//        msg.putOpt(DishWasherConstant.AutoVentilation, autoVentilation);
//        msg.putOpt(DishWasherConstant.EnhancedDrySwitch, enhancedDrySwitch);
//        msg.putOpt(DishWasherConstant.AppointmentSwitch, appointmentSwitch);
//        msg.putOpt(DishWasherConstant.AppointmentTime, appointmentTime);
        return request;
    }
}