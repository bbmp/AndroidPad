package com.robam.dishwasher.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.ui.adapter.RvStringAdapter;
import com.robam.dishwasher.util.DishWasherCommonHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;

import java.util.ArrayList;
import java.util.Map;

//工作预约
public class AppointmentActivity extends DishWasherBaseActivity {
    /**
     * 小时选择
     */
    private RecyclerView mHourView;
    /**
     * 分钟选择
     */
    private RecyclerView mMinuteView;
    /**
     * 小时adapter
     */
    private RvStringAdapter mHourAdapter;
    /**
     * 分钟adapter
     */
    private RvStringAdapter mMinuteAdapter;
    /**
     * layoutManager
     */
    private PickerLayoutManager mHourManager;
    private PickerLayoutManager mMinuteManager;

    /**
     * 预约时间
     * @return
     */
    private String orderTime;
    private TextView tvTime;

    //当前模式
    private DishWasherModeBean modeBean = null;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_appointment;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        //设置时间选择器
        mHourView = findViewById(R.id.rv_time_hour);
        mMinuteView = findViewById(R.id.rv_time_minute);
        tvTime = findViewById(R.id.tv_time);
        mHourAdapter = new RvStringAdapter();
        mMinuteAdapter = new RvStringAdapter();
        mHourManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setMaxItem(3)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setOrderDate();
                    }
                }).build();
        mMinuteManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setOnPickerListener((recyclerView, position) -> setOrderDate())
                .build();
        mHourView.setLayoutManager(mHourManager);
        mMinuteView.setLayoutManager(mMinuteManager);

        setOnClickListener(R.id.btn_cancel, R.id.btn_ok,R.id.ll_left);
    }

    @Override
    protected void initData() {
        // 生产小时
        ArrayList<String> hourData = new ArrayList<>(24);
        for (int i = 0; i <= 23; i++) {
            hourData.add((i < 10 ? "0" : "") + i + "");
        }

        // 生产分钟
        ArrayList<String> minuteData = new ArrayList<>(6);
        minuteData.add("00");
        minuteData.add("10");
        minuteData.add("20");
        minuteData.add("30");
        minuteData.add("40");
        minuteData.add("50");
        mHourAdapter.setList(hourData);
        mMinuteAdapter.setList(minuteData);
        mHourView.setAdapter(mHourAdapter);
        mMinuteView.setAdapter(mMinuteAdapter);
        modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);
        //默认
        setOrderDate();

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) { //当前锅
                    DishWasher dishWasher = (DishWasher) device;
                    LogUtils.e("AppointmentActivity mqtt msg arrive isWorking "+dishWasher.powerStatus);
                    switch (dishWasher.powerStatus){
                        case DishWasherConstant.WORKING:
                        case DishWasherConstant.PAUSE:
                            Intent intent = new Intent();
                            intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, modeBean);
                            intent.setClass(this, AppointingActivity.class);
                            startActivity(intent);
                            finish();
                    }
                    break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel)
            finish();
        else if (id == R.id.btn_ok) { //确认预约
            HomeDishWasher.getInstance().orderTime = orderTime;
            Map params = DishWasherCommonHelper.getModelMap(MsgKeys.setDishWasherWorkMode,(short)modeBean.code,(short) 1,DishWasherCommonHelper.getAppointingTimeMin(tvTime.getText().toString()));
            DishWasherCommonHelper.sendCommonMsg(params);
        }else if(id == R.id.ll_left){
            finish();
        }
    }


    /**
     * 设置下方提示的开始时间
     */
    private void setOrderDate() {
        String hour = mHourAdapter.getItem(mHourManager.getPickedPosition());
        String minute = mMinuteAdapter.getItem(mMinuteManager.getPickedPosition());
        orderTime = hour + ":" + minute;
        if (DateUtil.compareTime(DateUtil.getCurrentTime(DateUtil.PATTERN), orderTime, DateUtil.PATTERN) == 1) {
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint2), orderTime ));
        } else {
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint3), orderTime ));
        }
    }
}